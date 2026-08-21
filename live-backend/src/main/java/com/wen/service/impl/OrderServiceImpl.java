package com.wen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.GoodStatusEnum;
import com.wen.common.enums.OrderStatusEnum;
import com.wen.common.enums.PayTypeEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.response.PageResult;
import com.wen.mapper.AddressMapper;
import com.wen.mapper.GoodMapper;
import com.wen.mapper.OrderMapper;
import com.wen.mapper.UserMapper;
import com.wen.model.dto.OrderDto;
import com.wen.model.entity.AddressEntity;
import com.wen.model.entity.GoodEntity;
import com.wen.model.entity.OrderEntity;
import com.wen.model.entity.UserEntity;
import com.wen.model.vo.OrderCreateRequest;
import com.wen.model.vo.OrderQueryRequest;
import com.wen.model.vo.OrderIdRequest;
import com.wen.model.vo.OrderUpdateRequest;
import com.wen.service.OrderService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单服务实现
 *
 * @author jwruan
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final GoodMapper goodMapper;

    private final AddressMapper addressMapper;

    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderCreateRequest request) {
        // 1. 查询商品并校验可购买
        GoodEntity good = goodMapper.selectById(request.getGoodId());
        if (good == null || DeleteEnum.DELETED.getCode() == good.getDeleted()) {
            throw new BusinessException("商品不存在");
        }
        if (GoodStatusEnum.NOT_LISTED.getCode() == good.getStatus()) {
            throw new BusinessException("商品已下架，无法购买");
        }
        Long userId = UserInfoContext.getUserId();
        // 2. 查询地址并校验归属，随后快照收货信息
        AddressEntity address = addressMapper.selectById(request.getAddressId());
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException("收货地址不存在");
        }
        // 3. 原子扣减库存，防超卖；影响行数为 0 说明库存不足
        int rows = goodMapper.reduceStock(request.getGoodId(), request.getQuantity(), System.currentTimeMillis());
        if (rows == 0) {
            throw new BusinessException("库存不足");
        }
        goodMapper.markOutOfStockIfEmpty(request.getGoodId(), System.currentTimeMillis());
        // 4. 后端计算订单金额，落库
        BigDecimal orderAmount = good.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setGoodId(good.getId());
        order.setRoomId(good.getRoomId());
        order.setQuantity(request.getQuantity());
        order.setAmount(orderAmount);
        order.setStatus(OrderStatusEnum.UNPAID.getCode());
        order.setPayType(request.getPayType());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getFullAddress());
        order.setRemark(request.getRemark());
        order.setDeleted(DeleteEnum.ACTIVE.getCode());
        orderMapper.insert(order);
        log.info("下单成功: orderNo={}, userId={}, goodId={}", order.getOrderId(), userId, good.getId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(OrderUpdateRequest request) {
        checkOrderInfo(request.getOrderId());
        if (request.getStatus() != null && OrderStatusEnum.of(request.getStatus()) == null) {
            throw new BusinessException("订单状态不合法");
        }
        if (request.getPayType() != null && PayTypeEnum.of(request.getPayType()) == null) {
            throw new BusinessException("支付方式不合法");
        }

        LambdaUpdateWrapper<OrderEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderEntity::getOrderId, request.getOrderId());
        if (request.getStatus() != null) {
            wrapper.set(OrderEntity::getStatus, request.getStatus());
            // 状态流转为已支付时记录支付时间
            if (request.getStatus() == OrderStatusEnum.PAID.getCode()) {
                wrapper.set(OrderEntity::getPayTime, System.currentTimeMillis());
            }
        }
        if (request.getPayType() != null) {
            wrapper.set(OrderEntity::getPayType, request.getPayType());
        }
        if (StrUtil.isNotBlank(request.getRemark())) {
            wrapper.set(OrderEntity::getRemark, request.getRemark());
        }
        wrapper.set(OrderEntity::getUpdateTime, System.currentTimeMillis());
        orderMapper.update(null, wrapper);

        log.info("更新订单成功: orderId={}", request.getOrderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(OrderIdRequest request) {
        checkOrderInfo(request.getOrderId());
        LambdaUpdateWrapper<OrderEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderEntity::getOrderId, request.getOrderId())
                .set(OrderEntity::getDeleted, DeleteEnum.DELETED.getCode())
                .set(OrderEntity::getUpdateTime, System.currentTimeMillis());
        orderMapper.update(null, wrapper);
        log.info("删除订单成功: orderId={}", request.getOrderId());
    }

    @Override
    public PageResult<OrderDto> queryOrders(OrderQueryRequest request) {
        long pageNum = request.getPageNum();
        long pageSize = request.getPageSize();

        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEntity::getDeleted, DeleteEnum.ACTIVE.getCode())
                .eq(request.getPayType() != null, OrderEntity::getPayType, request.getPayType())
                .eq(request.getStatus() != null, OrderEntity::getStatus, request.getStatus())
                .ge(request.getStartTime() != null, OrderEntity::getCreateTime, request.getStartTime())
                .le(request.getEndTime() != null, OrderEntity::getCreateTime, request.getEndTime())
                .orderByDesc(OrderEntity::getCreateTime);

        // 订单表只存 userId，按手机号筛选需先反查用户，避免手写 join
        if (StrUtil.isNotBlank(request.getPhone())) {
            List<Long> userIds = userMapper.selectList(new LambdaQueryWrapper<UserEntity>()
                            .like(UserEntity::getPhone, request.getPhone()))
                    .stream().map(UserEntity::getUserId).toList();
            if (userIds.isEmpty()) {
                return PageResult.of(List.of(), 0, pageNum, pageSize);
            }
            wrapper.in(OrderEntity::getUserId, userIds);
        }

        // 订单表只存 goodId，按商品名筛选需先反查商品
        if (StrUtil.isNotBlank(request.getGoodName())) {
            List<Long> goodIds = goodMapper.selectList(new LambdaQueryWrapper<GoodEntity>()
                            .like(GoodEntity::getName, request.getGoodName()))
                    .stream().map(GoodEntity::getId).toList();
            if (goodIds.isEmpty()) {
                return PageResult.of(List.of(), 0, pageNum, pageSize);
            }
            wrapper.in(OrderEntity::getGoodId, goodIds);
        }

        Page<OrderEntity> page = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<OrderDto> records = page.getRecords().stream().map(this::buildOrderDto).toList();
        return PageResult.of(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 校验订单存在且未删除
     */
    private void checkOrderInfo(Long orderId) {
        OrderEntity order = orderMapper.selectOne(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderId, orderId)
                .eq(OrderEntity::getDeleted, DeleteEnum.ACTIVE.getCode()));
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
    }

    private OrderDto buildOrderDto(OrderEntity order) {
        OrderDto dto = new OrderDto();
        BeanUtil.copyProperties(order, dto);
        return dto;
    }

}

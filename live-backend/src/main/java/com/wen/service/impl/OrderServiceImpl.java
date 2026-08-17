package com.wen.service.impl;

import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.GoodStatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.mapper.AddressMapper;
import com.wen.mapper.GoodMapper;
import com.wen.mapper.OrderMapper;
import com.wen.model.entity.AddressEntity;
import com.wen.model.entity.GoodEntity;
import com.wen.model.entity.OrderEntity;
import com.wen.model.vo.OrderCreateRequest;
import com.wen.model.vo.OrderQueryRequest;
import com.wen.service.OrderService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final int ORDER_STATUS_UNPAID = 0;

    private final OrderMapper orderMapper;

    private final GoodMapper goodMapper;

    private final AddressMapper addressMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(OrderCreateRequest request) {
        Long userId = currentUserId();
        if (request.getAddressId() == null || request.getGoodId() == null || request.getQuantity() == null) {
            throw new BusinessException("下单参数不能为空");
        }
        if (request.getQuantity() <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }

        // 1. 查询商品并校验可购买
        GoodEntity good = goodMapper.selectById(request.getGoodId());
        if (good == null || DeleteEnum.DELETED.getCode() == good.getDeleted()) {
            throw new BusinessException("商品不存在");
        }
        if (GoodStatusEnum.NOT_LISTED.getCode() == good.getStatus()) {
            throw new BusinessException("商品已下架，无法购买");
        }

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
        long currentTime = System.currentTimeMillis();
        OrderEntity order = new OrderEntity();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setGoodId(good.getId());
        order.setRoomId(good.getRoomId());
        order.setQuantity(request.getQuantity());
        order.setOrderAmount(orderAmount);
        order.setStatus(ORDER_STATUS_UNPAID);
        order.setPayType(request.getPayType());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverAddress(address.getFullAddress());
        order.setRemark(request.getRemark());
        order.setDeleted(DeleteEnum.ACTIVE.getCode());
        order.setCreateTime(currentTime);
        order.setUpdateTime(currentTime);
        orderMapper.insert(order);

        log.info("下单成功: orderNo={}, userId={}, goodId={}", order.getOrderNo(), userId, good.getId());
        return order.getOrderNo();
    }

    @Override
    public String queryOrder(OrderQueryRequest request) {
        // TODO: 分页查询订单
        return null;
    }

    private Long currentUserId() {
        Long userId = UserInfoContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        return userId;
    }

    private String generateOrderNo() {
        return String.valueOf(System.currentTimeMillis()) + ThreadLocalRandom.current().nextInt(1000, 9999);
    }
}

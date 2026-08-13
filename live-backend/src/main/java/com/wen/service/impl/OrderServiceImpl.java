package com.wen.service.impl;

import com.wen.common.exception.BusinessException;
import com.wen.model.vo.OrderCreateRequest;
import com.wen.model.vo.OrderQueryRequest;
import com.wen.service.OrderService;
import com.wen.utils.UserInfoContext;
import org.springframework.stereotype.Service;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public String createOrder(OrderCreateRequest request) {
        Long userId = UserInfoContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }
        if (request.getAddressId() == null || request.getGoodId() == null || request.getQuantity() == null) {
            throw new BusinessException("下单参数不能为空");
        }

        // TODO: 1. 校验商品与库存，后端计算订单金额（不信任前端金额）
        // TODO: 2. 根据 addressId 查询地址，将收货人/电话/完整地址快照进订单（与地址表解耦）
        // TODO: 3. 生成订单号、落库、扣减库存
        return null;
    }

    @Override
    public String queryOrder(OrderQueryRequest request) {
        // TODO: 分页查询订单，关联商品与用户信息
        return null;
    }
}

package com.wen.module.order.service.impl;

import com.wen.common.exception.BusinessException;
import com.wen.module.order.domain.vo.OrderCreateRequest;
import com.wen.module.order.domain.vo.OrderQueryRequest;
import com.wen.module.order.domain.entity.GoodInfo;
import com.wen.module.order.service.OrderService;
import com.wen.module.order.service.GoodService;
import com.wen.module.user.domain.entity.UserInfo;
import com.wen.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;

    private final GoodService goodService;

    @Override
    public String createOrder(OrderCreateRequest request) {
        if (request.getUserId() == null || request.getGoodId() == null || request.getAddressId() == null) {
            throw new BusinessException("用户参数为空");
        }

        UserInfo userInfo = userService.queryByUserId(request.getUserId());
        GoodInfo goodInfo = goodService.queryGoodById(request.getGoodId());




        return null;
    }

    @Override
    public String queryOrder(OrderQueryRequest request) {
        return null;
    }
}

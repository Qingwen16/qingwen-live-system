package com.wen.module.order.service;

import com.wen.module.order.domain.vo.OrderCreateRequest;
import com.wen.module.order.domain.vo.OrderQueryRequest;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface OrderService {

    String createOrder(OrderCreateRequest request);

    String queryOrder(OrderQueryRequest request);

}

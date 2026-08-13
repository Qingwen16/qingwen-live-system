package com.wen.service;

import com.wen.model.vo.OrderCreateRequest;
import com.wen.model.vo.OrderQueryRequest;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface OrderService {

    String createOrder(OrderCreateRequest request);

    String queryOrder(OrderQueryRequest request);

}

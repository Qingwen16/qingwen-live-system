package com.wen.service;

import com.wen.model.vo.OrderAddRequest;
import com.wen.model.vo.OrderGetRequest;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
public interface OrderService {

    String createOrder(OrderAddRequest request);

    String queryOrder(OrderGetRequest request);

}

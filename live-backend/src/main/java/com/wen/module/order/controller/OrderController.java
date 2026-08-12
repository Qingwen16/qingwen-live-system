package com.wen.module.order.controller;

import com.wen.common.response.Response;
import com.wen.module.order.service.OrderService;
import com.wen.module.order.domain.vo.OrderCreateRequest;
import com.wen.module.order.domain.vo.OrderQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create/order")
    public Response<String> createOrder(@RequestBody OrderCreateRequest request) {
        String response = orderService.createOrder(request);
        return Response.success(response);
    }

    /**
     * 查询订单
     */
    @PostMapping("/query/order")
    public Response<String> queryOrder(@RequestBody OrderQueryRequest request) {
        String response = orderService.queryOrder(request);
        return Response.success(response);
    }

}

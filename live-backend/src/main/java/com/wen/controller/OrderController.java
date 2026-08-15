package com.wen.controller;

import com.wen.common.response.Response;
import com.wen.model.vo.OrderCreateRequest;
import com.wen.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器
 *
 * @author : rjw
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 下单购买
     */
    @PostMapping
    public Response<String> createOrder(@RequestBody OrderCreateRequest request) {
        return Response.success(orderService.createOrder(request), "下单成功");
    }
}

package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.PageResult;
import com.wen.common.response.Response;
import com.wen.model.dto.OrderDto;
import com.wen.model.vo.OrderCreateRequest;
import com.wen.model.vo.OrderQueryRequest;
import com.wen.model.vo.OrderIdRequest;
import com.wen.model.vo.OrderUpdateRequest;
import com.wen.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器
 *
 * @author jwruan
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 下单购买（登录即可）
     */
    @PostMapping("/create")
    public Response<Void> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        orderService.createOrder(request);
        return Response.success(null, "订单创建成功");
    }

    /**
     * 更新订单状态/支付方式/备注（管理端）
     */
    @PostMapping("/update")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> updateOrder(@Valid @RequestBody OrderUpdateRequest request) {
        orderService.updateOrder(request);
        return Response.success(null, "订单更新成功");
    }

    /**
     * 删除订单（管理端，软删除）
     */
    @PostMapping("/delete")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<Void> deleteOrder(@Valid @RequestBody OrderIdRequest request) {
        orderService.deleteOrder(request);
        return Response.success(null, "订单已删除");
    }

    /**
     * 分页查询订单（管理端）
     */
    @PostMapping("/query")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<PageResult<OrderDto>> queryOrders(@Valid @RequestBody OrderQueryRequest request) {
        return Response.success(orderService.queryOrders(request));
    }

}

package com.wen.service;

import com.wen.common.response.PageResult;
import com.wen.model.dto.OrderDto;
import com.wen.model.vo.OrderCreateRequest;
import com.wen.model.vo.OrderQueryRequest;
import com.wen.model.vo.OrderIdRequest;
import com.wen.model.vo.OrderUpdateRequest;

/**
 * 订单服务接口
 *
 * @author jwruan
 */
public interface OrderService {

    /**
     * 下单购买，返回订单号
     */
    void createOrder(OrderCreateRequest request);

    /**
     * 更新订单（状态、支付方式、备注）
     */
    void updateOrder(OrderUpdateRequest request);

    /**
     * 删除订单（软删除）
     */
    void deleteOrder(OrderIdRequest request);

    /**
     * 分页查询订单（管理端）
     */
    PageResult<OrderDto> queryOrders(OrderQueryRequest request);

}

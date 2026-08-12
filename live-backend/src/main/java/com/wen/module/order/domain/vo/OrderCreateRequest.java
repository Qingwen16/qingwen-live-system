package com.wen.module.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class OrderCreateRequest {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 收货地址
     */
    private String addressId;
    /**
     * 商品ID
     */
    private Long goodId;
    /**
     * 购买数量
     */
    private Integer quantity;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;
    /**
     * 支付时间
     */
    private Long payTime;
    /**
     * 备注
     */
    private String remark;
}

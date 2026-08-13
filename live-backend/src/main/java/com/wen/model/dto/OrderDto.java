package com.wen.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单信息 DTO
 *
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
public class OrderDto {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成
     */
    private Integer status;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;

    /**
     * 支付时间
     */
    private Long payTime;

    /**
     * 收货人姓名（快照）
     */
    private String receiverName;

    /**
     * 收货人电话（快照）
     */
    private String receiverPhone;

    /**
     * 收货完整地址（快照）
     */
    private String receiverAddress;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;
}

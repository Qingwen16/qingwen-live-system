package com.wen.module.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单信息实体类
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
@TableName("order_info")
public class OrderInfo {
    /**
     * 订单号
     */
    private String orderNo;
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
    /**
     * 创建时间
     */
    private Long createTime;
}
package com.wen.module.order.domain.vo;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class OrderQueryRequest {

    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 商品名称
     */
    private String goodName;
    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;
    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 截止时间
     */
    private Long endTime;

}

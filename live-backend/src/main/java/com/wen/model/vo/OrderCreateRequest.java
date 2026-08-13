package com.wen.model.vo;

import lombok.Data;

/**
 * 下单请求
 *
 * 下单时只传地址ID，收货信息由后端根据地址ID快照进订单，前端不可伪造；
 * 订单金额由后端按商品价格计算，不信任前端。
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class OrderCreateRequest {

    /**
     * 收货地址ID（下单时选择的地址簿地址）
     */
    private Long addressId;

    /**
     * 商品ID
     */
    private Long goodId;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;

    /**
     * 备注
     */
    private String remark;
}

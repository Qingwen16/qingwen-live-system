package com.wen.model.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
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
    @NotNull(message = "收货ID不能为空")
    private Long addressId;

    /**
     * 商品ID
     */
    @NotNull(message = "货物ID不能为空")
    private Long goodId;

    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "获取条数最小为1")
    private Integer quantity;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    /**
     * 备注
     */
    private String remark;
}

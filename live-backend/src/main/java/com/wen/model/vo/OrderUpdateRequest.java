package com.wen.model.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新订单请求
 * 订单金额、商品、收货地址为下单时快照，不可修改；仅允许变更状态、支付方式与备注
 *
 * @author jwruan
 */
@Data
public class OrderUpdateRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成
     */
    private Integer status;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;

    /**
     * 备注
     */
    private String remark;

}

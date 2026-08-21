package com.wen.model.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单分页查询请求
 *
 * @author jwruan
 */
@Data
public class OrderQueryRequest {

    /**
     * 页码，从 1 开始
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "获取页码最小页为1")
    private long pageNum;

    /**
     * 每页大小
     */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "获取条数最小为1")
    private long pageSize;

    /**
     * 用户手机号（模糊）
     */
    private String phone;

    /**
     * 商品名称（模糊）
     */
    private String goodName;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;

    /**
     * 订单状态：0-待支付，1-已支付，2-已取消，3-已完成
     */
    private Integer status;

    /**
     * 下单开始时间
     */
    private Long startTime;

    /**
     * 下单截止时间
     */
    private Long endTime;

}

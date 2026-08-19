package com.wen.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 修改商品请求（状态与销量由上下架/下单系统维护，不在本请求中修改）
 */
@Data
public class GoodUdtRequest {

    /**
     * 货物ID
     */
    private Long id;

    /**
     * 货物代码
     */
    private String code;

    /**
     * 货物名称
     */
    private String name;

    /**
     * 货物描述
     */
    private String desc;

    /**
     * 货物价格
     */
    private BigDecimal price;

    /**
     * 货物单位
     */
    private String unit;

    /**
     * 货物重量(kg)
     */
    private BigDecimal weight;

    /**
     * 货物图片URL
     */
    private String imageUrl;

    /**
     * 库存数量
     */
    private Integer stockCount;

    /**
     * 关联直播间 ID（可空）
     */
    private Long roomId;

    /**
     * 备注
     */
    private String remark;
}

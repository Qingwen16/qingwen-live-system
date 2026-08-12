package com.wen.module.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class GoodUpdateRequest {
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
     * 货物状态：0-已下架，1-已上架，2-缺货
     */
    private Integer status;
    /**
     * 库存数量
     */
    private Integer stockCount;
    /**
     * 销售数量
     */
    private Integer salesCount;
    /**
     * 备注
     */
    private String remark;
}

package com.wen.module.order.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
public class InsertGoodRequest {
    /**
     * 货物编号
     */
    private String number;
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
     * 库存数量
     */
    private Integer stock;
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
     * 供应商ID
     */
    private Long supplierId;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 备注
     */
    private String remark;
}

package com.wen.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增商品请求
 */
@Data
public class GoodAddRequest {
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
     * 库存数量
     */
    private Integer stockCount;
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
     * 关联直播间 ID（可空）
     */
    private Long roomId;
    /**
     * 备注
     */
    private String remark;
}

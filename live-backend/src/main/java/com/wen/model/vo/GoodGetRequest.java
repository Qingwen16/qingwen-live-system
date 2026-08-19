package com.wen.model.vo;

import lombok.Data;

/**
 * 商品分页查询请求
 */
@Data
public class GoodGetRequest {

    /**
     * 货物名称（模糊）
     */
    private String name;

    /**
     * 货物状态：0-已下架，1-已上架，2-缺货
     */
    private Integer status;

    /**
     * 关联直播间 ID
     */
    private Long roomId;
}

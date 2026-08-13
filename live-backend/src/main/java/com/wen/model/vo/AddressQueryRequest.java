package com.wen.model.vo;

import lombok.Data;

/**
 * 后台地址分页查询请求
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class AddressQueryRequest {

    /**
     * 页码，从 1 开始
     */
    private long pageNum = 1;

    /**
     * 每页大小
     */
    private long pageSize = 10;

    /**
     * 用户ID（精确）
     */
    private Long userId;

    /**
     * 收货人姓名（模糊）
     */
    private String name;

    /**
     * 收货人电话（模糊）
     */
    private String phone;

    /**
     * 是否默认地址：0-否，1-是
     */
    private Integer isDefault;
}

package com.wen.model.vo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台地址分页查询请求
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class AddressGetRequest {

    /**
     * 页码，从 1 开始
     */
    @NotNull
    @Min(value = 1, message = "获取页码最小页为1")
    private long pageNum;

    /**
     * 每页大小
     */
    @NotNull
    @Min(value = 1, message = "获取条数最小为1")
    private long pageSize;

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

package com.wen.model.vo;

import lombok.Data;

/**
 * 用户收货地址请求参数
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class AddressRequest {

    /**
     * 地址ID（修改时必传）
     */
    private Long id;

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 是否默认地址：0-否，1-是
     */
    private Integer isDefault;
}

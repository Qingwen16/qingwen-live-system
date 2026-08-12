package com.wen.module.user.domain.vo;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-03-18
 */
@Data
public class UserInfoDto {

    /**
     * 用户唯一标识（10 位数字，类似 QQ 号）
     */
    private Long userId;

    /**
     * 用户名（系统自动生成或用户自定义）
     */
    private String username;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;

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
     * 地址
     */
    private String address;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 用户状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 用户是否注销 0-注销 1-未注销
     */
    private Integer deleted;

}

package com.wen.module.user.domain.vo;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class UserQueryRequest {
    /**
     * 用户名（系统自动生成或用户自定义）
     */
    private String username;
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
     * 用户状态 0-禁用 1-正常
     */
    private Integer status;
    /**
     * 用户是否注销 0-注销 1-未注销
     */
    private Integer deleted;

}

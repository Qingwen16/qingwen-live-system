package com.wen.module.auth.domain.vo;

import lombok.Data;

/**
 * @Author : 青灯文案
 * @Date: 2026/4/7 20:39
 */
@Data
public class PhoneLoginRequest {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;

    /**
     * 登录IP
     */
    private String ip;

}

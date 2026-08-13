package com.wen.model.vo;

import lombok.Data;

/**
 * 微信登录请求
 * @Author : 青灯文案
 * @Date: 2026/4/7 20:41
 */
@Data
public class WechatLoginRequest {

    /**
     * 微信登录code（用于获取openid和session_key）
     */
    private String code;

    /**
     * 获取手机号的code（微信小程序getPhoneNumber返回）
     */
    private String phoneCode;

    /**
     * 登录IP
     */
    private String ip;

    /**
     * 客户端类型 {@link com.wen.common.enums.ClientTypeEnum} 0-手机端 1-管理端
     */
    private Integer clientType;

}

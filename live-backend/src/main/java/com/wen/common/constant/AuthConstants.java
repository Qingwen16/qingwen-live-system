package com.wen.common.constant;

/**
 * @author : rjw
 * @date : 2026-03-17
 */
public class AuthConstants {

    /**
     * 手机号格式
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 验证码长度
     */
    public static final int SMS_CODE_LENGTH = 6;

    /**
     * 过期时间（秒）
     */
    public static final int SMS_CODE_EXPIRE_SECONDS = 5 * 60;

    /**
     * 最大重试次数
     */
    public static final int SMS_CODE_MAX_RETRY_COUNT = 5;

    /**
     * 发送间隔（秒）
     */
    public static final int SMS_CODE_SEND_INTERVAL_SECONDS = 60;

}

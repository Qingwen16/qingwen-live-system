package com.wen.common.generator;


import com.wen.common.constant.AuthConstants;

import java.security.SecureRandom;

/**
 * @author : rjw
 * @date : 2026-03-18
 * 手机验证码生成器
 */
public class SmsCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < AuthConstants.SMS_CODE_LENGTH; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }

}

package com.wen.common.generator;


import com.wen.common.constant.AuthConstants;

/**
 * @author : rjw
 * @date : 2026-03-18
 * 手机验证码生成器
 */
public class SmsCodeGenerator {

    public static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < AuthConstants.SMS_CODE_LENGTH; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

}

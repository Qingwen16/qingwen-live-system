package com.wen.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

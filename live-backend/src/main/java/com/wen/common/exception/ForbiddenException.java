package com.wen.common.exception;

import lombok.Getter;

/**
 * 权限不足异常（403）
 *
 * @author jwruan
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private final int code = 403;

    public ForbiddenException(String message) {
        super(message);
    }
}

package com.wen.common.exception;

import com.wen.common.response.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Response<Void> handleBusinessException(BusinessException e) {
        return Response.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        return Response.fail(500, "服务器内部错误：" + e.getMessage());
    }
}

package com.wen.common.exception;

import com.wen.common.response.Response;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(ForbiddenException.class)
    public Response<Void> handleForbiddenException(ForbiddenException e) {
        return Response.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("参数校验失败");
        return Response.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        return Response.fail(500, "服务器内部错误：" + e.getMessage());
    }
}

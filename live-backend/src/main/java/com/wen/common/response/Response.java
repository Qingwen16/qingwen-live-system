package com.wen.common.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author : rjw
 * @description: 返回基础类
 * @date : 2026-03-12
 */
@Data
@Builder
@AllArgsConstructor
public class Response<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Response<T> success() {
        return Response
                .<T>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .build();
    }

    public static <T> Response<T> success(T t) {

        return Response
                .<T>builder()
                .code(HttpStatus.OK.value())
                .data(t)
                .message("Success")
                .build();
    }

    public static <T> Response<T> success(T t, String message) {

        return Response
                .<T>builder()
                .code(HttpStatus.OK.value())
                .data(t)
                .message(message)
                .build();
    }

    public static <T> Response<T> fail(T t, int code, String message) {
        return Response
                .<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> Response<T> fail(int code, String message) {
        return Response
                .<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> Response<T> response(int code, String message) {
        return Response
                .<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}

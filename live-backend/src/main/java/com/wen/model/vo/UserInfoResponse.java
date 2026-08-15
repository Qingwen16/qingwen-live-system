package com.wen.model.vo;

import lombok.Data;

/**
 * 用户登录响应
 */
@Data
public class UserInfoResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * Token 有效期（秒）
     */
    private Long expiresIn;

    /**
     * 当前登录用户摘要信息
     */
    private LoginUserInfoVO user;

    public static UserInfoResponse of(String token, Long expiresIn, LoginUserInfoVO user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setToken(token);
        response.setExpiresIn(expiresIn);
        response.setUser(user);
        return response;
    }
}

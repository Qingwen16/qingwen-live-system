package com.wen.module.auth.domain.vo;

import lombok.Data;

/**
 * Token DTO - 单令牌模式
 * @author : rjw
 * @date : 2026-03-19
 */
@Data
public class TokenDto {

    /**
     * JWT Token (7天有效期)
     */
    private String token;

}

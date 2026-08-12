package com.wen.common.generator;

import com.wen.module.auth.domain.vo.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT Token 生成器 - 单令牌模式
 * 
 * @author : rjw
 * @date : 2026-03-18
 */
@Component
@Slf4j
@Data
public class JwtTokenGenerator {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    /**
     * Token 过期时间：秒, 604800 (7天)
     */
    @Value("${jwt.token-timeout:604800}")
    private long tokenTimeout;

    /**
     * 生成 Token
     */
    public TokenDto generateToken(Long userId) {
        SecretKey key = createSecretKey();

        // 生成 Token，包含 userId 和唯一的 JTI
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        String token = Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenTimeout * 1000))
                .signWith(key)
                .compact();

        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(token);
        return tokenDto;
    }

    /**
     * 从 Token 中解析出 Claims
     */
    public Claims parseToken(String token) {
        SecretKey key = createSecretKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 创建密钥
     */
    private SecretKey createSecretKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }

    /**
     * 检查 Token 是否过期
     */
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * 从 Token 中获取 userId
     * @param token JWT token
     * @return 用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取 JTI (唯一ID)
     * @param token JWT token
     * @return JTI
     */
    public String getJtiFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getId();
    }

}
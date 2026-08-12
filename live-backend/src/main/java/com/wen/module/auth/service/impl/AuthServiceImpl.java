package com.wen.module.auth.service.impl;

import com.wen.module.auth.domain.vo.TokenDto;
import com.wen.module.auth.service.AuthService;
import com.wen.module.auth.service.CacheService;
import com.wen.common.generator.JwtTokenGenerator;
import com.wen.utils.UserInfoContext;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类 - 单令牌模式
 * @Author : 青灯文案
 * @Date: 2026/4/7 20:57
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final CacheService cacheService;
    
    private final JwtTokenGenerator tokenGenerator;

    /**
     * 用户登出
     */
    @Override
    public void logout(TokenDto tokenDto) {
        // 1. 参数校验
        String requestToken = tokenDto.getToken();
        if (requestToken == null || requestToken.isEmpty()) {
            UserInfoContext.clear();
            return;
        }
        
        // 2. 解析 Token，获取 userId
        Claims claims;
        try {
            claims = tokenGenerator.parseToken(requestToken);
        } catch (RuntimeException exception) {
            // 如果 Token 本身就无效，直接忽略即可
            UserInfoContext.clear();
            return;
        }

        Long userId = claims.get("userId", Long.class);

        // 3. 从 Redis 中删除用户的 Token (单Token模式，删除即可)
        cacheService.delUserToken(userId);
        
        // 4. 清除用户上下文
        UserInfoContext.clear();
        log.info("用户登出成功: userId={}", userId);
    }
    
}

package com.wen.intercepter;

import com.wen.module.auth.service.CacheService;
import com.wen.common.generator.JwtTokenGenerator;
import com.wen.utils.UserInfoContext;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用户认证拦截器
 * 解析 Token，设置用户上下文（游客或登录用户）
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtTokenGenerator jwtTokenGenerator;

    private final CacheService cacheService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 从请求头获取 Token
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        String accessToken = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtTokenGenerator.parseToken(accessToken);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return false;
        }

        // 2. 检查 Token 是否过期
        if (jwtTokenGenerator.isTokenExpired(claims)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return false;
        }

        Long userId = claims.get("userId", Long.class);

        // 3. 检查单点登录：校验 Redis 中是否存在该用户的 Token
        String storedToken = cacheService.getUserToken(userId);
        if (storedToken == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired, please login again");
            return false;
        }

        // 5. 所有检查通过，将用户信息存入 ThreadLocal，供后续业务使用
        // 例如: UserContextHolder.setUser(new AuthenticatedUser(userId, claims.get("role", String.class)));

        return true;
    }

    /**
     * 别忘了在 afterCompletion 中清理 ThreadLocal
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清理上下文，防止内存泄漏
        UserInfoContext.clear();
    }
}

package com.wen.intercepter;

import com.wen.common.enums.ClientTypeEnum;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.common.generator.JwtTokenGenerator;
import com.wen.model.entity.UserEntity;
import com.wen.service.CacheService;
import com.wen.service.RoleService;
import com.wen.service.UserService;
import com.wen.utils.LoginUser;
import com.wen.utils.UserInfoContext;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 用户认证拦截器
 * 解析 Token，校验单点登录与用户状态，设置登录用户上下文
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtTokenGenerator jwtTokenGenerator;

    private final CacheService cacheService;

    private final UserService userService;

    private final RoleService roleService;

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
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return false;
        }

        // 3. 单点登录校验：Redis 中存储的 token 必须与当前请求一致，重新登录后旧 token 立即失效
        String storedToken = cacheService.getUserToken(userId);
        if (storedToken == null || !storedToken.equals(accessToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired, please login again");
            return false;
        }

        // 4. 实时校验用户状态，避免禁用/注销后旧 token 仍可访问
        UserEntity user = userService.queryByUserId(userId);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return false;
        }
        if (Objects.equals(user.getStatus(), StatusEnum.DISABLED.getCode())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account disabled");
            return false;
        }
        if (Objects.equals(user.getDeleted(), DeleteEnum.DELETED.getCode())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Account deleted");
            return false;
        }

        // 5. 角色以数据库为准，不信任 token 中的快照，保证角色变更实时生效
        Integer role = roleService.queryRoleByUserId(userId);
        if (role == null) {
            role = RoleTypeEnum.USER.getCode();
        }
        Integer clientType = claims.get("clientType", Integer.class);
        if (clientType == null) {
            clientType = ClientTypeEnum.APP.getCode();
        }

        UserInfoContext.setLoginUser(new LoginUser(userId, role, clientType));
        return true;
    }

    /**
     * 请求完成后清理上下文，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserInfoContext.clear();
    }
}

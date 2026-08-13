package com.wen.common.aop;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.exception.ForbiddenException;
import com.wen.utils.UserInfoContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限校验切面
 * 校验当前登录用户角色是否满足 @RequireRole 声明的角色
 * 方法级注解优先于类级注解
 *
 * @author jwruan
 */
@Aspect
@Component
public class RequireRoleAspect {

    @Around("@within(com.wen.common.annotation.RequireRole) || @annotation(com.wen.common.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        RequireRole requireRole = resolveRequireRole(joinPoint);
        if (requireRole == null) {
            return joinPoint.proceed();
        }

        Integer role = UserInfoContext.getRole();
        if (role == null) {
            throw new ForbiddenException("未登录或登录已失效");
        }

        // 系统管理员拥有所有权限
        if (role == RoleTypeEnum.SUPER_ADMIN.getCode()) {
            return joinPoint.proceed();
        }

        for (RoleTypeEnum allowed : requireRole.value()) {
            if (allowed.getCode() == role) {
                return joinPoint.proceed();
            }
        }

        throw new ForbiddenException("无权限访问该接口，当前角色：" + describeRole(role));
    }

    /**
     * 解析权限注解，方法级优先，其次类级
     */
    private RequireRole resolveRequireRole(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole annotation = method.getAnnotation(RequireRole.class);
        if (annotation != null) {
            return annotation;
        }
        return joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
    }

    /**
     * 角色 code 转中文描述
     */
    private String describeRole(Integer role) {
        for (RoleTypeEnum type : RoleTypeEnum.values()) {
            if (type.getCode() == role) {
                return type.getDesc();
            }
        }
        return String.valueOf(role);
    }
}

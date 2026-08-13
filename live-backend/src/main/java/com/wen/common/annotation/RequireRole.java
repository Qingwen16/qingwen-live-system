package com.wen.common.annotation;

import com.wen.common.enums.RoleTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限注解 - 标注在类或方法上，声明允许访问的角色
 * 系统管理员（SUPER_ADMIN）默认拥有所有权限
 *
 * @author jwruan
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     */
    RoleTypeEnum[] value();
}

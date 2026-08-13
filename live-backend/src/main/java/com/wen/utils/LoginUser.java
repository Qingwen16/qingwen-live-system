package com.wen.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当前登录用户上下文对象
 * 拦截器解析 Token 后写入，供后续业务鉴权使用
 *
 * @author jwruan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色类型 {@link com.wen.common.enums.RoleTypeEnum}
     */
    private Integer role;

    /**
     * 客户端类型 {@link com.wen.common.enums.ClientTypeEnum}
     */
    private Integer clientType;
}

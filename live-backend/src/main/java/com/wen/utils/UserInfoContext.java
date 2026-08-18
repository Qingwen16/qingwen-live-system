package com.wen.utils;

import com.wen.common.exception.BusinessException;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前请求的登录用户信息
 * 系统强制登录，必须通过手机号登录后才能访问
 *
 * @author jwruan
 */
public class UserInfoContext {

    /**
     * 当前登录用户
     */
    private static final ThreadLocal<LoginUser> CURRENT_USER = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     * @param loginUser 登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        CURRENT_USER.set(loginUser);
    }

    /**
     * 获取当前登录用户
     * @return 登录用户
     */
    public static LoginUser getLoginUser() {
        return CURRENT_USER.get();
    }

    /**
     * 获取当前登录用户 ID
     * @return 用户 ID
     */
    public static Long getUserId() {
        LoginUser user = CURRENT_USER.get();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("用户 ID 为 null，未登录或登录已过期");
        }
        return user.getUserId();
    }

    /**
     * 获取当前登录用户角色
     * @return 角色类型
     */
    public static Integer getRole() {
        LoginUser user = CURRENT_USER.get();
        return user == null ? null : user.getRole();
    }

    /**
     * 获取当前登录客户端类型
     * @return 客户端类型
     */
    public static Integer getClientType() {
        LoginUser user = CURRENT_USER.get();
        return user == null ? null : user.getClientType();
    }

    /**
     * 清除当前上下文
     * 必须在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        CURRENT_USER.remove();
    }
}

package com.wen.utils;


import com.wen.module.user.domain.vo.UserInfoDto;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 存储当前请求的用户信息
 */
public class UserInfoContext {

    /**
     * 当前登录用户（null 表示游客）
     */
    private static final ThreadLocal<UserInfoDto> CURRENT_USER = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     * @param userInfo 用户
     */
    public static void setUserInfo(UserInfoDto userInfo) {
        CURRENT_USER.set(userInfo);
    }

    /**
     * 获取当前登录用户
     * @return 用户，如果是游客返回 null
     */
    public static UserInfoDto getUserInfo() {
        return CURRENT_USER.get();
    }

    /**
     * 判断是否是游客
     * @return true-游客，false-已登录用户
     */
    public static boolean isGuest() {
        return CURRENT_USER.get() == null;
    }

    /**
     * 清除当前上下文
     * 必须在请求结束时调用，防止内存泄漏
     */
    public static void clear() {
        CURRENT_USER.remove();
    }
}

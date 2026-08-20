package com.wen.service;

/**
 * @author : rjw
 * @date : 2026-03-17
 */
public interface CacheService {

    /**
     * 保存短信验证码发送记录(间隔控制)
     */
    void setSendSmsCodeInterval(String phone);

    /**
     * 查询手机号是否在发送间隔内，true-在间隔内(不能发送) false-不在间隔内(可以发送)
     */
    boolean getSendSmsCodeInterval(String phone);

    /**
     * 存储短信验证码
     */
    void setSmsCodeCache(String phone, String code);

    /**
     * 获取短信验证码
     */
    String getSmsCodeCache(String phone);

    /**
     * 删除短信验证码
     */
    void delSmsCodeCache(String phone);

    /**
     * 设置用户 Token (用于登出时验证)
     */
    void setUserToken(Long userId, String token, Long timeout);

    /**
     * 获取用户 Token
     */
    String getUserToken(Long userId);

    /**
     * 删除用户 Token (登出时调用)
     */
    void delUserToken(Long userId);

    /**
     * 累加验证码错误次数，返回累加后的值
     */
    long incrSmsCodeRetryCount(String phone);

    /**
     * 删除验证码错误次数
     */
    void delSmsCodeRetryCount(String phone);

    /**
     * 缓存用户角色（role 为 null 时不缓存）
     */
    void setUserRoleCache(Long userId, Integer role);

    /**
     * 获取缓存的用户角色，未命中返回 null
     */
    Integer getUserRoleCache(Long userId);

    /**
     * 删除用户角色缓存（角色变更后调用）
     */
    void delUserRoleCache(Long userId);

}

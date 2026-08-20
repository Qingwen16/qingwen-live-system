package com.wen.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author : 青灯文案
 * @Date: 2026/3/14 16:54
 */
@Data
@Slf4j
@Configuration
public class CacheConfig {

    @Value("${cache.keyNameSpace}")
    private String keyNameSpace;

    @Value("${cache.defaultKeyVersion}")
    private String defaultKeyVersion;

    private TimeUnit defaultTimeUnit = TimeUnit.SECONDS;

    /**
     * 角色缓存过期时间（秒）
     */
    private long timeoutRole = 30 * 60L;

    /**
     * 项目 key 前缀
     */
    public String prefix() {
        return keyNameSpace + ":" + defaultKeyVersion + ":";
    }

    /**
     * 手机短信是否发送过的时间
     */
    public String getKeySendSmsCodeInterval(String phone) {
        return prefix() + "SendSmsCodeInterval:" + phone;
    }

    /**
     * 手机短信验证码的缓存
     */
    public String getKeyPhoneSmsCode(String phone) {
        return prefix() + "PhoneSmsCode:" + phone;
    }

    /**
     * 获取用户手机验证码缓存Key
     */
    public String getKeyRefreshToken(Long userId) {
        return prefix() + "RefreshToken:" + userId;
    }

    /**
     * 手机验证码错误次数的缓存Key
     */
    public String getKeySmsCodeRetry(String phone) {
        return prefix() + "PhoneSmsCodeRetry:" + phone;
    }

    /**
     * 用户角色缓存的 Key
     */
    public String getKeyUserRole(Long userId) {
        return prefix() + "UserRole:" + userId;
    }

}

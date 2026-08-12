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
     * 设置AccessToken的黑名单
     */
    public String getKeyAccessTokenBlackList(String accessTokenJti) {
        return prefix() + "AccessTokenBlackList:" + accessTokenJti;
    }

}

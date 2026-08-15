package com.wen.service.impl;

import com.wen.config.CacheConfig;
import com.wen.common.constant.AuthConstants;
import com.wen.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现类
 *
 * @author : rjw
 * @date : 2026-03-17
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final CacheConfig cacheConfig;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存发送短信验证码的间隔时间
     */
    @Override
    public void setSendSmsCodeInterval(String phone) {
        String timeKey = cacheConfig.getKeySendSmsCodeInterval(phone);
        redisTemplate.opsForValue().set(timeKey, "1", AuthConstants.SMS_CODE_SEND_INTERVAL_SECONDS,
                cacheConfig.getDefaultTimeUnit());
    }

    /**
     * 获取发送短信验证码的间隔时间
     */
    @Override
    public boolean getSendSmsCodeInterval(String phone) {
        String timeKey = cacheConfig.getKeySendSmsCodeInterval(phone);
        return Boolean.TRUE.equals(redisTemplate.hasKey(timeKey));
    }

    /**
     * 存储短信验证码缓存
     */
    @Override
    public void setSmsCodeCache(String phone, String code) {
        String codeKey = cacheConfig.getKeyPhoneSmsCode(phone);
        redisTemplate.opsForValue().set(codeKey, code, AuthConstants.SMS_CODE_EXPIRE_SECONDS,
                cacheConfig.getDefaultTimeUnit());
        log.info("保存短信验证码缓存: phone={}", phone);
    }

    /**
     * 获取短信验证码缓存
     */
    @Override
    public String getSmsCodeCache(String phone) {
        String codeKey = cacheConfig.getKeyPhoneSmsCode(phone);
        return (String) redisTemplate.opsForValue().get(codeKey);
    }

    /**
     * 删除短信验证码缓存
     */
    @Override
    public void delSmsCodeCache(String phone) {
        String codeKey = cacheConfig.getKeyPhoneSmsCode(phone);
        redisTemplate.delete(codeKey);
    }

    /**
     * 累加验证码错误次数
     */
    @Override
    public long incrSmsCodeRetryCount(String phone) {
        String retryKey = cacheConfig.getKeySmsCodeRetry(phone);
        Long count = redisTemplate.opsForValue().increment(retryKey);
        if (count != null && count == 1L) {
            redisTemplate.expire(retryKey, AuthConstants.SMS_CODE_EXPIRE_SECONDS, cacheConfig.getDefaultTimeUnit());
        }
        return count == null ? 0L : count;
    }

    /**
     * 删除验证码错误次数
     */
    @Override
    public void delSmsCodeRetryCount(String phone) {
        redisTemplate.delete(cacheConfig.getKeySmsCodeRetry(phone));
    }

    /**
     * 存储用户 Token
     */
    @Override
    public void setUserToken(Long userId, String token, Long timeout) {
        String tokenKey = cacheConfig.getKeyRefreshToken(userId);
        TimeUnit timeUnit = cacheConfig.getDefaultTimeUnit();
        redisTemplate.opsForValue().set(tokenKey, token, timeout, timeUnit);
    }

    /**
     * 获取用户 Token
     */
    @Override
    public String getUserToken(Long userId) {
        String tokenKey = cacheConfig.getKeyRefreshToken(userId);
        return (String) redisTemplate.opsForValue().get(tokenKey);
    }

    /**
     * 删除用户 Token
     */
    @Override
    public void delUserToken(Long userId) {
        String tokenKey = cacheConfig.getKeyRefreshToken(userId);
        redisTemplate.delete(tokenKey);
    }

}

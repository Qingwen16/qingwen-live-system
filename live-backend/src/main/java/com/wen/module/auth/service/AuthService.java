package com.wen.module.auth.service;

import com.wen.module.auth.domain.vo.TokenDto;

/**
 * 认证服务接口 - 单令牌模式
 *
 * @Author : 青灯文案
 * @Date: 2026/4/7 20:57
 */
public interface AuthService {

    /**
     * 用户登出
     */
    void logout(TokenDto tokenDto);

}

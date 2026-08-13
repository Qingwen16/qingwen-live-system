package com.wen.service;

import com.wen.model.vo.PhoneLoginRequest;
import com.wen.model.vo.SmsCodeRequest;
import com.wen.model.dto.TokenDto;
import com.wen.model.vo.UserInfoResponse;
import com.wen.model.vo.WechatLoginRequest;

/**
 * 认证服务接口 - 统一管理短信验证码、手机号登录、微信登录、登出
 *
 * @author jwruan
 */
public interface AuthService {

    /**
     * 发送短信验证码
     */
    void sendSmsCode(SmsCodeRequest request);

    /**
     * 手机号验证码登录
     */
    UserInfoResponse loginByPhone(PhoneLoginRequest request);

    /**
     * 微信登录
     */
    UserInfoResponse loginByWeChat(WechatLoginRequest request);

    /**
     * 用户登出
     */
    void logout(TokenDto tokenDto);

}

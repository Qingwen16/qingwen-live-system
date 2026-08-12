package com.wen.module.auth.service;


import com.wen.module.auth.domain.vo.WechatLoginRequest;
import com.wen.module.user.domain.vo.UserInfoResponse;

/**
 * @author : rjw
 * @date : 2026-03-19
 */
public interface WechatService {

    /**
     * 处理登录请求
     */
    UserInfoResponse loginByWeChat(WechatLoginRequest request);

}

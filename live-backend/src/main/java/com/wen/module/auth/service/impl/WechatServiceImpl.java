package com.wen.module.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.wen.module.auth.domain.vo.TokenDto;
import com.wen.module.auth.domain.vo.WechatLoginRequest;
import com.wen.common.constant.AuthConstants;
import com.wen.common.exception.BusinessException;
import com.wen.common.generator.JwtTokenGenerator;
import com.wen.module.user.domain.vo.UserInfoDto;
import com.wen.module.user.domain.vo.UserInfoResponse;
import com.wen.module.user.service.UserService;
import com.wen.utils.UserInfoContext;
import com.wen.utils.WechatUtils;
import com.wen.module.auth.service.CacheService;
import com.wen.module.auth.service.WechatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信登录服务实现类
 * @author : rjw
 * @date : 2026-03-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final WechatUtils wechatUtils;
    
    private final UserService userService;
    
    private final JwtTokenGenerator jwtTokenGenerator;
    
    private final CacheService cacheService;

    /**
     * 微信一键获取手机号登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoResponse loginByWeChat(WechatLoginRequest request) {
        // 1. 参数校验
        validateParams(request);
        
        // 2. 通过 code 获取 openid 和 session_key
        JSONObject sessionResult = wechatUtils.getOpenIdAndSessionKey(request.getCode());
        String openid = sessionResult.getStr("openid");
        String unionid = sessionResult.getStr("unionid");
        log.info("微信登录成功获取 openid: {}, unionid: {}", openid, unionid);
        
        // 3. 通过 phoneCode 获取手机号
        String phone = wechatUtils.getPhoneNumber(request.getPhoneCode());
        log.info("微信登录获取到手机号: {}", phone);
        
        // 4. 验证手机号格式
        if (!phone.matches(AuthConstants.PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
        
        // 5. 查询或注册用户
        UserInfoDto user = userService.registerUser(phone);
        if (user == null) {
            throw new BusinessException("用户注册失败");
        }
        
        // TODO: 将微信 openid/unionid 与用户绑定到 user_auth 表
        // bindWechatToUser(user.getUserId(), openid, unionid);
        
        // 6. 登录成功，将用户信息存入上下文
        UserInfoContext.setUserInfo(user);
        
        // 7. 生成 Token
        TokenDto tokenDto = jwtTokenGenerator.generateToken(user.getUserId());
        
        // 8. 缓存 Token
        cacheService.setUserToken(user.getUserId(), tokenDto.getToken(),
                jwtTokenGenerator.getTokenTimeout());
        
        // 9. 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setUserInfoDto(user);
        response.setTokenDto(tokenDto);
        
        log.info("微信登录成功: userId={}, phone={}", user.getUserId(), phone);
        return response;
    }

    /**
     * 参数校验
     */
    private void validateParams(WechatLoginRequest request) {
        if (StrUtil.isBlank(request.getCode())) {
            throw new BusinessException("微信登录code不能为空");
        }
        if (StrUtil.isBlank(request.getPhoneCode())) {
            throw new BusinessException("微信手机号code不能为空");
        }
    }
}

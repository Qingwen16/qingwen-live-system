package com.wen.module.auth.controller;


import com.wen.common.response.Response;
import com.wen.module.auth.domain.vo.PhoneLoginRequest;
import com.wen.module.auth.domain.vo.SmsCodeRequest;
import com.wen.module.auth.domain.vo.TokenDto;
import com.wen.module.auth.domain.vo.WechatLoginRequest;
import com.wen.module.auth.service.AuthService;
import com.wen.module.auth.service.PhoneService;
import com.wen.module.auth.service.WechatService;
import com.wen.module.user.domain.vo.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author jwruan
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    private final WechatService weChatService;
    
    private final PhoneService phoneService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/sms/send")
    public Response<Void> sendSmsCode(@RequestBody SmsCodeRequest request) {
        phoneService.sendSmsCode(request);
        return Response.success(null, "验证码已发送");
    }

    /**
     * 手机号验证码登录
     */
    @PostMapping("/phone/login")
    public Response<UserInfoResponse> phoneLogin(@RequestBody PhoneLoginRequest request) {
        UserInfoResponse response = phoneService.loginByPhone(request);
        return Response.success(response);
    }

    /**
     * 微信一键获取手机号登录
     */
    @PostMapping("/wechat/login")
    public Response<UserInfoResponse> weChatLogin(@RequestBody WechatLoginRequest request) {
        UserInfoResponse response = weChatService.loginByWeChat(request);
        return Response.success(response);
    }
        
    /**
     * 登出
     */
    @GetMapping("/logout")
    public Response<Void> logout(TokenDto request) {
        authService.logout(request);
        return Response.success(null, "登出成功");
    }

}

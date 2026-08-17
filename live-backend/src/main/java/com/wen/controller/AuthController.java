package com.wen.controller;


import com.wen.common.response.Response;
import com.wen.model.vo.PhoneLoginRequest;
import com.wen.model.vo.SmsCodeRequest;
import com.wen.model.vo.UserInfoResponse;
import com.wen.model.vo.WechatLoginRequest;
import com.wen.service.AuthService;
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

    /**
     * 发送短信验证码
     */
    @PostMapping("/sms/send")
    public Response<Void> sendSmsCode(@RequestBody SmsCodeRequest request) {
        authService.sendSmsCode(request);
        return Response.success(null, "验证码已发送");
    }

    /**
     * 手机号验证码登录
     */
    @PostMapping("/phone/login")
    public Response<UserInfoResponse> phoneLogin(@RequestBody PhoneLoginRequest request) {
        UserInfoResponse response = authService.loginByPhone(request);
        return Response.success(response);
    }

    /**
     * 微信一键获取手机号登录
     */
    @PostMapping("/wechat/login")
    public Response<UserInfoResponse> weChatLogin(@RequestBody WechatLoginRequest request) {
        UserInfoResponse response = authService.loginByWeChat(request);
        return Response.success(response);
    }

    /**
     * 登出（token 从 Authorization 头获取，避免出现在 URL 日志中）
     */
    @PostMapping("/logout")
    public Response<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        authService.logout(token);
        return Response.success(null, "登出成功");
    }

}

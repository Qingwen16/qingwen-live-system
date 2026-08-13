package com.wen.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.wen.common.constant.AuthConstants;
import com.wen.common.enums.ClientTypeEnum;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.common.exception.BusinessException;
import com.wen.common.generator.JwtTokenGenerator;
import com.wen.common.generator.SmsCodeGenerator;
import com.wen.model.dto.TokenDto;
import com.wen.model.dto.UserDto;
import com.wen.model.vo.PhoneLoginRequest;
import com.wen.model.vo.SmsCodeRequest;
import com.wen.model.vo.UserInfoResponse;
import com.wen.model.vo.WechatLoginRequest;
import com.wen.service.AuthService;
import com.wen.service.CacheService;
import com.wen.service.RoleService;
import com.wen.service.UserService;
import com.wen.utils.LoginUser;
import com.wen.utils.UserInfoContext;
import com.wen.utils.WechatUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现类 - 统一管理短信验证码、手机号登录、微信登录、登出
 *
 * @author jwruan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenGenerator jwtTokenGenerator;

    private final CacheService cacheService;

    private final UserService userService;

    private final RoleService roleService;

    private final WechatUtils wechatUtils;

    /**
     * 发送短信验证码
     */
    @Override
    public void sendSmsCode(SmsCodeRequest request) {
        // 1. 校验手机号
        if (StrUtil.isBlank(request.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }
        if (!request.getPhone().matches(AuthConstants.PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }

        // 2. 检查发送间隔
        if (cacheService.getSendSmsCodeInterval(request.getPhone())) {
            throw new RuntimeException("发送过于频繁，请" + AuthConstants.SMS_CODE_SEND_INTERVAL_SECONDS + "秒后再试");
        }

        String code = SmsCodeGenerator.generateCode();

        // 存缓存验证码与发送间隔
        cacheService.setSmsCodeCache(request.getPhone(), code);
        cacheService.setSendSmsCodeInterval(request.getPhone());

        // 3. 调用短信服务商发送短信
        sendSmsCode(request.getPhone(), code);
        log.info("【验证码】手机号={}, 验证码={}, 有效期 5 分钟", request.getPhone(), code);
        System.out.println("【青问直播】您的验证码是：" + code + "，5 分钟内有效");
    }

    /**
     * 手机号验证码登录（用户不存在时自动注册）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoResponse loginByPhone(PhoneLoginRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();
        Integer clientType = request.getClientType();
        // 1. 参数校验
        validateParams(phone, code);
        // 2. 验证验证码
        verifyCode(phone, code);
        // 3. 查询用户
        UserDto user = buildUserInfoDto(phone);
        // 4. 查询角色并校验客户端权限
        Integer role = resolveRole(user.getUserId());
        checkClientPermission(clientType, role);
        user.setRole(role);
        // 5. 登录成功，将用户信息存入本地
        UserInfoContext.setLoginUser(new LoginUser(user.getUserId(), role, clientType));
        // 6. 生成 Token
        TokenDto tokenDto = jwtTokenGenerator.generateToken(user.getUserId(), role, clientType);
        // 7. 缓存 Token
        cacheService.setUserToken(user.getUserId(), tokenDto.getToken(),
                jwtTokenGenerator.getTokenTimeout());
        // 8. 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setUserInfoDto(user);
        response.setTokenDto(tokenDto);
        return response;
    }

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
        UserDto user = userService.registerUser(phone);
        if (user == null) {
            throw new BusinessException("用户注册失败");
        }

        // TODO: 将微信 openid/unionid 与用户绑定到 user_auth 表
        // bindWechatToUser(user.getUserId(), openid, unionid);

        // 6. 查询角色并校验客户端权限
        Integer clientType = request.getClientType();
        Integer role = resolveRole(user.getUserId());
        checkClientPermission(clientType, role);
        user.setRole(role);

        // 7. 登录成功，将用户信息存入上下文
        UserInfoContext.setLoginUser(new LoginUser(user.getUserId(), role, clientType));

        // 8. 生成 Token
        TokenDto tokenDto = jwtTokenGenerator.generateToken(user.getUserId(), role, clientType);

        // 9. 缓存 Token
        cacheService.setUserToken(user.getUserId(), tokenDto.getToken(),
                jwtTokenGenerator.getTokenTimeout());

        // 10. 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setUserInfoDto(user);
        response.setTokenDto(tokenDto);

        log.info("微信登录成功: userId={}, phone={}", user.getUserId(), phone);
        return response;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(TokenDto tokenDto) {
        // 1. 参数校验
        String requestToken = tokenDto.getToken();
        if (requestToken == null || requestToken.isEmpty()) {
            UserInfoContext.clear();
            return;
        }

        // 2. 解析 Token，获取 userId
        Claims claims;
        try {
            claims = jwtTokenGenerator.parseToken(requestToken);
        } catch (RuntimeException exception) {
            // Token 无效时直接忽略
            UserInfoContext.clear();
            return;
        }

        Long userId = claims.get("userId", Long.class);

        // 3. 从 Redis 中删除用户的 Token (单 Token 模式，删除即可)
        cacheService.delUserToken(userId);

        // 4. 清除用户上下文
        UserInfoContext.clear();
        log.info("用户登出成功: userId={}", userId);
    }

    /**
     * 发送短信
     */
    private void sendSmsCode(String phone, String code) {
        // 调用第三方短信服务
        log.info("发送短信：phone={}, code={}", phone, code);
    }

    /**
     * 手机号登录参数校验
     */
    private void validateParams(String phone, String code) {
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!phone.matches(AuthConstants.PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
        if (StrUtil.isBlank(code)) {
            throw new BusinessException("验证码不能为空");
        }
    }

    /**
     * 微信登录参数校验
     */
    private void validateParams(WechatLoginRequest request) {
        if (StrUtil.isBlank(request.getCode())) {
            throw new BusinessException("微信登录code不能为空");
        }
        if (StrUtil.isBlank(request.getPhoneCode())) {
            throw new BusinessException("微信手机号code不能为空");
        }
    }

    /**
     * 验证验证码
     */
    private void verifyCode(String phone, String code) {
        // 缓存获取验证码
        String cacheCode = cacheService.getSmsCodeCache(phone);
        if (cacheCode == null) {
            throw new BusinessException("验证码存在问题，请重新发送验证码");
        }
        if (!cacheCode.equals(code)) {
            throw new BusinessException("验证码存在问题，请重新发送验证码");
        }
        // 删除缓存中的 code，防止重复使用
        cacheService.delSmsCodeCache(phone);
    }

    /**
     * 查询用户信息
     */
    private UserDto buildUserInfoDto(String phone) {
        UserDto userInfoDto = userService.queryByPhone(phone);
        // 用户存在，则直接组装返回
        if (userInfoDto != null) {
            if (userInfoDto.getStatus() == StatusEnum.DISABLED.getCode()) {
                throw new BusinessException("该账号 [" + phone + "] 已被禁用");
            }
            if (userInfoDto.getDeleted() == DeleteEnum.DELETED.getCode()) {
                throw new BusinessException("该账号 [" + phone + "] 已被注销");
            }
            return userInfoDto;
        }
        // 用户不存在，注册用户
        return userService.registerUser(phone);
    }

    /**
     * 查询用户角色，未查到默认普通用户
     */
    private Integer resolveRole(Long userId) {
        Integer role = roleService.queryRoleByUserId(userId);
        return role == null ? RoleTypeEnum.USER.getCode() : role;
    }

    /**
     * 校验客户端权限：管理端只允许系统管理员登录
     */
    private void checkClientPermission(Integer clientType, Integer role) {
        if (clientType != null && clientType == ClientTypeEnum.WEB.getCode()
                && role != RoleTypeEnum.SUPER_ADMIN.getCode()) {
            throw new BusinessException("非系统管理员，无法登录管理后台");
        }
    }
}
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
import com.wen.model.dto.UserDto;
import com.wen.model.vo.LoginUserInfoVO;
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

import java.util.Objects;

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
        String phone = request.getPhone();
        validatePhone(phone);

        // 检查发送间隔
        if (cacheService.getSendSmsCodeInterval(phone)) {
            throw new BusinessException("发送过于频繁，请" + AuthConstants.SMS_CODE_SEND_INTERVAL_SECONDS + "秒后再试");
        }

        String code = SmsCodeGenerator.generateCode();

        // 先发送成功，再缓存验证码与发送间隔，避免发送失败后用户仍被限流
        sendSmsCode(phone, code);
        cacheService.setSmsCodeCache(phone, code);
        cacheService.setSendSmsCodeInterval(phone);
        log.info("【验证码】发送成功，phone={}", phone);
    }

    /**
     * 手机号验证码登录（用户不存在时自动注册）
     */
    @Override
    public UserInfoResponse loginByPhone(PhoneLoginRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();
        // 1. 参数校验
        validateParams(phone, code);
        // 2. 验证验证码
        verifyCode(phone, code);
        // 3. 查询或注册用户
        UserDto user = buildUserInfoDto(phone);
        // 4. 统一登录收尾
        return doLogin(user, resolveClientType(request.getClientType()));
    }

    /**
     * 微信一键获取手机号登录
     */
    @Override
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
        validatePhone(phone);
        log.info("微信登录获取到手机号: {}", phone);

        // 4. 查询或注册用户
        UserDto user = userService.registerUser(phone);
        if (user == null) {
            throw new BusinessException("用户注册失败");
        }

        // TODO: 将微信 openid/unionid 与用户绑定到 user_auth 表
        // bindWechatToUser(user.getUserId(), openid, unionid);

        // 5. 统一登录收尾
        return doLogin(user, resolveClientType(request.getClientType()));
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(String token) {
        if (StrUtil.isBlank(token)) {
            UserInfoContext.clear();
            return;
        }

        // 解析 Token，获取 userId
        Long userId;
        try {
            Claims claims = jwtTokenGenerator.parseToken(token);
            userId = claims.get("userId", Long.class);
        } catch (RuntimeException exception) {
            // Token 无效时直接忽略
            UserInfoContext.clear();
            return;
        }

        if (userId == null) {
            UserInfoContext.clear();
            return;
        }

        // 单 Token 模式，删除即可
        cacheService.delUserToken(userId);
        UserInfoContext.clear();
        log.info("用户登出成功: userId={}", userId);
    }

    /**
     * 登录收尾：解析角色、校验客户端权限、写入上下文、生成并缓存 Token、组装响应
     */
    private UserInfoResponse doLogin(UserDto user, Integer clientType) {
        Integer role = resolveRole(user.getUserId());
        // 校验登录设备类型和用户的角色，判断用户是否可以登录
        checkClientPermission(clientType, role);
        user.setRole(role);
        // 设置本地用户信息
        UserInfoContext.setLoginUser(new LoginUser(user.getUserId(), role, clientType));
        String token = jwtTokenGenerator.generateToken(user.getUserId(), role, clientType);
        long tokenTimeout = jwtTokenGenerator.getTokenTimeout();
        cacheService.setUserToken(user.getUserId(), token, tokenTimeout);

        return UserInfoResponse.of(token, tokenTimeout, LoginUserInfoVO.from(user));
    }

    /**
     * 发送短信
     */
    private void sendSmsCode(String phone, String code) {
        // 调用第三方短信服务
        log.info("发送短信：phone={}", phone);
    }

    /**
     * 手机号登录参数校验
     */
    private void validateParams(String phone, String code) {
        validatePhone(phone);
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
     * 手机号格式校验
     */
    private void validatePhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (!phone.matches(AuthConstants.PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }
    }

    /**
     * 验证验证码，失败累计次数，超过上限需重新发送
     */
    private void verifyCode(String phone, String code) {
        String cacheCode = cacheService.getSmsCodeCache(phone);
        if (cacheCode == null || !cacheCode.equals(code)) {
            long retryCount = cacheService.incrSmsCodeRetryCount(phone);
            if (retryCount >= AuthConstants.SMS_CODE_MAX_RETRY_COUNT) {
                cacheService.delSmsCodeCache(phone);
                cacheService.delSmsCodeRetryCount(phone);
                throw new BusinessException("验证码错误次数过多，请重新发送验证码");
            }
            throw new BusinessException("验证码存在问题，请重新发送验证码");
        }
        // 删除缓存中的 code，防止重复使用
        cacheService.delSmsCodeCache(phone);
        cacheService.delSmsCodeRetryCount(phone);
    }

    /**
     * 查询或注册用户
     */
    private UserDto buildUserInfoDto(String phone) {
        UserDto userInfoDto = userService.queryByPhone(phone);
        // 用户存在，校验状态后返回
        if (userInfoDto != null) {
            if (Objects.equals(userInfoDto.getStatus(), StatusEnum.DISABLED.getCode())) {
                throw new BusinessException("该账号 [" + phone + "] 已被禁用");
            }
            if (Objects.equals(userInfoDto.getDeleted(), DeleteEnum.DELETED.getCode())) {
                throw new BusinessException("该账号 [" + phone + "] 已被注销");
            }
            return userInfoDto;
        }
        // 用户不存在，注册用户
        UserDto user = userService.registerUser(phone);
        if (user == null) {
            throw new BusinessException("用户注册失败");
        }
        return user;
    }

    /**
     * 查询用户角色，未查到默认普通用户
     */
    private Integer resolveRole(Long userId) {
        Integer role = roleService.queryRoleByUserId(userId);
        return role == null ? RoleTypeEnum.USER.getCode() : role;
    }

    /**
     * 客户端类型默认手机端
     */
    private Integer resolveClientType(Integer clientType) {
        return clientType == null ? ClientTypeEnum.APP.getCode() : clientType;
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

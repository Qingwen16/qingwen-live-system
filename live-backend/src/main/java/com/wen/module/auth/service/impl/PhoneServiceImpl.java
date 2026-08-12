package com.wen.module.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wen.common.enums.DeleteEnum;
import com.wen.common.enums.StatusEnum;
import com.wen.module.auth.domain.vo.PhoneLoginRequest;
import com.wen.common.constant.AuthConstants;
import com.wen.module.auth.domain.entity.SmsCode;
import com.wen.module.auth.domain.vo.TokenDto;
import com.wen.module.auth.mapper.SmsCodeMapper;
import com.wen.module.auth.service.PhoneService;
import com.wen.common.exception.BusinessException;
import com.wen.common.generator.JwtTokenGenerator;
import com.wen.common.generator.SmsCodeGenerator;
import com.wen.module.auth.service.CacheService;
import com.wen.module.auth.domain.vo.SmsCodeRequest;
import com.wen.module.user.domain.vo.UserInfoDto;
import com.wen.module.user.domain.vo.UserInfoResponse;
import com.wen.module.user.service.UserService;
import com.wen.utils.UserInfoContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 手机验证码登录处理器
 * 当前唯一实现的登录方式
 *
 * @author jwruan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService {

    private final JwtTokenGenerator jwtTokenGenerator;

    private final CacheService cacheService;

    private final UserService userService;

    private final SmsCodeMapper smsCodeMapper;

    /**
     * 发送验证码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSmsCode(SmsCodeRequest request) {
        // 1. 校验手机号
        if (StrUtil.isBlank(request.getPhone())) {
            throw new BusinessException("手机号不能为空");
        }
        if (!request.getPhone().matches(AuthConstants.PHONE_REGEX)) {
            throw new BusinessException("手机号格式不正确");
        }

        // 2. 不考虑发送次数检查，只检查发送间隔
        if (cacheService.getSendSmsCodeInterval(request.getPhone())) {
            throw new RuntimeException("发送过于频繁，请" + AuthConstants.SMS_CODE_SEND_INTERVAL_SECONDS + "秒后再试");
        }

        String code = SmsCodeGenerator.generateCode();
        long currentTime = System.currentTimeMillis();

        SmsCode smsCode = createSmsCode(request, code, currentTime);
        smsCodeMapper.insert(smsCode);

        // 存缓存验证码，存缓存这个手机发送验证码的时间间隔
        cacheService.setSmsCodeCache(request.getPhone(), code);
        cacheService.setSendSmsCodeInterval(request.getPhone());

        // 3. 调用短信服务商发送短信
        sendSmsCode(request.getPhone(), code);
        log.info("【验证码】手机号={}, 验证码={}, 有效期 5 分钟", request.getPhone(), code);
        System.out.println("【青问直播】您的验证码是：" + code + "，5 分钟内有效");
    }

    /**
     * 创建存储数据
     */
    private SmsCode createSmsCode(SmsCodeRequest request, String code, long currentTime) {
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(request.getPhone());
        smsCode.setCode(code);
        smsCode.setType(request.getType());
        smsCode.setSendIp(request.getIp());
        smsCode.setExpireTime(currentTime + AuthConstants.SMS_CODE_EXPIRE_MINUTES * 60 * 1000);
        smsCode.setUpdateTime(currentTime);
        smsCode.setCreateTime(currentTime);
        return smsCode;
    }

    /**
     * 发送短信
     */
    private void sendSmsCode(String phone, String code) {
        // 调用第三方短信服务
        log.info("发送短信：phone={}, code={}", phone, code);
    }

    /**
     * 用户获取到了验证码进行登录（如果存在该手机的用户直接登录，没有就直接注册）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoResponse loginByPhone(PhoneLoginRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();
        String ip = request.getIp();
        // 1. 参数校验
        validateParams(phone, code);
        // 2. 验证验证码
        verifyCode(phone, code, ip);
        // 3. 查询用户
        UserInfoDto user = buildUserInfoDto(phone);
        // 4. 登录成功，将用户信息存入本地
        UserInfoContext.setUserInfo(user);
        // 5. 生成 Token
        TokenDto tokenDto = jwtTokenGenerator.generateToken(user.getUserId());
        // 6. 缓存 Token
        cacheService.setUserToken(user.getUserId(), tokenDto.getToken(),
                jwtTokenGenerator.getTokenTimeout());
        // 5. 构建响应
        UserInfoResponse response = new UserInfoResponse();
        response.setUserInfoDto(user);
        response.setTokenDto(tokenDto);
        return response;
    }

    /**
     * 参数校验
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
     * 验证验证码
     */
    private void verifyCode(String phone, String code, String ip) {
        // 缓存获取验证码
        String cacheCode = cacheService.getSmsCodeCache(phone);
        if (cacheCode == null) {
            throw new BusinessException("验证码存在问题，请重新发送验证码");
        }
        if (!cacheCode.equals(code)) {
            throw new BusinessException("验证码存在问题，请重新发送验证码");
        }
        long currentTime = System.currentTimeMillis();
        // 设置验证码被使用
        LambdaUpdateWrapper<SmsCode> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SmsCode::getPhone, phone)
                .eq(SmsCode::getCode, code)
                .set(SmsCode::getUsedTime, currentTime)
                .set(SmsCode::getUpdateTime, currentTime)
                .set(SmsCode::getUsedIp, ip);
        smsCodeMapper.update(updateWrapper);
        // 更新缓存，清楚缓存中的code，防止重复使用
        cacheService.delSmsCodeCache(phone);
    }

    /**
     * 查询用户信息
     */
    private UserInfoDto buildUserInfoDto(String phone) {
        UserInfoDto userInfoDto = userService.queryByPhone(phone);
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


}

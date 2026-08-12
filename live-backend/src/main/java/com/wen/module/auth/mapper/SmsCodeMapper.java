package com.wen.module.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.module.auth.domain.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author : 青灯文案
 * @Date: 2026/3/14 17:25
 * 手机号验证码 Mapper
 */
@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {
}

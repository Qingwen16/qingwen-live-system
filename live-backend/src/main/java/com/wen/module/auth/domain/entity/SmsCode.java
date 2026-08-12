package com.wen.module.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机号验证码实体类
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sms_code")
public class SmsCode {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 验证码
     */
    private String code;

    /**
     * 类型：1-注册登录 2-绑定手机 3-找回密码
     */
    private Integer type;

    /**
     * 发送IP地址（用于安全防护）
     */
    private String sendIp;

    /**
     * 使用IP地址（用于安全防护）
     */
    private String usedIp;

    /**
     * 使用时间（毫秒时间戳）
     */
    private Long usedTime;

    /**
     * 过期时间（毫秒时间戳）
     */
    private Long expireTime;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updateTime;
}
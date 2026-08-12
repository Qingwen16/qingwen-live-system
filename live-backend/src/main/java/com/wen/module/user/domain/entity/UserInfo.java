package com.wen.module.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 用户信息实体类
 * 只存储用户基本信息，不包含任何登录认证信息
 */
@TableName("user_info")
@Data
public class UserInfo {
    /**
     * 主键 ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户唯一标识（10 位数字，类似 QQ 号）
     */
    private Long userId;

    /**
     * 用户名（系统自动生成或用户自定义）
     */
    private String username;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地址
     */
    private String address;

    /**
     * 邮编
     */
    private String zipCode;

    /**
     * 用户状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 用户是否注销 0-注销 1-未注销
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;
}

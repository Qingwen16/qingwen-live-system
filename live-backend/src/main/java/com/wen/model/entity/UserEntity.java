package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 用户信息实体类
 * 只存储用户基本信息，不包含任何登录认证信息
 */
@TableName("user_entity")
@Data
public class UserEntity {

    /**
     * 用户唯一标识（雪花算法生成，作为主键）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    /**
     * 用户名（系统自动生成或用户自定义）
     */
    private String username;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 性别 0-未知 1-男 2-女
     */
    private Integer gender;

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
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}

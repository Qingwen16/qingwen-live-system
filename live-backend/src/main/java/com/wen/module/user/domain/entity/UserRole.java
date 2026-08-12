package com.wen.module.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联实体类
 * @author : rjw
 * @date : 2026-03-16
 */
@Data
@TableName("user_role")
public class UserRole implements Serializable {

    /**
     * 主键ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名字
     */
    private String userName;

    /**
     * 用户手机
     */
    private String phone;

    /**
     * 角色类型
     */
    private Integer role;

    /**
     * 创建时间（时间戳）
     */
    private Long createTime;

    /**
     * 更新时间（时间戳）
     */
    private Long updateTime;

}
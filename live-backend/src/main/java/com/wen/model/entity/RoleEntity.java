package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户角色关联实体类
 * @author : rjw
 * @date : 2026-03-16
 */
@Data
@TableName("role_entity")
public class RoleEntity {

    /**
     * 用户ID，复用作为角色表主键（一个用户一条角色记录）
     */
    @TableId(type = IdType.INPUT)
    private Long userId;

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
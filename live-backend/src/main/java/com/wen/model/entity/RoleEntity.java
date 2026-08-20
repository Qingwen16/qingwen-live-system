package com.wen.model.entity;

import com.baomidou.mybatisplus.annotation.*;
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
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（唯一索引，一个用户一条角色记录）
     */
    private Long userId;

    /**
     * 角色类型
     */
    private Integer role;

    /**
     * 删除标记 {@link com.wen.common.enums.DeleteEnum} 0-已删除 1-未删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
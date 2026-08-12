package com.wen.module.user.domain.vo;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@Data
public class UserRoleDto {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名字
     */
    private Long userName;

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

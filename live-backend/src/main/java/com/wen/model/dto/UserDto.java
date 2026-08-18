package com.wen.model.dto;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-03-18
 */
@Data
public class UserDto {

    /**
     * 用户唯一标识
     */
    private Long userId;

    /**
     * 用户名
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
     * 角色类型 {@link com.wen.common.enums.RoleTypeEnum} 0-用户 1-主播 2-管理员 3-系统管理员
     */
    private Integer role;

    /**
     * 用户状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 用户是否注销 0-注销 1-未注销
     */
    private Integer deleted;

}

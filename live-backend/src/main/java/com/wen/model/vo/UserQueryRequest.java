package com.wen.model.vo;

import lombok.Data;

/**
 * @author : rjw
 * @date : 2026-04-09
 */
@Data
public class UserQueryRequest {
    /**
     * 页码，从 1 开始
     */
    private long pageNum = 1;

    /**
     * 每页大小
     */
    private long pageSize = 20;

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
     * 角色类型，取值见 RoleTypeEnum（0-用户 1-主播 2-管理员 3-系统管理员）
     */
    private Integer role;

    /**
     * 用户是否注销 0-注销 1-未注销；不传默认仅查未注销
     */
    private Integer deleted;

    /**
     * 注册时间起点（时间戳，含）
     */
    private Long createTimeStart;

    /**
     * 注册时间终点（时间戳，含）
     */
    private Long createTimeEnd;

}

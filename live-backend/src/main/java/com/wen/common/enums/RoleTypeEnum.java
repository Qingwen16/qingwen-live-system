package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author : 青灯文案
 * @Date: 2026/3/16 20:29
 * 用户角色类型枚举 - 用于区分用户在平台中的身份和权限
 */
@AllArgsConstructor
@Getter
public enum RoleTypeEnum {

    /**
     * 普通用户 (手机端基础用户，具有观看、关注、评论等基础权限)
     */
    USER(0, "用户"),

    /**
     * 主播 (手机端已开通直播间的主播用户)
     */
    ANCHOR(1, "主播"),

    /**
     * 管理员 (手机端平台运营，具有内容审核、用户管理等权限)
     */
    ADMIN(2, "管理员"),

    /**
     * 系统管理员 (管理端后台最高权限)
     */
    SUPER_ADMIN(3, "系统管理员");

    private final int code;

    private final String desc;
}

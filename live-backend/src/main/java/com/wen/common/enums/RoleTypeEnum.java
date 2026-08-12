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
     * 普通用户 (基础用户角色，具有观看、关注、评论等基础权限)
     */
    USER(0, "用户"),

    /**
     * 主播 (已开通直播间，可以进行直播活动的主播用户)
     */
    ANCHOR(1, "主播"),

    /**
     * 房管 (负责管理特定直播间的房间管理员，具有禁言、踢人等权限)
     */
    ROOM(2, "房管"),

    /**
     * 普通管理员 (平台级管理员，具有内容审核、用户管理等权限)
     */
    ADMIN(3, "管理员");

    private final int code;

    private final String desc;
}

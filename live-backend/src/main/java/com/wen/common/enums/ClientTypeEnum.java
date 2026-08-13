package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户端类型枚举 - 用于区分登录来源
 *
 * @author jwruan
 */
@AllArgsConstructor
@Getter
public enum ClientTypeEnum {

    /**
     * 手机端（直播 App）
     */
    APP(0, "手机端"),

    /**
     * 管理端（管理系统 Web）
     */
    WEB(1, "管理端");

    private final int code;

    private final String desc;
}

package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户删除状态枚举
 * @author jwruan
 */
@AllArgsConstructor
@Getter
public enum DeleteEnum {

    /**
     * 用户信息的状态
     */
    DELETED(0, "已注销"),

    ACTIVE(1, "未注销");

    private final int code;

    private final String desc;
}

package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@AllArgsConstructor
@Getter
public enum StatusEnum {

    /**
     * 禁用（封号、违规等），正常（可正常使用所有功能）
     */
    DISABLED(0, "禁用"),

    NORMAL(1, "正常");

    private final int code;

    private final String desc;

}

package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否默认枚举
 */
@AllArgsConstructor
@Getter
public enum DefaultEnum {

    NO(0, "否"),

    YES(1, "是");

    private final int code;

    private final String desc;

}

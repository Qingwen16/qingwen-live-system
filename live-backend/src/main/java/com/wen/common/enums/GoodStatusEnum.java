package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@AllArgsConstructor
@Getter
public enum GoodStatusEnum {

    /**
     * 未上架（新建商品，待上架）
     */
    NOT_LISTED(0, "未上架"),

    /**
     * 已上架（正常销售中）
     */
    LISTED(1, "已上架"),

    /**
     * 缺货（库存不足，暂停销售）
     */
    OUT_OF_STOCK(2, "缺货");

    private final int code;

    private final String desc;

}
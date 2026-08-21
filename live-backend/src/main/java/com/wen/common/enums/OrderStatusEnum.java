package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@AllArgsConstructor
@Getter
public enum OrderStatusEnum {

    /**
     * 0-待支付
     */
    UNPAID(0, "待支付"),

    /**
     * 1-已支付
     */
    PAID(1, "已支付"),

    /**
     * 2-已取消
     */
    CANCELLED(2, "已取消"),

    /**
     * 3-已完成
     */
    COMPLETED(3, "已完成");

    private final int code;

    private final String desc;

    /**
     * 根据角色 code 获取枚举，未匹配返回 null
     */
    public static OrderStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatusEnum orderStatusEnum : values()) {
            if (orderStatusEnum.code == code) {
                return orderStatusEnum;
            }
        }
        return null;
    }

}

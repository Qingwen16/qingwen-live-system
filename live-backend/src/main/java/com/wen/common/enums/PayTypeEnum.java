package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@AllArgsConstructor
@Getter
public enum PayTypeEnum {

    /**
     * 1-支付宝
     */
    ALIPAY(1, "支付宝"),

    /**
     * 2-微信
     */
    WECHAT(2, "微信"),

    /**
     * 3-银行卡
     */
    BANK_CARD(3, "银行卡");

    private final int code;

    private final String desc;

    /**
     * 根据角色 code 获取枚举，未匹配返回 null
     */
    public static PayTypeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (PayTypeEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }

}

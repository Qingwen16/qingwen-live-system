package com.wen.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : rjw
 * @date : 2026-03-16
 */
@AllArgsConstructor
@Getter
public enum LiveRoomStatus {

    /**
     * 直播间状态
     */
    NOT_STARTED(0, "未开播"),

    LIVING(1, "直播中"),

    PLAYBACK(2, "回放"),

    CLOSED(3, "关闭");

    private final int code;

    private final String desc;

}

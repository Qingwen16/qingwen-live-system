package com.wen.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 弹幕删除事件（广播到直播间，前端收到后实时移除对应消息）
 *
 * @author : rjw
 */
@Data
@AllArgsConstructor
public class ChatDeleteEvent {

    /**
     * 删除事件类型
     */
    public static final String TYPE_DELETE = "delete";

    /**
     * 事件类型，固定为 delete
     */
    private String type;

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 被删除的消息 ID
     */
    private Long messageId;

}

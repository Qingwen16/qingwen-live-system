package com.wen.model.vo;

import lombok.Data;

/**
 * 发送聊天消息请求（STOMP 消息体）
 *
 * @author : rjw
 */
@Data
public class ChatSendRequest {

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 消息内容
     */
    private String content;

}

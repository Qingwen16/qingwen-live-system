package com.wen.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送聊天消息请求（STOMP 消息体）
 *
 * @author : rjw
 */
@Data
public class MessageSendRequest {

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 消息内容
     */
    private String content;

}

package com.wen.model.dto;

import lombok.Data;

/**
 * 聊天消息返回对象（广播与历史查询共用）
 *
 * @author : rjw
 */
@Data
public class ChatMessageDto {

    /**
     * 直播间 ID
     */
    private Long roomId;

    /**
     * 发言用户 ID
     */
    private Long userId;

    /**
     * 发言用户名
     */
    private String username;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间（时间戳）
     */
    private Long createTime;

}

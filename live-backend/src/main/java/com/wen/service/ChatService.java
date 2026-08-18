package com.wen.service;

import com.wen.model.dto.ChatMessageDto;
import com.wen.model.vo.ChatSendRequest;

import java.util.List;

/**
 * 直播间聊天服务
 *
 * @author : rjw
 */
public interface ChatService {

    /**
     * 发送聊天消息（校验 + 落库 + 返回消息对象）
     */
    ChatMessageDto sendMessage(Long userId, ChatSendRequest request);

    /**
     * 查询直播间历史消息（按时间正序返回最近 N 条）
     */
    List<ChatMessageDto> queryHistory(Long roomId, Integer limit);

}

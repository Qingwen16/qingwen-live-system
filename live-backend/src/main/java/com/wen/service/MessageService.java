package com.wen.service;

import com.wen.common.response.PageResult;
import com.wen.model.dto.MessageDto;
import com.wen.model.vo.MessageQueryWebRequest;
import com.wen.model.vo.MessageIdRequest;
import com.wen.model.vo.MessageSendRequest;

import java.util.List;

/**
 * 直播间聊天服务
 *
 * @author : rjw
 */
public interface MessageService {

    /**
     * 发送聊天消息（校验 + 落库 + 返回消息对象）
     */
    MessageDto sendMessage(Long userId, MessageSendRequest request);

    /**
     * 查询直播间历史消息（按时间正序返回最近 N 条）
     */
    List<MessageDto> getRoomMessage(Long roomId, Integer limit);

    /**
     * 后台管理分页查询直播间消息（含已删除，超管专用）
     */
    PageResult<MessageDto> getWebMessage(MessageQueryWebRequest request);

    /**
     * 删除弹幕（逻辑删除，主播仅限自己直播间，管理员/超管任意），返回被删消息所在直播间 ID
     */
    Long deleteMessage(MessageIdRequest request);

}


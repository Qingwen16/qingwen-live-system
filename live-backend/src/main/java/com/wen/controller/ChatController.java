package com.wen.controller;

import com.wen.common.response.Response;
import com.wen.model.vo.ChatHistoryRequest;
import com.wen.model.dto.ChatMessageDto;
import com.wen.model.vo.ChatSendRequest;
import com.wen.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 直播间聊天控制器
 *
 * @author : rjw
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 接收客户端弹幕（STOMP：/app/chat.send），广播到对应房间
     */
    @MessageMapping("/send")
    public void send(ChatSendRequest request, SimpMessageHeaderAccessor accessor) {
        Long userId = accessor.getSessionAttributes() != null
                ? (Long) accessor.getSessionAttributes().get("userId")
                : null;
        ChatMessageDto message = chatService.sendMessage(userId, request);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    /**
     * 查询直播间历史消息
     */
    @PostMapping("/history")
    public Response<List<ChatMessageDto>> history(@RequestBody ChatHistoryRequest request) {
        return Response.success(chatService.queryHistory(request.getRoomId(), request.getLimit()));
    }

}

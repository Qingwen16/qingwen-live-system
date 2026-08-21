package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.PageResult;
import com.wen.common.response.Response;
import com.wen.model.dto.ChatDeleteEvent;
import com.wen.model.dto.MessageDto;
import com.wen.model.vo.MessageQueryWebRequest;
import com.wen.model.vo.MessageIdRequest;
import com.wen.model.vo.MessageQueryRequest;
import com.wen.model.vo.MessageSendRequest;
import com.wen.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * 直播间聊天控制器
 *
 * @author : rjw
 */
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 接收客户端弹幕（STOMP：/app/chat.send），广播到对应房间
     */
    @MessageMapping("/send")
    public void send(MessageSendRequest request, Principal principal) {
        Long userId = principal == null ? null : Long.valueOf(principal.getName());
        MessageDto message = messageService.sendMessage(userId, request);
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    /**
     * 查询直播间历史消息
     */
    @PostMapping("/room/get")
    public Response<List<MessageDto>> getRoomMessage(@Valid @RequestBody MessageQueryRequest request) {
        return Response.success(messageService.getRoomMessage(request.getRoomId(), request.getLimit()));
    }

    /**
     * 后台管理查询直播间消息（含已删除，超管专用）
     */
    @PostMapping("/web/get")
    @RequireRole({RoleTypeEnum.SUPER_ADMIN})
    public Response<PageResult<MessageDto>> getWebMessage(@Valid @RequestBody MessageQueryWebRequest request) {
        return Response.success(messageService.getWebMessage(request));
    }

    /**
     * 删除弹幕（主播仅限自己直播间，管理员/超管任意），删除后广播事件实时移除
     */
    @PostMapping("/delete")
    @RequireRole({RoleTypeEnum.ANCHOR, RoleTypeEnum.ADMIN})
    public Response<Void> deleteMessage(@Valid @RequestBody MessageIdRequest request) {
        Long roomId = messageService.deleteMessage(request);
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                new ChatDeleteEvent(ChatDeleteEvent.TYPE_DELETE, roomId, request.getMessageId()));
        return Response.success(null, "消息已删除");
    }

}

package com.wen.listener;

import com.wen.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直播间在线人数监听器
 * 用户订阅房间 topic 时 +1，断开连接时 -1
 *
 * @author : rjw
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoomViewerListener {

    private static final String ROOM_TOPIC_PREFIX = "/topic/room/";

    private final RoomService roomService;

    // sessionId -> 已订阅房间集合，连接断开时据此扣减在线人数
    private final ConcurrentHashMap<String, Set<Long>> sessionRooms = new ConcurrentHashMap<>();

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        Long roomId = parseRoomId(event.getMessage());
        String sessionId = SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
        if (roomId == null || sessionId == null) {
            return;
        }
        sessionRooms.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(roomId);
        roomService.incrementViewers(roomId);
        log.info("session [{}] 进入直播间 [{}]", sessionId, roomId);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
        if (sessionId == null) {
            return;
        }
        Set<Long> rooms = sessionRooms.remove(sessionId);
        if (rooms == null) {
            return;
        }
        for (Long roomId : rooms) {
            roomService.decrementViewers(roomId);
        }
        log.info("session [{}] 离开直播间 [{}]", sessionId, rooms);
    }

    private Long parseRoomId(Message<?> message) {
        String destination = SimpMessageHeaderAccessor.getDestination(message.getHeaders());
        if (destination == null || !destination.startsWith(ROOM_TOPIC_PREFIX)) {
            return null;
        }
        String roomIdStr = destination.substring(ROOM_TOPIC_PREFIX.length());
        try {
            return Long.parseLong(roomIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}

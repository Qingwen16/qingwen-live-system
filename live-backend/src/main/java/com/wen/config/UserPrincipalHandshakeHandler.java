package com.wen.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket 握手处理器：把握手阶段解析出的 userId 包装为 Principal，
 * 使 @MessageMapping 方法可直接注入 Principal 获取当前用户
 *
 * @author : rjw
 */
public class UserPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object userId = attributes.get("userId");
        if (userId == null) {
            return null;
        }
        return () -> String.valueOf(userId);
    }

}

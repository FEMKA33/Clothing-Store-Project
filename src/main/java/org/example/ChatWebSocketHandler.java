package org.example;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long discussionId = getDiscussionId(session);
        rooms.putIfAbsent(discussionId, new ConcurrentHashMap<>());
        rooms.get(discussionId).put(session.getId(), session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long discussionId = getDiscussionId(session);
        for (WebSocketSession s : rooms.getOrDefault(discussionId, Map.of()).values()) {
            if (s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long discussionId = getDiscussionId(session);
        rooms.getOrDefault(discussionId, Map.of()).remove(session.getId());
    }

    private Long getDiscussionId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return Long.valueOf(path.substring(path.lastIndexOf("/") + 1));
    }
}
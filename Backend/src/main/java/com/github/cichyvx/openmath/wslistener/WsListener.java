package com.github.cichyvx.openmath.wslistener;

import org.springframework.web.socket.WebSocketSession;

public interface WsListener<T> {
    String path();
    void process(WebSocketSession session, Object message);
}

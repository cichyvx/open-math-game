package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.model.ConnectionRequest;
import com.github.cichyvx.openmath.session.SessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ConnectionListener implements WsListener<ConnectionRequest> {

    private final SessionHandler sessionHandler;

    public ConnectionListener(SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    @Override
    public String path() {
        return "/connect";
    }

    @Override
    public void process(WebSocketSession session, Object message) {
        sessionHandler.registerSession(session, (ConnectionRequest) message);
    }
}

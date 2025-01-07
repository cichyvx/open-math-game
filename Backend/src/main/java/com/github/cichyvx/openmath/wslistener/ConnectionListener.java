package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.model.ConnectionRequest;
import com.github.cichyvx.openmath.model.StatusChangeResponse;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ConnectionListener implements WsListener<ConnectionRequest> {

    private final SessionHandler sessionHandler;
    private final WebSocketMessageSender webSocketMessageSender;

    public ConnectionListener(SessionHandler sessionHandler, WebSocketMessageSender webSocketMessageSender) {
        this.sessionHandler = sessionHandler;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    @Override
    public String path() {
        return "/connect";
    }

    @Override
    public void process(WebSocketSession session, Object message) {
        var userData = sessionHandler.registerSession(session, (ConnectionRequest) message);
        webSocketMessageSender.sendMessage(session.getId(), new StatusChangeResponse(userData.state()));
    }
}

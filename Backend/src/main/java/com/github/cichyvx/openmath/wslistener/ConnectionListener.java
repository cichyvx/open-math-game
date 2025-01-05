package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.model.ConnectionRequest;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.wsproducer.StatusChangeProducer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ConnectionListener implements WsListener<ConnectionRequest> {

    private final SessionHandler sessionHandler;
    private final StatusChangeProducer statusChangeProducer;

    public ConnectionListener(SessionHandler sessionHandler, StatusChangeProducer statusChangeProducer) {
        this.sessionHandler = sessionHandler;
        this.statusChangeProducer = statusChangeProducer;
    }

    @Override
    public String path() {
        return "/connect";
    }

    @Override
    public void process(WebSocketSession session, Object message) {
        var userData = sessionHandler.registerSession(session, (ConnectionRequest) message);
        statusChangeProducer.sendStatusChange(session, userData.state());
    }
}

package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.model.response.ErrorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.util.Optional;

@Controller
public class WebSocketHandler extends BinaryWebSocketHandler {

    private final static Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private final WebSocketMessagePathMapper pathMapper;
    private final WebSocketDeserializer webSocketDeserializer;
    private final SessionHandler sessionHandler;

    WebSocketHandler(WebSocketMessagePathMapper pathMapper, WebSocketDeserializer webSocketDeserializer,
                            SessionHandler sessionHandler) {
        this.pathMapper = pathMapper;
        this.webSocketDeserializer = webSocketDeserializer;
        this.sessionHandler = sessionHandler;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        var response = pathMapper.processMessage(session, message);
        sendErrorMessageIfPresent(session, response);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionHandler.createPlaintSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionHandler.removeSession(session);
    }

    /**
     * TODO note
     * sending response in websocket should by optional, and mapped by request by some filed. In future
     * it would by migrated as something like:
     * request {
     *     ...
     *     "debugId": bdfb8705-6809-77dd-e5f2-7bd0c59c2a09
     *     ...
     * }
     * response {
     *      ...
     *     "forMessageId": bdfb8705-6809-77dd-e5f2-7bd0c59c2a09
     *     ...
     * }
     * at this moment is no more profit from returning exception handling in async websockets
     */
    private void sendErrorMessageIfPresent(WebSocketSession session, Optional<ErrorData> response) {
        if (response.isPresent()) {
            try {
                session.sendMessage(webSocketDeserializer.mapAsWsResponse(response.get()));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }
}

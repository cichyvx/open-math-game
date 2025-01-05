package com.github.cichyvx.openmath.wsproducer;

import com.github.cichyvx.openmath.model.StatusChangeResponse;
import com.github.cichyvx.openmath.session.UserState;
import com.github.cichyvx.openmath.util.WebSocketDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class StatusChangeProducer {

    private final static Logger log = LoggerFactory.getLogger(StatusChangeProducer.class);
    private final WebSocketDeserializer webSocketDeserializer;

    public StatusChangeProducer(WebSocketDeserializer webSocketDeserializer) {
        this.webSocketDeserializer = webSocketDeserializer;
    }

    @Async
    public void sendStatusChange(WebSocketSession session, UserState state) {
        try {
            session.sendMessage(webSocketDeserializer.mapAsWsResponse(new StatusChangeResponse(state)));
        } catch (IOException e) {
            log.error("error while sending status change for session {}, trying another time", session.getId(), e);
            try {
                TextMessage message = webSocketDeserializer.mapAsWsResponse(new StatusChangeResponse(state));
                session.sendMessage(message);
            } catch (IOException ex) {
                log.error("another attempt failed, interrupted message sending. session: {}, state: {} ", session.getId(), state, e);
            }
        }
    }
}

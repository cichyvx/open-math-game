package com.github.cichyvx.openmath.wsproducer;

import com.github.cichyvx.openmath.model.QuestionResponse;
import com.github.cichyvx.openmath.util.WebSocketDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class QuestionProducer {

    private static final Logger log = LoggerFactory.getLogger(QuestionProducer.class);
    private final WebSocketDeserializer deserializer;

    public QuestionProducer(WebSocketDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public void sendQuestion(WebSocketSession session, QuestionResponse questionResponse) {
        var response = deserializer.mapAsWsResponse(questionResponse);
        try {
            session.sendMessage(response);
        } catch (Exception e) {
            log.error("Could not send question, starting another attempt", e);
            try {
                session.sendMessage(response);
            } catch (Exception e2) {
                log.error("Could not send question", e2);
            }
        }
    }

}

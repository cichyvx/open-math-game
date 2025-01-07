package com.github.cichyvx.openmath.wsproducer;

import com.github.cichyvx.openmath.model.AnswerResponse;
import com.github.cichyvx.openmath.util.WebSocketDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class AnswerSender {

    private static final Logger log = LoggerFactory.getLogger(AnswerSender.class);
    private final WebSocketDeserializer deserializer;

    public AnswerSender(WebSocketDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    public void sendAnswer(WebSocketSession session, AnswerResponse answerResponse) {
        var response = deserializer.mapAsWsResponse(answerResponse);
        try {
            session.sendMessage(response);
        } catch (Exception e1) {
            log.error("error while sending answer, starting another attempt", e1);
            try {
                session.sendMessage(response);
            } catch (Exception e2) {
                log.error("error while sending answer", e2);
            }
        }
    }

}

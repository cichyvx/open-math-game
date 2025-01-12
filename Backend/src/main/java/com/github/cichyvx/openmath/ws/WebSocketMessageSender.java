package com.github.cichyvx.openmath.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class WebSocketMessageSender {

    private final static PriorityBlockingQueue<Message> messages = new PriorityBlockingQueue<>();
    private static final Logger log = LoggerFactory.getLogger(WebSocketMessageSender.class);
    public static final int RESEND_LIMIT = 5;
    private final WebSocketDeserializer deserializer;
    private final SessionHandler sessionHandler;

    public WebSocketMessageSender(WebSocketDeserializer deserializer, SessionHandler sessionHandler) {
        this.deserializer = deserializer;
        this.sessionHandler = sessionHandler;
    }

    public void sendMessage(String sessionId, Object message) {
        TextMessage convertedMessage = deserializer.mapAsWsResponse(message);
        WebSocketSession session = sessionHandler.getSession(sessionId).orElseThrow().session();
        messages.offer(new Message(session, convertedMessage, Instant.now(), 0));
    }

    @Scheduled(fixedRate = 20)
    private void sender() {
        boolean areMoreMessages = true;
        while (areMoreMessages) {
            Message message = messages.poll();

            if (anyMessagesReadyToSend(message)) {
                if (polledButNotReady(message)) {
                    messages.offer(message);
                }
                areMoreMessages = false;
            } else {
                try {
                    send(message);
                } catch (Exception ex) {
                    trySendLater(ex, message);
                }
            }
        }
    }

    private void trySendLater(Exception ex, Message message) {
        log.warn("error while sending message. Session Id: {}, message: {}", message.message, message.session.getId(),
                ex);
        int resendCount = message.resendCount + 1;

        if (resendCount >= RESEND_LIMIT) {
            log.error("resend limit exceeded. Session Id: {}, message: {}", message.message, message.session.getId());
        } else {
            Message tryLaterMessage = new Message(
                    message.session,
                    message.message,
                    Instant.now().plus(2L, ChronoUnit.SECONDS),
                    resendCount
            );

            messages.offer(tryLaterMessage);
        }
    }

    private void send(Message message) throws IOException {
        message.session.sendMessage(message.message);
    }

    private boolean polledButNotReady(Message message) {
        return message != null;
    }

    private boolean anyMessagesReadyToSend(Message message) {
        return message == null || Instant.now().isBefore(message.timestamp);
    }

    private record Message(WebSocketSession session, TextMessage message, Instant timestamp, int resendCount) implements Comparable<Message>{
        @Override
        public int compareTo(Message o) {
            return timestamp.compareTo(o.timestamp);
        }
    }
}

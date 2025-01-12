package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.config.OpenMathConfig;
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
    private final WebSocketDeserializer deserializer;
    private final SessionHandler sessionHandler;
    private final OpenMathConfig config;

    public WebSocketMessageSender(WebSocketDeserializer deserializer, SessionHandler sessionHandler,
                                  OpenMathConfig config) {
        this.deserializer = deserializer;
        this.sessionHandler = sessionHandler;
        this.config = config;
    }

    public void sendMessage(String sessionId, Object message) {
        TextMessage convertedMessage = deserializer.mapAsWsResponse(message);
        WebSocketSession session = sessionHandler.getSession(sessionId).orElseThrow().session();
        messages.offer(new Message(session, convertedMessage, Instant.now(), 0));
    }

    @Scheduled(fixedRateString = "${openmath.send-message-to-client-task-rate}")
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

        if (resendCount >= config.getMaxSendRetry()) {
            log.error("resend limit exceeded. Session Id: {}, message: {}", message.message, message.session.getId());
        } else {
            Message tryLaterMessage = new Message(
                    message.session,
                    message.message,
                    Instant.now().plus(config.getRetryMessageDelay(), ChronoUnit.SECONDS),
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

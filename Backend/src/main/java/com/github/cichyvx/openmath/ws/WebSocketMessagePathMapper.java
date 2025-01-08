package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.exception.ExceptionHandler;
import com.github.cichyvx.openmath.exception.PathNotFound;
import com.github.cichyvx.openmath.model.ErrorData;
import com.github.cichyvx.openmath.model.GenericWsRequest;
import com.github.cichyvx.openmath.wslistener.WsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Component
public class WebSocketMessagePathMapper {

    private final static Logger log = LoggerFactory.getLogger(WebSocketMessagePathMapper.class);
    private final WebSocketDeserializer webSocketDeserializer;
    private final WsMessageValidator wsMessageValidator;
    private final WebSocketListenerMapper webSocketListenerMapper;
    private final ExceptionHandler exceptionHandler;

    WebSocketMessagePathMapper(WebSocketDeserializer webSocketDeserializer, WsMessageValidator wsMessageValidator,
                               WebSocketListenerMapper webSocketListenerMapper,
                               ExceptionHandler exceptionHandler) {
        this.webSocketDeserializer = webSocketDeserializer;
        this.wsMessageValidator = wsMessageValidator;
        this.webSocketListenerMapper = webSocketListenerMapper;
        this.exceptionHandler = exceptionHandler;
    }

    Optional<ErrorData> processMessage(WebSocketSession session, TextMessage message) {
        try {
            GenericWsRequest deserializedMessage = deserializeMessage(message);
            mapToListenerAndProcess(session.getId(), deserializedMessage);
            return Optional.empty();
        } catch (Exception ex) {
            return Optional.of(exceptionHandler.handleException(ex));
        }
    }

    private GenericWsRequest deserializeMessage(TextMessage message) {
        GenericWsRequest genericWsRequest = webSocketDeserializer.mapAsWsRequest(message.asBytes());
        wsMessageValidator.validate(genericWsRequest);
        log.debug("Received message {}", genericWsRequest);
        return genericWsRequest;
    }

    private void mapToListenerAndProcess(String session, GenericWsRequest genericWsRequest) {
        var listenerData = webSocketListenerMapper.getListener(genericWsRequest.path())
                .orElseThrow(() -> new PathNotFound(genericWsRequest.path()));

        WsListener<?> listener = listenerData.listener();
        Class<?> type = listenerData.type();

        Object convertedMsg = webSocketDeserializer.convert(genericWsRequest.data(), type);
        listener.process(session, convertedMsg);
    }
}

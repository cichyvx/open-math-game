package com.github.cichyvx.openmath.wsproducer;

import com.github.cichyvx.openmath.matchmaking.Room;
import com.github.cichyvx.openmath.model.GameInfoResponse;
import com.github.cichyvx.openmath.model.StatusChangeResponse;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.util.WebSocketDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class GameStartInfoProducer {

    private final static Logger log = LoggerFactory.getLogger(GameStartInfoProducer.class);
    private final WebSocketDeserializer webSocketDeserializer;

    public GameStartInfoProducer(WebSocketDeserializer webSocketDeserializer) {
        this.webSocketDeserializer = webSocketDeserializer;
    }

    public void sendGameInfo(WebSocketSession session, Room room, SessionHandler.UserData enemyPlayerData) {
        try {
            session.sendMessage(webSocketDeserializer.mapAsWsResponse(new GameInfoResponse(enemyPlayerData.username(), room.eventTime())));
        } catch (IOException e) {
            log.error("error while sending game info for session {}, trying another time", session.getId(), e);
            try {
                TextMessage message = webSocketDeserializer.mapAsWsResponse(new GameInfoResponse(enemyPlayerData.username(), room.eventTime()));
                session.sendMessage(message);
            } catch (IOException ex) {
                log.error("another attempt failed, interrupted message sending. session: {}, room: {} ", session.getId(), room, e);
            }
        }
    }
}

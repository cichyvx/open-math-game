package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.config.OpenMathConfig;
import com.github.cichyvx.openmath.model.response.GameInfoResponse;
import com.github.cichyvx.openmath.ws.SessionHandler;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PlayerWaitingRoom {

    private final static PriorityBlockingQueue<Room> waitingRooms = new PriorityBlockingQueue<>();
    private static final Logger log = LoggerFactory.getLogger(PlayerWaitingRoom.class);
    private final InGameRoomsHolder inGameRoomsHolder;
    private final SessionHandler sessionHandler;
    private final WebSocketMessageSender webSocketMessageSender;
    private final OpenMathConfig config;

    public PlayerWaitingRoom(InGameRoomsHolder inGameRoomsHolder,
                             SessionHandler sessionHandler, WebSocketMessageSender webSocketMessageSender,
                             OpenMathConfig config) {
        this.inGameRoomsHolder = inGameRoomsHolder;
        this.sessionHandler = sessionHandler;
        this.webSocketMessageSender = webSocketMessageSender;
        this.config = config;
    }

    public void add(String session1, String session2) {
        Room room = new Room(session1, session2, Instant.now().plus(config.getWaitingRoomTime(), ChronoUnit.SECONDS));
        waitingRooms.add(room);

        SessionHandler.UserData userData1 = sessionHandler.getSession(room.session1()).orElseThrow();
        SessionHandler.UserData userData2 = sessionHandler.getSession(room.session2()).orElseThrow();

        webSocketMessageSender.sendMessage(room.session1(), new GameInfoResponse(userData2.username(), room.eventTime()));
        webSocketMessageSender.sendMessage(room.session2(), new GameInfoResponse(userData1.username(), room.eventTime()));
    }

    @Async
    @Scheduled(fixedRateString = "${openmath.game-starting-task-rate}")
    public void addAsStarted() {
        Room room = waitingRooms.poll();
        if (room != null) {
            if (room.eventTime().isBefore(Instant.now())) {
                log.debug("starting room {}", room);
                inGameRoomsHolder.add(room);
            } else {
                waitingRooms.offer(room);
            }
        }
    }

}

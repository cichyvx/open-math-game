package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.model.GameInfoResponse;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PlayerWaitingRoom {

    private final static PriorityBlockingQueue<Room> waitingRooms = new PriorityBlockingQueue<>();
    private static final Logger log = LoggerFactory.getLogger(PlayerWaitingRoom.class);
    public static final long SECONDS_TO_WAIT = 5L;
    private final InGameRoomsHolder inGameRoomsHolder;
    private final SessionHandler sessionHandler;
    private final WebSocketMessageSender webSocketMessageSender;

    public PlayerWaitingRoom(InGameRoomsHolder inGameRoomsHolder,
                             SessionHandler sessionHandler, WebSocketMessageSender webSocketMessageSender) {
        this.inGameRoomsHolder = inGameRoomsHolder;
        this.sessionHandler = sessionHandler;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    public void add(String session1, String session2) {
        Room room = new Room(session1, session2, Instant.now().plus(SECONDS_TO_WAIT, ChronoUnit.SECONDS));
        waitingRooms.add(room);

        SessionHandler.UserData userData1 = sessionHandler.getSession(room.session1()).orElseThrow();
        SessionHandler.UserData userData2 = sessionHandler.getSession(room.session2()).orElseThrow();

        webSocketMessageSender.sendMessage(room.session1(), new GameInfoResponse(userData2.username(), room.eventTime()));
        webSocketMessageSender.sendMessage(room.session2(), new GameInfoResponse(userData1.username(), room.eventTime()));
    }

    @Scheduled(fixedRate = 10L)
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

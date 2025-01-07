package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.wsproducer.GameStartInfoProducer;
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
    private final GameStartInfoProducer gameStartInfoProducer;
    private final SessionHandler sessionHandler;

    public PlayerWaitingRoom(InGameRoomsHolder inGameRoomsHolder, GameStartInfoProducer gameStartInfoProducer,
                             SessionHandler sessionHandler) {
        this.inGameRoomsHolder = inGameRoomsHolder;
        this.gameStartInfoProducer = gameStartInfoProducer;
        this.sessionHandler = sessionHandler;
    }

    public void add(String session1, String session2) {
        Room room = new Room(session1, session2, Instant.now().plus(SECONDS_TO_WAIT, ChronoUnit.SECONDS));
        waitingRooms.add(room);

        SessionHandler.UserData userData1 = sessionHandler.getSession(room.session1()).orElseThrow();
        SessionHandler.UserData userData2 = sessionHandler.getSession(room.session2()).orElseThrow();

        gameStartInfoProducer.sendGameInfo(userData1.session(), room, userData2);
        gameStartInfoProducer.sendGameInfo(userData2.session(), room, userData1);
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

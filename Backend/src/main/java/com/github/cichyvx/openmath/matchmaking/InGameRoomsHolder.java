package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.game.GameLoop;
import com.github.cichyvx.openmath.game.MatchRoom;
import com.github.cichyvx.openmath.game.Score;
import com.github.cichyvx.openmath.model.AnswerRequest;
import com.github.cichyvx.openmath.model.AnswerResponse;
import com.github.cichyvx.openmath.model.StatusChangeResponse;
import com.github.cichyvx.openmath.ws.SessionHandler;
import com.github.cichyvx.openmath.ws.UserState;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class InGameRoomsHolder {

    private final static PriorityBlockingQueue<MatchRoom> currentInGameRooms = new PriorityBlockingQueue<>();
    private final static Map<String, MatchRoom> matchRoomsMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InGameRoomsHolder.class);
    private final SessionHandler sessionHandler;
    private final WebSocketMessageSender webSocketMessageSender;

    public InGameRoomsHolder(SessionHandler sessionHandler, WebSocketMessageSender webSocketMessageSender) {
        this.sessionHandler = sessionHandler;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    @Async
    @Scheduled(fixedRate = 100L)
    public void endGames() {
        boolean isAllEndedMatchRoomCleaned = false;
        int counter = 0;
        do {
            MatchRoom matchRoom = currentInGameRooms.poll();
            if (matchRoom == null || matchRoom.endTime().isAfter(Instant.now())) {
                isAllEndedMatchRoomCleaned = true;
                if (matchRoom != null) {
                    currentInGameRooms.offer(matchRoom);
                }

            } else {
                removeEndedMatch(matchRoom);
                counter++;
            }
        } while (!isAllEndedMatchRoomCleaned);

        if (counter > 0) {
            log.debug("ended {} match", counter);
        }
    }

    private void removeEndedMatch(MatchRoom matchRoom) {
        var session1 = matchRoom.session1();
        var session2 = matchRoom.session2();

        matchRoomsMap.remove(session1);
        matchRoomsMap.remove(session2);


        var status1 = sessionHandler.changeUserState(session1, UserState.CONNECTED).orElseThrow();
        var status2 = sessionHandler.changeUserState(session2, UserState.CONNECTED).orElseThrow();

        webSocketMessageSender.sendMessage(session1, new StatusChangeResponse(status1.state()));
        webSocketMessageSender.sendMessage(session2, new StatusChangeResponse(status2.state()));
    }

    public void add(Room room) {
        var status1 = sessionHandler.changeUserState(room.session1(), UserState.IN_GAME).orElseThrow();
        var status2 = sessionHandler.changeUserState(room.session2(), UserState.IN_GAME).orElseThrow();

        var session1 = room.session1();
        var session2 = room.session2();

        webSocketMessageSender.sendMessage(session1, new StatusChangeResponse(status1.state()));
        webSocketMessageSender.sendMessage(session2, new StatusChangeResponse(status2.state()));

        GameLoop gameLoop = new GameLoop(session1, session2, webSocketMessageSender);
        Score score = new Score(room.session1(), room.session2());
        MatchRoom matchRoom = new MatchRoom(gameLoop, Instant.now().plus(1L, ChronoUnit.MINUTES), score, session1, session2);

        currentInGameRooms.add(matchRoom);
        matchRoomsMap.put(room.session1(), matchRoom);
        matchRoomsMap.put(room.session2(), matchRoom);

        gameLoop.generateAndSendEquation();

    }


    public void answer(String sessionId, AnswerRequest message) {
        MatchRoom matchRoom = matchRoomsMap.get(sessionId);

        if (isAnswerCorrect(message, matchRoom)) {
            matchRoom.gameLoop().generateAndSendEquation();
            Score score = matchRoom.score();
            score.score(sessionId);

            String session1 = matchRoom.session1();
            String session2 = matchRoom.session2();

            int score1 = score.getScore(session1);
            int score2 = score.getScore(session2);

            webSocketMessageSender.sendMessage(session1, new AnswerResponse(score1, score2));
            webSocketMessageSender.sendMessage(session2, new AnswerResponse(score2, score1));
        }
    }

    private boolean isAnswerCorrect(AnswerRequest message, MatchRoom matchRoom) {
        return matchRoom.gameLoop().answer(message.answer());
    }
}

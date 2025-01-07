package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.game.Loop;
import com.github.cichyvx.openmath.game.MatchRoom;
import com.github.cichyvx.openmath.game.Score;
import com.github.cichyvx.openmath.model.AnswerRequest;
import com.github.cichyvx.openmath.model.AnswerResponse;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.session.UserState;
import com.github.cichyvx.openmath.wsproducer.AnswerSender;
import com.github.cichyvx.openmath.wsproducer.QuestionProducer;
import com.github.cichyvx.openmath.wsproducer.StatusChangeProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

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
    private final StatusChangeProducer statusChangeProducer;
    private final QuestionProducer questionProducer;
    private final AnswerSender answerSender;

    public InGameRoomsHolder(SessionHandler sessionHandler, StatusChangeProducer statusChangeProducer,
                             QuestionProducer questionProducer, AnswerSender answerSender) {
        this.sessionHandler = sessionHandler;
        this.statusChangeProducer = statusChangeProducer;
        this.questionProducer = questionProducer;
        this.answerSender = answerSender;
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

        matchRoomsMap.remove(session1.getId());
        matchRoomsMap.remove(session2.getId());


        var status1 = sessionHandler.changeUserState(session1.getId(), UserState.CONNECTED).orElseThrow();
        var status2 = sessionHandler.changeUserState(session2.getId(), UserState.CONNECTED).orElseThrow();

        statusChangeProducer.sendStatusChange(session1, status1.state());
        statusChangeProducer.sendStatusChange(session2, status2.state());
    }

    public void add(Room room) {
        var status1 = sessionHandler.changeUserState(room.session1(), UserState.IN_GAME).orElseThrow();
        var status2 = sessionHandler.changeUserState(room.session2(), UserState.IN_GAME).orElseThrow();

        WebSocketSession session1 = status1.session();
        WebSocketSession session2 = status2.session();

        statusChangeProducer.sendStatusChange(session1, status1.state());
        statusChangeProducer.sendStatusChange(session2, status1.state());

        Loop gameLoop = new Loop(session1, session2, questionProducer);
        Score score = new Score(room.session1(), room.session2());
        MatchRoom matchRoom = new MatchRoom(gameLoop, Instant.now().plus(1L, ChronoUnit.MINUTES), score, session1, session2);

        currentInGameRooms.add(matchRoom);
        matchRoomsMap.put(room.session1(), matchRoom);
        matchRoomsMap.put(room.session2(), matchRoom);

        gameLoop.generateAndSendEquation();

    }


    public void answer(WebSocketSession session, AnswerRequest message) {
        String sessionId = session.getId();
        MatchRoom matchRoom = matchRoomsMap.get(sessionId);

        if (isAnswerCorrect(message, matchRoom)) {
            matchRoom.loop().generateAndSendEquation();
            Score score = matchRoom.score();
            score.score(sessionId);

            WebSocketSession session1 = matchRoom.session1();
            WebSocketSession session2 = matchRoom.session2();

            int score1 = score.getScore(session1.getId());
            int score2 = score.getScore(session2.getId());

            answerSender.sendAnswer(session1, new AnswerResponse(score1, score2));
            answerSender.sendAnswer(session2, new AnswerResponse(score2, score1));
        }
    }

    private boolean isAnswerCorrect(AnswerRequest message, MatchRoom matchRoom) {
        return matchRoom.loop().answer(message.answer());
    }
}

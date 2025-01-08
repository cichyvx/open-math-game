package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.exception.AlreadyWaitingInMatchMakingException;
import com.github.cichyvx.openmath.exception.WrongUserState;
import com.github.cichyvx.openmath.model.response.StatusChangeResponse;
import com.github.cichyvx.openmath.ws.SessionHandler;
import com.github.cichyvx.openmath.ws.UserState;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class PlayerRegistration {

    private final static PriorityBlockingQueue<WaitingSession> waitingSessionIdsQueue = new PriorityBlockingQueue<>();
    private final static Set<WaitingSession> waitingSessionsSet = ConcurrentHashMap.newKeySet();

    public static final String WRONG_USER_STATE_MESSAGE = "bad current user status for starting matchmaking. Current user status is %s, it should be " + UserState.CONNECTED;
    private final SessionHandler sessionHandler;
    private final WebSocketMessageSender webSocketMessageSender;

    public PlayerRegistration(SessionHandler sessionHandler, WebSocketMessageSender webSocketMessageSender) {
        this.sessionHandler = sessionHandler;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    public void add(String sessionId) {
        WaitingSession waitingSession = new WaitingSession(sessionId, Instant.now());
        SessionHandler.UserData userData = sessionHandler.getSession(sessionId).orElseThrow(() -> new WrongUserState("user not registered"));

        if (userData.state() != UserState.CONNECTED) {
            throw new WrongUserState(WRONG_USER_STATE_MESSAGE.formatted(userData.state()));
        } else if (waitingSessionsSet.add(waitingSession)) {
            waitingSessionIdsQueue.offer(waitingSession);
            userData = sessionHandler.changeUserState(sessionId, UserState.MATCHMAKING).orElseThrow();
            webSocketMessageSender.sendMessage(sessionId, new StatusChangeResponse(userData.state()));
        } else {
            throw new AlreadyWaitingInMatchMakingException();
        }
    }

    public Optional<MatchingPlayersPair> getPairOfLongestWaitingSessionId() {
        if (waitingSessionIdsQueue.size() >= 2) {
            WaitingSession session1 = waitingSessionIdsQueue.poll();
            WaitingSession session2 = waitingSessionIdsQueue.poll();

            if (session1 == null || session2 == null) {
                rollbackPoll(session1, session2);
                return Optional.empty();
            }

            waitingSessionsSet.remove(session1);
            waitingSessionsSet.remove(session2);

            return Optional.of(new MatchingPlayersPair(session1.sessionId, session2.sessionId));
        }


        return Optional.empty();
    }

    private static void rollbackPoll(WaitingSession session1, WaitingSession session2) {
        if (session1 != null) {
            waitingSessionIdsQueue.offer(session1);
        } else {
            waitingSessionIdsQueue.offer(session2);
        }
    }

    record WaitingSession(String sessionId, Instant searchStartTime) implements Comparable<WaitingSession> {

        @Override
        public int compareTo(WaitingSession o) {
            return searchStartTime.compareTo(o.searchStartTime);
        }

        @Override
        public int hashCode() {
            return sessionId.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof WaitingSession ws && sessionId.equals(ws.sessionId);
        }
    }

}

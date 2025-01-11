package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.exception.DeserializationException;
import com.github.cichyvx.openmath.exception.SessionAlreadyExistsException;
import com.github.cichyvx.openmath.model.request.ConnectionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionHandler {

    private final static Logger log = LoggerFactory.getLogger(SessionHandler.class);
    private final static Map<String, UserData> sessions = new ConcurrentHashMap<>();

    public UserData registerSession(String sessionId, ConnectionRequest data) {
        WebSocketSession session = sessions.get(sessionId).session();

        String username = data.username();

        UserData userData = new UserData(session, username, UserState.CONNECTED);

        sessions.merge(session.getId(), userData, ((userData1, userData2) -> {
            if (userData2.state != UserState.CONNECTED) {
                log.error("Session already exists: {} --- {}", userData1.session().getId(), userData2.session().getId());
                throw new SessionAlreadyExistsException();
            }
            return userData2;
        }));

        log.debug("Registered session: {} for user: {}", session.getId(), username);

        return userData;
    }

    public Optional<UserData> getSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public Optional<UserData> changeUserState(String sessionId, UserState state) {
        return Optional.ofNullable(sessions.computeIfPresent(
                sessionId,
                ((s, userData) -> new UserData(userData.session(), userData.username(), state))
        ));
    }

    void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
    }

    void createPlaintSession(WebSocketSession session) {
        if (sessions.containsKey(session.getId())) {
            throw new SessionAlreadyExistsException();
        } else {
            sessions.put(session.getId(), new UserData(session, session.getId(), UserState.CONNECTED));
        }
    }

    public record UserData(WebSocketSession session, String username, UserState state) {
        public UserData {
            if (session == null) {
                throw new IllegalArgumentException("session");
            }

            if (username == null || username.isEmpty()) {
                throw new DeserializationException("username");
            }
        }
    }

}

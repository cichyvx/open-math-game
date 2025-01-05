package com.github.cichyvx.openmath.session;

import com.github.cichyvx.openmath.exception.DeserializationError;
import com.github.cichyvx.openmath.exception.SessionAlreadyExists;
import com.github.cichyvx.openmath.model.ConnectionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionHandler {

    private final static Logger log = LoggerFactory.getLogger(SessionHandler.class);
    private final static Map<String, UserData> sessions = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session, ConnectionRequest data) {
        String username = data.username();

        UserData userData = new UserData(session, username);

        sessions.merge(session.getId(), userData, ((userData1, userData2) -> {
            log.error("Session already exists: {} --- {}", userData1.session().getId(), userData2.session().getId());
            throw new SessionAlreadyExists();
        }));

        log.debug("Registered session: {} for user: {}", session.getId(), username);
    }

    record UserData(WebSocketSession session, String username) {
        UserData {
            if (session == null) {
                throw new IllegalArgumentException("session");
            }

            if (username == null || username.isEmpty()) {
                throw new DeserializationError("username");
            }
        }
    }

}

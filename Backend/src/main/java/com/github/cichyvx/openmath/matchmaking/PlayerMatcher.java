package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.session.UserState;
import com.github.cichyvx.openmath.wsproducer.StatusChangeProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PlayerMatcher {

    private static final Logger log = LoggerFactory.getLogger(PlayerMatcher.class);
    private final PlayerRegistration playerRegistration;
    private final SessionHandler sessionHandler;
    private final StatusChangeProducer statusChangeProducer;

    protected PlayerMatcher(PlayerRegistration playerRegistration, SessionHandler sessionHandler,
                            StatusChangeProducer statusChangeProducer) {
        this.playerRegistration = playerRegistration;
        this.sessionHandler = sessionHandler;
        this.statusChangeProducer = statusChangeProducer;
    }

    @Scheduled(fixedRate = 1000L)
    public void match() {
        var optionalPlayers = playerRegistration.getPairOfLongestWaitingSessionId();
        if (optionalPlayers.isPresent()) {
            String session1 = optionalPlayers.get().sessionId1();
            String session2 = optionalPlayers.get().sessionId2();

            log.debug("found players pair: s1: {}, s2: {}", session1, session2);

            var user1 = sessionHandler.changeUserState(session1, UserState.WAITING_FOR_GAME_START).orElseThrow();
            var user2 = sessionHandler.changeUserState(session2, UserState.WAITING_FOR_GAME_START).orElseThrow();

            statusChangeProducer.sendStatusChange(user1.session(), user1.state());
            statusChangeProducer.sendStatusChange(user2.session(), user2.state());
        } else {
            log.debug("not found any matching players pair");
        }

    }

}

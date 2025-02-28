package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.matchmaking.PlayerRegistration;
import com.github.cichyvx.openmath.model.request.MatchMakingRequest;
import org.springframework.stereotype.Component;

@Component
public class MatchMakingListener implements WsListener<MatchMakingRequest> {

    private final PlayerRegistration playerRegistration;

    public MatchMakingListener(PlayerRegistration playerRegistration) {
        this.playerRegistration = playerRegistration;
    }

    @Override
    public String path() {
        return "/matchmaking";
    }

    @Override
    public void process(String session, Object message) {
        playerRegistration.add(session);
    }
}

package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.matchmaking.InGameRoomsHolder;
import com.github.cichyvx.openmath.model.AnswerRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class AnswerListener implements WsListener<AnswerRequest> {

    private final InGameRoomsHolder inGameRoomsHolder;

    public AnswerListener(InGameRoomsHolder inGameRoomsHolder) {
        this.inGameRoomsHolder = inGameRoomsHolder;
    }

    @Override
    public String path() {
        return "/answer";
    }

    @Override
    public void process(WebSocketSession session, Object message) {
        inGameRoomsHolder.answer(session, (AnswerRequest) message);
    }
}

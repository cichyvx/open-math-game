package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.wslistener.WsListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketListenerMapperTest {

    private WsListener<String> stringListener = new WsListener<String>() {
        @Override
        public String path() {
            return "1";
        }

        @Override
        public void process(WebSocketSession session, Object message) {

        }
    };

    private WsListener<Integer> integerListener = new WsListener<Integer>() {
        @Override
        public String path() {
            return "2";
        }

        @Override
        public void process(WebSocketSession session, Object message) {

        }
    };

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void shouldFillListeners() throws ClassNotFoundException {
        var subject = new WebSocketListenerMapper(List.of(stringListener, integerListener));

        assertEquals(2, subject.listenerMap.size());

        WebSocketListenerMapper.ListenerData stringListenerData = subject.listenerMap.get("1");
        WebSocketListenerMapper.ListenerData integerListenerData = subject.listenerMap.get("2");

        assertEquals(stringListener, stringListenerData.listener());
        assertEquals(integerListener, integerListenerData.listener());

        assertEquals(String.class, stringListenerData.type());
        assertEquals(Integer.class, integerListenerData.type());
    }

    @Test
    public void shouldThrowIfListenerDuplicated() {
        assertThrows(IllegalArgumentException.class, () -> new WebSocketListenerMapper(List.of(stringListener, stringListener)));
    }

    @Test
    public void shouldReturnListener() throws ClassNotFoundException {
        var subject = new WebSocketListenerMapper(List.of(stringListener));

        assertTrue(subject.getListener("1").isPresent());
    }

    @Test
    public void shouldReturnEmptyIfListenerNotFound() throws ClassNotFoundException {
        var subject = new WebSocketListenerMapper(List.of(stringListener));

        assertTrue(subject.getListener("X").isEmpty());
    }
}

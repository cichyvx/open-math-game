package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.wslistener.WsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class WebSocketListenerMapper {

    private final static Logger log = LoggerFactory.getLogger(WebSocketListenerMapper.class);
    protected final Map<String, ListenerData> listenerMap;

    WebSocketListenerMapper(List<WsListener<?>> listeners) throws ClassNotFoundException {
        Map<String, ListenerData> wsListenerMap = new HashMap<>();

        for (WsListener<?> listener : listeners) {
            throwIfPathDuplicated(listener, wsListenerMap);
            var clazz = getListenerMessageClass(listener);

            wsListenerMap.put(listener.path(), new ListenerData(listener, clazz));
        }

        listenerMap = Collections.unmodifiableMap(wsListenerMap);
        log.debug("registered {} listeners", listenerMap.size());
    }

    public Optional<ListenerData> getListener(String path) {
        return Optional.ofNullable(listenerMap.get(path));
    }

    private Class<?> getListenerMessageClass(WsListener<?> listener) throws ClassNotFoundException {
        var type = ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        return Class.forName(type.getTypeName());
    }

    private void throwIfPathDuplicated(WsListener<?> listener, Map<String, ListenerData> wsListenerMap) {
        if (wsListenerMap.containsKey(listener.path())) {
            throw new IllegalArgumentException("Duplicate path: " + listener.path());
        }
    }

    public record ListenerData(WsListener<?> listener, Class<?> type) {}

}

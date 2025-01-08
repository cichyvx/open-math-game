package com.github.cichyvx.openmath.wslistener;

public interface WsListener<T> {
    String path();
    void process(String session, Object message);
}

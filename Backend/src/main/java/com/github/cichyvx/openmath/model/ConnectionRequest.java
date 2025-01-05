package com.github.cichyvx.openmath.model;

public record ConnectionRequest(String username) {

    public ConnectionRequest {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }
}

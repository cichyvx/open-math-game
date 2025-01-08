package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.exception.PathNotSpecified;
import com.github.cichyvx.openmath.model.request.GenericWsRequest;
import org.springframework.stereotype.Component;

@Component
public class WsMessageValidator {

    public void validate(GenericWsRequest message) {
        if (message.path() == null || message.path().isEmpty()) {
            throw new PathNotSpecified();
        }
    }

}

package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.websocket;


import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class UserHandshakeHandle extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(org.springframework.http.server.ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object pid = attributes.get("principalId");
        if (pid != null) {
            final String name = pid.toString(); // e.g. "1", "2"
            return () -> name;
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}

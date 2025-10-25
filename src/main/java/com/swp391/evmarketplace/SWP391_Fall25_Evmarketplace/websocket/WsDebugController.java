package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.websocket;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class WsDebugController {
    private final SimpMessagingTemplate messaging;
    private final AuthUtil auth;

    @PostMapping("/ping")
    public void pingMe() {
        Long me = auth.getCurrentAccountIdOrNull();
        messaging.convertAndSendToUser(String.valueOf(me), "/queue/ping",
                Map.of("type","PING","ts", Instant.now().toString()));
    }
}

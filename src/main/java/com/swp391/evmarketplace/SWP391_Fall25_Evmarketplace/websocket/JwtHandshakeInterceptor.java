package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.websocket;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                   org.springframework.http.server.ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String token = null;

        var params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        token = params.getFirst("token");

        // 2. Nếu không có, thử lấy từ header Authorization
        if (token == null) {
            var auths = request.getHeaders().get("Authorization");
            if (auths != null && !auths.isEmpty() && auths.get(0).startsWith("Bearer ")) {
                token = auths.get(0).substring(7);
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            String uid = extractAccountIdAsString(token);
            if (uid != null) {
                attributes.put("principalId", uid);
                System.out.println("[WS] HANDSHAKE OK, userId=" + uid);
            } else {
                System.out.println("[WS] HANDSHAKE FAIL: Cannot extract accountId from token");
            }
        } else {
            System.out.println("[WS] HANDSHAKE FAIL: invalid or missing token");
        }
        return attributes.containsKey("principalId");
    }

    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    public String extractAccountIdAsString(String token) {
        try {
            String uid = jwtUtil.extractClaim(token, c -> {
                Object v = c.get("uid");
                return v != null ? String.valueOf(v) : null;
            });
            if (uid != null && uid.matches("\\d+")) return uid;

            String jti = jwtUtil.extractClaim(token, Claims::getId);
            if (jti != null && jti.matches("\\d+")) return jti;

            return null;
        } catch (Exception e) {
            return null;
        }
    }

}

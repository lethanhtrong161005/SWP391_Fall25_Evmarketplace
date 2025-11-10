package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Account;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.entities.Profile;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.enums.MediaType;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.CustomBusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.key}")
    private String jwtKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long jwtRefreshExpiration;

    private SecretKey getSignInKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKey));
    }

    public String generateToken(Account account, Profile profile) {
        return Jwts.builder()
                .id(account.getId().toString())
                .subject(getIdentifier(account))
                .claim("uid", account.getId())
                .claim("fullName", profile != null ? profile.getFullName() : null)
                .claim("avatar", profile != null ? MedialUtils.converMediaNametoMedialUrl(profile.getAvatarUrl(), MediaType.IMAGE.name()) : null)
                .claim("phoneVerified", account.isPhoneVerified())
                .claim("emailVerified", account.isEmailVerified())
                .claim("role", account.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }
    private String getIdentifier(Account account) {
        String identifier = "";

        if (account.getGoogleId() != null) {
            identifier = "google_" + account.getGoogleId();
        } else if (account.getPhoneNumber() != null) {
            identifier = "phone_" + account.getPhoneNumber();
        } else {
            throw new CustomBusinessException("Account must have either GoogleId or PhoneNumber");
        }
        return identifier;
    }

    public String generateRefreshToken(Account account) {
        return Jwts.builder()
                .id(account.getId().toString())
                .subject(getIdentifier(account))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            getAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return getAllClaims(token);
    }

}

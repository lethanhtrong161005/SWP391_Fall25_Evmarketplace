package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security;

import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.exception.JwtAuthenticationException;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.security.handlers.CustomSecurityHandlers;
import com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil  jwtUtil;
    @Autowired
    private CustomSecurityHandlers customSecurityHandlers;

    @Autowired
    private AppUserDetailsService userDetailsService;

    private final AccountStatusUserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1) Bỏ qua preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Nếu đã có Authentication (ví dụ từ filter khác) thì không xử lý lại
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try{
            Claims claims = jwtUtil.getClaimsFromToken(token);


            if(jwtUtil.isTokenExpired(token)){
                throw new ExpiredJwtException(null, claims, "JWT token expired");
            }

            String identifer = claims.getSubject();
            if(identifer == null){
                throw new JwtAuthenticationException("Token invalid: missing identifier");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(identifer);

            userDetailsChecker.check(userDetails);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e) {
            customSecurityHandlers.commence(request, response, new JwtAuthenticationException("JWT token is expired"));
        }catch (MalformedJwtException e) {
            customSecurityHandlers.commence(request, response,
                    new JwtAuthenticationException("Malformed JWT token"));
        } catch (SignatureException e) {
            customSecurityHandlers.commence(request, response,
                    new JwtAuthenticationException("Invalid JWT signature"));
        } catch (Exception e) {
            customSecurityHandlers.commence(request, response,
                    new JwtAuthenticationException("Invalid JWT"));
        }
    }
}

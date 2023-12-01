package com.example.cloudservicebackend.config.security.web.filter;

import com.example.cloudservicebackend.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("auth-token");
        final String jwt;
        final String login;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        login = extractLogin(jwt);

        if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    login,
                    null,
                    extractAuthorities(jwt));
            token.setDetails(createAuthenticationDetails(jwt));
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        filterChain.doFilter(request, response);
    }


    private Map<String, Object> createAuthenticationDetails(String jwt) {
        Map<String, Object> details = new HashMap<>(1);
        details.put("id", jwtTokenUtils.extractId(jwt));
        return details;
    }

    private List<SimpleGrantedAuthority> extractAuthorities(String jwt) {
        return jwtTokenUtils.extractRoles(jwt).stream()
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toList());
    }

    private String extractLogin(String jwt) {
        try {
            return jwtTokenUtils.extractLogin(jwt);
        } catch (ExpiredJwtException e) {
            log.info("РўРѕРєРµРЅ РёСЃС‚РµРє: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.info("РћС€РёР±РєР° РїСЂРѕРІРµСЂРєРё РїРѕРґРїРёСЃРё С‚РѕРєРµРЅР°: {}", e.getMessage());
            throw e;
        }
    }
}
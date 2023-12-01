package com.example.cloudservicebackend.utils;

import com.example.cloudservicebackend.exception.DetailIdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProcessor {

    private final JwtTokenUtils jwtTokenUtils;

    public long extractIdFromRequestToken(String requestToken) {
        try {
            Object id = jwtTokenUtils.extractId(requestToken.substring(7));
            return (long) id;
        } catch (Exception e) {
            final String message = "Error getting id from token";
            log.error(message);
            throw new DetailIdNotFoundException(message, e);
        }
    }
}

package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudAuthService {
    private final JwtTokenUtils jwtTokenUtils;
    private final CloudUserService cloudUserService;
    private final AuthenticationManager authenticationManager;

    public String createAuthToken(String login, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);
        final Authentication authentication = authenticationManager.authenticate(token);
        final Long id = cloudUserService.getUserByLogin(login).getId();

        return jwtTokenUtils.generateToken(authentication, id);
    }
}
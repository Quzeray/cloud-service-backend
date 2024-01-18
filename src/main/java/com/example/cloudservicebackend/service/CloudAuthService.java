package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.exception.RegistrationException;
import com.example.cloudservicebackend.exception.UserExistsException;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;

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

    public void registration(String login, String password) {
        if (cloudUserService.isUserExists(login)) {
            throw new UserExistsException();
        }

        CloudUser newUser = cloudUserService.createUser(login, password);
        if (newUser == null) {
            throw new RegistrationException();
        }
    }
}
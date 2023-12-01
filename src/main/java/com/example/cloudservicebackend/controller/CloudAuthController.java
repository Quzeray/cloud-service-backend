package com.example.cloudservicebackend.controller;

import com.example.cloudservicebackend.model.request.LoginRequest;
import com.example.cloudservicebackend.model.response.LoginResponse;
import com.example.cloudservicebackend.service.CloudAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CloudAuthController {
    private final CloudAuthService cloudAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody LoginRequest loginRequest) {
        final String login = loginRequest.getLogin();
        final String password = loginRequest.getPassword();
        final String authToken = cloudAuthService.createAuthToken(login, password);
        return ResponseEntity.ok(new LoginResponse(authToken));
    }
}
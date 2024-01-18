package com.example.cloudservicebackend.controller;

import com.example.cloudservicebackend.model.request.LoginRequest;
import com.example.cloudservicebackend.model.request.RegistrationRequest;
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
        final String authToken = cloudAuthService.createAuthToken(loginRequest.getLogin(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(authToken));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        cloudAuthService.registration(registrationRequest.getLogin(), registrationRequest.getPassword());
        final String authToken = cloudAuthService.createAuthToken(
                registrationRequest.getLogin(), registrationRequest.getPassword()
        );
        return ResponseEntity.ok(new LoginResponse(authToken));
    }
}
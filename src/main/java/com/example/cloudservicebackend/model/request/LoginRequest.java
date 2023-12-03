package com.example.cloudservicebackend.model.request;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
public class LoginRequest {
    private final String login;
    private final String password;
}
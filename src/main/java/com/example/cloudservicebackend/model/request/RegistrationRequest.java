package com.example.cloudservicebackend.model.request;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RegistrationRequest {
    private final String login;
    private final String password;
}
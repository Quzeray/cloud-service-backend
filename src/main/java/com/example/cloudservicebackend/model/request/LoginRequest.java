package com.example.cloudservicebackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private final String login;
    private final String password;
}
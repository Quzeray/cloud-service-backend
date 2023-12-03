package com.example.cloudservicebackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class LoginErrorResponse {
    public static final String BAD_CREDENTIALS = "Bad credentials";
    private final String[] email;
    private final String[] password;
    private final String message;
    private final int id;
}
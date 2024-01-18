package com.example.cloudservicebackend.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RegistrationErrorResponse {
    public static final String BAD_CREDENTIALS = "Bad credentials";
    private final String message;
    private final int id;
}

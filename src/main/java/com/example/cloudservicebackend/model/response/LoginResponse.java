package com.example.cloudservicebackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class LoginResponse {
    @JsonProperty("auth-token")
    private final String authToken;
}
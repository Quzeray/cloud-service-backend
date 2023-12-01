package com.example.cloudservicebackend.controller;

import com.example.cloudservicebackend.model.request.LoginRequest;
import com.example.cloudservicebackend.model.response.LoginResponse;
import com.example.cloudservicebackend.service.CloudAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CloudAuthControllerTest {


    @Mock
    private CloudAuthService cloudAuthService;

    @InjectMocks
    private CloudAuthController cloudAuthController;

    @Test
    @DisplayName("Test create authentication. Successful")
    void testCreateAuthToken() {
        // Arrange
        final String user = "user";
        final String password = "password";
        final String expectedAuthToken = "generatedAuthToken";
        final LoginRequest loginRequest = new LoginRequest(user, password);
        when(cloudAuthService.createAuthToken(eq(user), eq(password)))
                .thenReturn(expectedAuthToken);

        // Action
        ResponseEntity<?> responseEntity = cloudAuthController.createAuthToken(loginRequest);

        // Assert
        verify(cloudAuthService, times(1)).createAuthToken(user, password);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof LoginResponse);
        final LoginResponse loginResponse = (LoginResponse) responseEntity.getBody();
        assertEquals(expectedAuthToken, loginResponse.getAuthToken());
    }

    @Test
    @DisplayName("Test create authentication. Throws BadCredentialsException")
    void testCreateAuthTokenWithInvalidCredentials() {
        // Arrange
        final String invalidUser = "invalidUser";
        final String invalidPassword = "invalidPassword";
        final LoginRequest loginRequest = new LoginRequest(invalidUser, invalidPassword);
        when(cloudAuthService.createAuthToken(eq(invalidUser), eq(invalidPassword)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Assert
        assertThrows(BadCredentialsException.class, () -> cloudAuthController.createAuthToken(loginRequest));
    }

}

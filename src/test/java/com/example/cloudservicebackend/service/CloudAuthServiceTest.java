package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.utils.JwtTokenUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CloudAuthServiceTest {
    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private CloudUserService cloudUserService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private CloudAuthService cloudAuthService;

    @Test
    @DisplayName("Test create jwt token by login and password")
    void testCreateAuthToken() {
        // Arrange
        final String login = "user";
        final String password = "password";
        final Long userId = 1L;
        final String expectedToken = "generatedAuthToken";
        final Authentication mockAuthentication = Mockito.mock(Authentication.class);
        final CloudUser mockCloudUser = mock(CloudUser.class);
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);

        when(cloudUserService.getUserByLogin(eq(login))).thenReturn(mockCloudUser);
        when(mockCloudUser.getId()).thenReturn(userId);
        when(authenticationManager.authenticate(eq(token))).thenReturn(mockAuthentication);
        when(jwtTokenUtils.generateToken(eq(mockAuthentication), eq(userId))).thenReturn(expectedToken);

        // Action
        final String authToken = cloudAuthService.createAuthToken(login, password);

        // Assert
        verify(cloudUserService, times(1)).getUserByLogin(login);
        verify(authenticationManager, times(1)).authenticate(token);
        verify(jwtTokenUtils, times(1)).generateToken(mockAuthentication, userId);

        assertEquals(expectedToken, authToken);
    }

    @Test
    @DisplayName("Test creating jwt token with authentication failure due to invalid credentials")
    void testCreateAuthTokenWithAuthenticationFailure() {
        // Arrange
        final String login = "invalidUser";
        final String password = "invalidPassword";
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);

        when(authenticationManager.authenticate(eq(token)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        //Assert
        assertThrows(BadCredentialsException.class, () -> cloudAuthService.createAuthToken(login, password));
    }
}

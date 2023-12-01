package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CloudUserServiceTest {
    @Mock
    private CloudUserRepository cloudUserRepository;

    @InjectMocks
    private CloudUserService cloudUserService;

    @Test
    @DisplayName("Test loading user by username")
    void testLoadUserByUsername() {
        // Arrange
        final String login = "user";
        final String password = "password";
        final String role = "ROLE_USER";
        final int roleId = 1;
        final CloudUser mockUser = mock(CloudUser.class);
        when(mockUser.getLogin()).thenReturn(login);
        when(mockUser.getPassword()).thenReturn(password);
        when(mockUser.getRoles()).thenReturn(singletonList(new CloudRole(roleId, role)));
        when(cloudUserRepository.findByLogin(eq(login))).thenReturn(Optional.of(mockUser));

        // Action
        final UserDetails userDetails = cloudUserService.loadUserByUsername(login);

        // Assert
        assertNotNull(userDetails);
        assertEquals(login, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        final Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals(role, authorities.iterator().next().getAuthority());
    }

    @Test
    @DisplayName("Test loading invalid user")
    void testLoadUserByUsernameWithInvalidUser() {
        // Arrange
        final String login = "invalidUser";
        when(cloudUserRepository.findByLogin(eq(login))).thenReturn(Optional.empty());

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudUserService.loadUserByUsername(login));
    }

    @Test
    @DisplayName("Test getting user by login")
    void testGetUserByLogin() {
        // Arrange
        final String login = "user";
        final CloudUser mockUser = mock(CloudUser.class);
        when(mockUser.getLogin()).thenReturn(login);
        when(cloudUserRepository.findByLogin(eq(login))).thenReturn(Optional.of(mockUser));

        // Action
        final CloudUser foundUser = cloudUserService.getUserByLogin(login);

        // Assert
        assertEquals(login, foundUser.getLogin());
    }

    @Test
    @DisplayName("Test getting invalid user by login")
    void testGetUserLoginIdWithInvalidLogin() {
        // Arrange
        final String login = "user";
        when(cloudUserRepository.findByLogin(eq(login))).thenReturn(Optional.empty());

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudUserService.getUserByLogin(login));
    }

    @Test
    @DisplayName("Test getting user by id")
    void testGetUserById() {
        // Arrange
        final String login = "user";
        final long userId = 1L;
        final CloudUser mockUser = mock(CloudUser.class);
        when(mockUser.getLogin()).thenReturn(login);
        when(cloudUserRepository.findById(eq(userId))).thenReturn(Optional.of(mockUser));

        // Action
        final CloudUser foundUser = cloudUserService.getUserById(userId);

        // Assert
        assertEquals(login, foundUser.getLogin());
    }

    @Test
    @DisplayName("Test getting invalid user by id")
    void testGetUserByIdWithInvalidId() {
        // Arrange
        final long userId = 3L;
        when(cloudUserRepository.findById(eq(userId))).thenReturn(Optional.empty());

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudUserService.getUserById(userId));
    }

}

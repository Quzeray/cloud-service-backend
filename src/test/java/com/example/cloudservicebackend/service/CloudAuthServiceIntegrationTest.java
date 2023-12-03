package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.config.ContainerConfig;
import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import com.example.cloudservicebackend.utils.TestDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(classes = ContainerConfig.class)
public class CloudAuthServiceIntegrationTest {
    private static final String TEST_LOGIN = "user";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ROLE = "test";

    @Autowired
    private CloudUserRepository cloudUserRepository;
    @Autowired
    private CloudRoleRepository cloudRoleRepository;
    @Autowired
    private CloudAuthService cloudAuthService;
    @Autowired
    private TestDataUtils testDataUtils;

    @BeforeEach
    @Transactional
    void setUpTestUser() {
        clearDatabase();
        final List<CloudRole> roleList = List.of(CloudRole.builder().name(TEST_ROLE).build());
        testDataUtils.createCloudUserWithRoles(TEST_LOGIN, TEST_PASSWORD, roleList);
    }

    @Transactional
    void clearDatabase() {
        cloudRoleRepository.deleteAll();
        cloudUserRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Test create authentication token. Successful")
    void testCreateAuthToken() {
        // Action
        final String authToken = cloudAuthService.createAuthToken(TEST_LOGIN, TEST_PASSWORD);

        // Assert
        assertNotNull(authToken);
    }

    @Test
    @Transactional
    @DisplayName("Test create authentication token. Throws BadCredentialsException")
    void testCreateAuthTokenBadCred() {
        // Arrange
        final String badPassword = "badPassword";

        // Assert
        assertThrows(BadCredentialsException.class, () -> cloudAuthService.createAuthToken(TEST_LOGIN, badPassword));
    }
}

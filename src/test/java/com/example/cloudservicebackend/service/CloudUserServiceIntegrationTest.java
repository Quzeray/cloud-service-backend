package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.config.ContainerConfig;
import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import com.example.cloudservicebackend.utils.TestDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = ContainerConfig.class)
public class CloudUserServiceIntegrationTest {
    private static final String TEST_LOGIN = "user";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ROLE = "test";
    private long testId = 1L;

    @Autowired
    private CloudUserRepository cloudUserRepository;
    @Autowired
    private CloudRoleRepository cloudRoleRepository;
    @Autowired
    private CloudUserService cloudUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TestDataUtils testDataUtils;

    @BeforeEach
    @Transactional
    void setUpTestUser() {
        clearDatabase();
        final List<CloudRole> roleList = List.of(CloudRole.builder().name(TEST_ROLE).build());
        testDataUtils.createCloudUserWithRoles(TEST_LOGIN, TEST_PASSWORD, roleList);
        // Id Р°РІС‚РѕРёРЅРєСЂРµРјРµРЅС‚РёСЂСѓРµС‚СЃСЏ
        testId = cloudUserRepository.findByLogin(TEST_LOGIN).orElseThrow().getId();
    }

    @Transactional
    void clearDatabase() {
        cloudRoleRepository.deleteAll();
        cloudUserRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Test get user by ID. Successful")
    void testGetUserById() {
        // Action
        final CloudUser retrievedUser = cloudUserService.getUserById(testId);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals(testId, retrievedUser.getId());
    }

    @Test
    @Transactional
    @DisplayName("Test get user by non-existent ID. Throws UserNotFoundException")
    void testGetUserByNonExistentId() {
        // Arrange
        final long nonExistentId = 99L;

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudUserService.getUserById(nonExistentId));
    }

    @Test
    @Transactional
    @DisplayName("Test get user by login. Successful")
    void testGetUserByLogin() {
        // Action
        final CloudUser retrievedUser = cloudUserService.getUserByLogin(TEST_LOGIN);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals(TEST_LOGIN, retrievedUser.getLogin());
    }

    @Test
    @Transactional
    @DisplayName("Test get user by non-existent login. Throws UsernameNotFoundException")
    void testGetUserByNonExistentLogin() {
        // Arrange
        final String nonExistentLogin = "admin";

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudUserService.getUserByLogin(nonExistentLogin));
    }

    @Test
    @Transactional
    @DisplayName("Test load user by login. Successful")
    void testLoadUserByLogin() {
        // Action
        final UserDetails userDetails = cloudUserService.loadUserByUsername(TEST_LOGIN);

        // Assert
        assertNotNull(userDetails);
        assertEquals(TEST_LOGIN, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, userDetails.getPassword()));
        assertNotNull(userDetails.getAuthorities());
        assertEquals(TEST_ROLE, userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @Transactional
    @DisplayName("Test load non-existent user by login. Throws UsernameNotFoundException")
    void testLoadNonExistentUserByLogin() {
        // Arrange
        final String nonExistentLogin = "admin";

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudUserService.loadUserByUsername(nonExistentLogin));
    }
}

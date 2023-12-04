package com.example.cloudservicebackend;

import com.example.cloudservicebackend.model.request.LoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.example.cloudservicebackend.utils.IntegrationUtils.jsonNode;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationAuthControllerIntegrationTest extends ContainersBaseTest {
    private static String URL;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setUrl() {
        URL = "http://localhost:" + getServiceContainer().getFirstMappedPort();
    }

    @Test
    @DisplayName("Test login request. Successful")
    void testLoginRequest() {
        final String login = "user";
        final String password = "password";

        final LoginRequest loginRequest = new LoginRequest(login, password);
        final ResponseEntity<String> response = restTemplate.postForEntity(URL + "/login", loginRequest, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(jsonNode(response.getBody()).has("auth-token"));
    }

    @Test
    @DisplayName("Test login request. Unauthorized")
    void testLoginRequestWithBadCredentials() {
        final String invalidLogin = "invalidUser";
        final String invalidPassword = "invalidPassword";

        final LoginRequest loginRequest = new LoginRequest(invalidLogin, invalidPassword);
        final ResponseEntity<Void> response = restTemplate.postForEntity(URL + "/login", loginRequest, Void.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}

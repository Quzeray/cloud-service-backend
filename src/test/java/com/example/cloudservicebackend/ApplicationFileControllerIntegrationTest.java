package com.example.cloudservicebackend;

import com.example.cloudservicebackend.model.request.FileNameRequest;
import com.example.cloudservicebackend.model.request.LoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.example.cloudservicebackend.utils.IntegrationUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationFileControllerIntegrationTest extends ContainersBaseTest {
    private static String URL;
    private static String AUTH_TOKEN;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void setUrl() {
        URL = "http://localhost:" + getServiceContainer().getFirstMappedPort();
    }

    @BeforeEach
    void createAuthToken() {
        final ResponseEntity<String> response = restTemplate
                .postForEntity(URL + "/login", new LoginRequest("user", "password"), String.class);
        AUTH_TOKEN = "Bearer " + jsonNode(response.getBody()).get("auth-token").asText();
    }

    @Test
    @DisplayName("Test request get file list. Successful")
    void testGetFileList() {
        final HttpHeaders headers = createHeaders(AUTH_TOKEN);

        final ResponseEntity<String> response = restTemplate
                .exchange(URL + "/list?limit=10", GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    @DisplayName("Test request upload file. Successful")
    void testUploadFile() {
        final String filename = generateUniqueFileName();
        final HttpHeaders headers = createHeaders(AUTH_TOKEN);
        final var requestEntity = createRequestEntityFile(filename, headers);

        final ResponseEntity<String> response = restTemplate
                .exchange(URL + "/file?filename=" + filename, POST, requestEntity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success upload", response.getBody());
    }

    @Test
    @DisplayName("Test request delete file. Successful")
    void testDeleteFile() {
        final String filename = generateUniqueFileName();
        final HttpHeaders headersUpload = createHeaders(AUTH_TOKEN);
        final var requestUploadEntity = createRequestEntityFile(filename, headersUpload);
        final ResponseEntity<String> responseUpload = restTemplate
                .exchange(URL + "/file?filename=" + filename, POST, requestUploadEntity, String.class);

        final HttpHeaders headersDelete = createHeaders(AUTH_TOKEN);
        final HttpEntity<Object> requestDeleteEntity = new HttpEntity<>(headersDelete);
        final ResponseEntity<String> responseDelete = restTemplate
                .exchange(URL + "/file?filename=" + filename, DELETE, requestDeleteEntity, String.class);

        assertEquals(HttpStatus.OK, responseDelete.getStatusCode());
        assertEquals("Success deleted", responseDelete.getBody());
    }

    @Test
    @DisplayName("Test request download file. Successful")
    void testDownloadFile() {
        final String filename = generateUniqueFileName();
        final HttpHeaders headersUpload = createHeaders(AUTH_TOKEN);
        final var requestUploadEntity = createRequestEntityFile(filename, headersUpload);
        final ResponseEntity<String> responseUpload = restTemplate
                .exchange(URL + "/file?filename=" + filename, POST, requestUploadEntity, String.class);


        final HttpHeaders headersDownload = createHeaders(AUTH_TOKEN);
        final HttpEntity<Object> requestDownloadEntity = new HttpEntity<>(headersDownload);
        final ResponseEntity<String> responseDownload = restTemplate
                .exchange(URL + "/file?filename=" + filename, GET, requestDownloadEntity, String.class);

        assertEquals(HttpStatus.OK, responseDownload.getStatusCode());
        assertNotNull(responseDownload.getBody());
        assertEquals(filename, jsonNode(responseDownload.getBody()).get("filename").asText());
    }

    @Test
    @DisplayName("Test request rename file. Successful")
    void testEditFileName() {
        final String oldFileName = generateUniqueFileName();
        final String newFileName = generateUniqueFileName();
        final HttpHeaders headersUpload = createHeaders(AUTH_TOKEN);
        final var requestUploadEntity = createRequestEntityFile(oldFileName, headersUpload);
        final ResponseEntity<String> responseUpload = restTemplate
                .exchange(URL + "/file?filename=" + oldFileName, POST, requestUploadEntity, String.class);


        final HttpHeaders headersRename = createHeaders(AUTH_TOKEN);
        HttpEntity<FileNameRequest> requestRenameEntity = new HttpEntity<>(new FileNameRequest(newFileName), headersRename);
        ResponseEntity<String> responseRename = restTemplate
                .exchange(URL + "/file?filename=" + oldFileName, PUT, requestRenameEntity, String.class);

        assertEquals(HttpStatus.OK, responseRename.getStatusCode());
        assertNotNull(responseRename.getBody());
        assertEquals("Success rename", responseRename.getBody());
    }
}

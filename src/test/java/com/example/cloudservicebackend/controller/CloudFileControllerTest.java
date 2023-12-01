package com.example.cloudservicebackend.controller;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.exception.BadLimitException;
import com.example.cloudservicebackend.exception.DetailIdNotFoundException;
import com.example.cloudservicebackend.model.request.FileNameRequest;
import com.example.cloudservicebackend.model.response.FileResponse;
import com.example.cloudservicebackend.service.CloudFileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CloudFileControllerTest {

    @Mock
    private CloudFileService cloudFileService;

    @InjectMocks
    private CloudFileController cloudFileController;


    private CloudFile createMockCloudFile() {
        final CloudFile cloudFileMock = Mockito.mock(CloudFile.class);
        when(cloudFileMock.getFileName()).thenReturn("filename");
        when(cloudFileMock.getData()).thenReturn(new byte[]{0x01, 0x02, 0x03});
        return cloudFileMock;
    }

    @Test
    @DisplayName("Test Get file list. Successful Response")
    void testGetFileList() {
        // Arrange
        final String validAuthToken = "validAuthToken";
        final int limit = 4;
        final List<CloudFile> mockFiles = Arrays.asList(
                createMockCloudFile(),
                createMockCloudFile(),
                createMockCloudFile()
        );
        when(cloudFileService.getFileList(eq(limit), eq(validAuthToken))).thenReturn(mockFiles);

        // Action
        final ResponseEntity<?> responseEntity = cloudFileController.getFileList(validAuthToken, limit);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List<?>);
        final List<?> responseList = (List<?>) responseEntity.getBody();
        assertTrue(responseList.stream().allMatch(element -> element instanceof FileResponse));
        assertEquals(mockFiles.size(), responseList.size());
    }

    @Test
    @DisplayName("Test Get file list with bad Limit. Throws BadLimitException")
    void testGetFileListWithBadLimit() {
        // Arrange
        final String validAuthToken = "validAuthToken";
        final int badLimit = 0;
        when(cloudFileService.getFileList(eq(badLimit), eq(validAuthToken))).thenThrow(new BadLimitException());

        // Assert
        assertThrows(BadLimitException.class, () -> cloudFileController.getFileList(validAuthToken, badLimit));
    }

    @Test
    @DisplayName("Test Upload file. Successful Response")
    void testUploadFile() {
        // Arrange
        final String validAuthToken = "validAuthToken";
        final String fileName = "example.txt";
        final MultipartFile file = mock(MultipartFile.class);

        // Act
        final ResponseEntity<?> responseEntity = cloudFileController.uploadFile(validAuthToken, fileName, file);

        // Assert
        verify(cloudFileService, times(1)).uploadFile(fileName, file, validAuthToken);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success upload", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test Delete file. Successful Response")
    void testDeleteFile() {
        // Arrange
        final String validAuthToken = "validAuthToken";
        final String fileName = "example.txt";

        // Act
        final ResponseEntity<?> responseEntity = cloudFileController.deleteFile(validAuthToken, fileName);

        // Assert
        verify(cloudFileService, times(1)).deleteFile(fileName, validAuthToken);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success deleted", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test Download file. Successful Response")
    void testDownloadFile() {
        // Arrange
        final String validAuthToken = "validAuthToken";
        final String fileName = "example.txt";
        final CloudFile mockCloudFile = createMockCloudFile();
        when(cloudFileService.downloadFile(eq(fileName), eq(validAuthToken))).thenReturn(mockCloudFile);

        // Act
        final ResponseEntity<?> responseEntity = cloudFileController.downloadFile(validAuthToken, fileName);

        // Assert
        verify(cloudFileService, times(1)).downloadFile(fileName, validAuthToken);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof FileResponse);
    }

    @Test
    @DisplayName("Test Edit file name. Successful Response")
    void testEditFileName() {
        // Arrange
        final String validAuthToken = "validAuthToken";
        final String newFileName = "newExample.txt";
        final String fileName = "example.txt";
        final FileNameRequest fileNameRequest = new FileNameRequest(newFileName);

        // Act
        final ResponseEntity<?> responseEntity = cloudFileController
                .editFileName(validAuthToken, fileName, fileNameRequest);

        // Assert
        verify(cloudFileService, times(1))
                .editFileName(fileName, newFileName, validAuthToken);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Success upload", responseEntity.getBody());
    }

    @Test
    @DisplayName("Test Requests with token without ID. Throws DetailIdNotFoundException")
    void testRequestsWithAuthTokenWithoutId() {
        // Arrange
        final String authTokenWithoutId = "authTokenWithoutId";
        final int limit = 4;
        when(cloudFileService.getFileList(eq(limit), eq(authTokenWithoutId)))
                .thenThrow(new DetailIdNotFoundException());

        // Assert
        assertThrows(DetailIdNotFoundException.class, () ->
                cloudFileController.getFileList(authTokenWithoutId, limit));
    }
}

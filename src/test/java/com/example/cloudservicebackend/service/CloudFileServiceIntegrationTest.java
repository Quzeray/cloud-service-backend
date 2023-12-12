package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.config.ContainerConfig;
import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.entity.CloudRole;
import com.example.cloudservicebackend.exception.*;
import com.example.cloudservicebackend.repository.CloudFileRepository;
import com.example.cloudservicebackend.repository.CloudRoleRepository;
import com.example.cloudservicebackend.repository.CloudUserRepository;
import com.example.cloudservicebackend.utils.TestDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = ContainerConfig.class)
public class CloudFileServiceIntegrationTest {
    private static final String TEST_LOGIN = "user";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ROLE = "test";
    private long testId = 1L;
    @Autowired
    private CloudUserRepository cloudUserRepository;
    @Autowired
    private CloudRoleRepository cloudRoleRepository;
    @Autowired
    private CloudFileRepository cloudFileRepository;
    @Autowired
    private CloudFileService cloudFileService;
    @Autowired
    private TestDataUtils testDataUtils;

    @BeforeEach
    @Transactional
    void setUpTestUser() {
        clearDatabase();
        final List<CloudRole> roleList = List.of(CloudRole.builder().name(TEST_ROLE).build());
        testDataUtils.createCloudUserWithRoles(TEST_LOGIN, TEST_PASSWORD, roleList);
        // Id is auto-incremented
        testId = cloudUserRepository.findByLogin(TEST_LOGIN).orElseThrow().getId();
    }

    @Transactional
    void clearDatabase() {
        cloudRoleRepository.deleteAll();
        cloudFileRepository.deleteAll();
        cloudUserRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("Test get file list. Successful")
    void
    testGetFileList() {
        // Arrange
        final int limit = 10;
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Action
        final List<CloudFile> fileList = cloudFileService.getFileList(limit, testBearerToken);

        // Assert
        assertEquals(0, fileList.size());
    }

    @Test
    @Transactional
    @DisplayName("Test get file list with invalid limit. Throws BadLimitException")
    void testGetFileListWithInvalidLimit() {
        // Arrange
        final int invalidLimit = -1;
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Assert
        assertThrows(BadLimitException.class, () -> cloudFileService.getFileList(invalidLimit, testBearerToken));
    }

    @Test
    @Transactional
    @DisplayName("Test upload file. Successful")
    void testUploadFile() {
        // Arrange
        final String fileName = "testFile.txt";
        final byte[] fileContent = "Test file content".getBytes();
        final MockMultipartFile file = new MockMultipartFile(fileName, fileContent);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Action
        cloudFileService.uploadFile(fileName, file, testBearerToken);

        // Assert
        assertTrue(cloudFileRepository.existsByFileNameAndUserId(fileName, testId));
    }

    @Test
    @Transactional
    @DisplayName("Test upload file with empty file. Throws CreateFileException")
    void testUploadFileWithEmptyFile() {
        // Arrange
        final String fileName = "emptyFile.txt";
        final MockMultipartFile emptyFile = new MockMultipartFile(fileName, new byte[0]);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Assert
        assertThrows(CreateFileException.class, () ->
                cloudFileService.uploadFile(null, emptyFile, testBearerToken));
    }

    @Test
    @Transactional
    @DisplayName("Test delete file. Successful")
    void testDeleteFile() {
        // Arrange
        final String fileName = "testFile.txt";
        testDataUtils.createFileForUser(TEST_LOGIN, fileName);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Action
        cloudFileService.deleteFile(fileName, testBearerToken);

        // Assert
        assertFalse(cloudFileRepository.existsByFileNameAndUserId(fileName, testId));
    }

    @Test
    @Transactional
    @DisplayName("Test delete non-existing file. Throws FileNotExistsException")
    void testDeleteNonExistingFile() {
        // Arrange
        final String existingFileName = "existingFile.txt";
        final String nonExistingFileName = "nonExistingFile.txt";
        testDataUtils.createFileForUser(TEST_LOGIN, existingFileName);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Assert
        assertTrue(cloudFileRepository.existsByFileNameAndUserId(existingFileName, testId));
        assertFalse(cloudFileRepository.existsByFileNameAndUserId(nonExistingFileName, testId));
        assertThrows(FileNotExistsException.class, () ->
                cloudFileService.deleteFile(nonExistingFileName, testBearerToken));
    }

    @Test
    @Transactional
    @DisplayName("Test download file. Successful")
    void testDownloadFile() {
        // Arrange
        final String fileName = "testFile.txt";
        testDataUtils.createFileForUser(TEST_LOGIN, fileName);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Action
        CloudFile cloudFile = cloudFileService.downloadFile(fileName, testBearerToken);

        // Assert
        assertNotNull(cloudFile);
        assertEquals(fileName, cloudFile.getFileName());
        assertEquals(TEST_LOGIN, cloudFile.getUser().getLogin());
    }

    @Test
    @Transactional
    @DisplayName("Test download non-existing file. Throws FileNotExistsException")
    void testDownloadNonExistingFile() {
        // Arrange
        final String existingFileName = "existingFile.txt";
        final String nonExistingFileName = "nonExistingFile.txt";
        testDataUtils.createFileForUser(TEST_LOGIN, existingFileName);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Assert
        assertTrue(cloudFileRepository.existsByFileNameAndUserId(existingFileName, testId));
        assertFalse(cloudFileRepository.existsByFileNameAndUserId(nonExistingFileName, testId));
        assertThrows(FileNotExistsException.class, () ->
                cloudFileService.downloadFile(nonExistingFileName, testBearerToken));
    }

    @Test
    @Transactional
    @DisplayName("Test edit file name. Successful")
    void testEditFileName() {
        // Arrange
        final String fileName = "testFile.txt";
        final String newFileName = "newTestFile.txt";
        testDataUtils.createFileForUser(TEST_LOGIN, fileName);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Action
        cloudFileService.editFileName(fileName, newFileName, testBearerToken);

        // Assert
        CloudFile cloudFile = cloudFileRepository.findByFileNameAndUserId(newFileName, testId);
        assertNotNull(cloudFile);
        assertEquals(newFileName, cloudFile.getFileName());
    }

    @Test
    @Transactional
    @DisplayName("Test edit non-existing file name. Throws FileNotExistsException")
    void testEditNonExistingFileName() {
        // Arrange
        final String newFileName = "newTestFile.txt";
        final String existingFileName = "existingFile.txt";
        final String nonExistingFileName = "nonExistingFile.txt";
        testDataUtils.createFileForUser(TEST_LOGIN, existingFileName);
        final String testBearerToken = testDataUtils.createTestBearerToken(TEST_LOGIN, TEST_PASSWORD, testId);

        // Assert
        assertTrue(cloudFileRepository.existsByFileNameAndUserId(existingFileName, testId));
        assertFalse(cloudFileRepository.existsByFileNameAndUserId(nonExistingFileName, testId));
        assertThrows(FileNotExistsException.class, () ->
                cloudFileService.editFileName(nonExistingFileName, newFileName, testBearerToken));
    }

}

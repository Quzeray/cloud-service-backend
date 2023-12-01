package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.entity.CloudFileFactory;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.exception.*;
import com.example.cloudservicebackend.repository.CloudFileRepository;
import com.example.cloudservicebackend.utils.CloudFileChecker;
import com.example.cloudservicebackend.utils.JwtTokenProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CloudFIleServiceTest {
    @Mock
    private JwtTokenProcessor jwtTokenProcessor;

    @Mock
    private CloudFileFactory cloudFileFactory;
    @Mock
    private CloudFileChecker cloudFileChecker;

    @Mock
    private CloudFileRepository cloudFileRepository;

    @Mock
    private CloudUserService cloudUserService;

    @InjectMocks
    private CloudFileService cloudFileService;


    @Test
    @DisplayName("Test each request with bad token details. Throws DetailIdNotFoundException")
    void testBadTokenDetailsWithDetailIdNotFoundException() {
        // Arrange
        final String fileName = "example.txt";
        final String newFileName = "newExample.txt";
        final String authToken = "tokenWithoutId";
        MultipartFile mockFile = mock(MultipartFile.class);

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenThrow(new DetailIdNotFoundException());

        // Assert
        assertThrows(DetailIdNotFoundException.class, () -> cloudFileService.getFileList(5, authToken));
        assertThrows(DetailIdNotFoundException.class, () -> cloudFileService.deleteFile(fileName, authToken));
        assertThrows(DetailIdNotFoundException.class, () -> cloudFileService.downloadFile(fileName, authToken));
        assertThrows(DetailIdNotFoundException.class, () ->
                cloudFileService.uploadFile(fileName, mockFile, authToken));
        assertThrows(DetailIdNotFoundException.class, () ->
                cloudFileService.editFileName(fileName, newFileName, authToken));
    }

    @Test
    @DisplayName("Test File search request. Throws FileNotExistsException")
    void testFileSearchRequestWithFileNotExistsException() {
        // Arrange
        final String fileName = "example.txt";
        final String newFileName = "newExample.txt";
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        doThrow(new FileNotExistsException()).when(cloudFileChecker).checkExistsFile(eq(fileName), eq(userId));

        //Assert
        assertThrows(FileNotExistsException.class, () -> cloudFileService.deleteFile(fileName, authToken));
        assertThrows(FileNotExistsException.class, () -> cloudFileService.downloadFile(fileName, authToken));
        assertThrows(FileNotExistsException.class, () ->
                cloudFileService.editFileName(fileName, newFileName, authToken));
    }

    @Test
    @DisplayName("Test Get file list. Successful")
    void testGetFileList() {
        // Arrange
        final int limit = 5;
        final String authToken = "validToken";
        final long userId = 1L;
        final List<CloudFile> expectedFiles = List.of(mock(CloudFile.class));

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudFileRepository.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(expectedFiles);

        // Action
        List<CloudFile> result = cloudFileService.getFileList(limit, authToken);

        // Assert
        assertEquals(expectedFiles, result);
    }

    @Test
    @DisplayName("Test Get file list with bad limit. Throws BadLimitException")
    void testGetFileListWithBadLimit() {
        // Arrange
        final int badLimit = 0;
        final String authToken = "validToken";

        // Assert
        assertThrows(BadLimitException.class, () -> cloudFileService.getFileList(badLimit, authToken));
    }

    @Test
    @DisplayName("Test Get file list. Throws SearchFileException")
    void testGetFileListWithSearchFileException() {
        // Arrange
        final int limit = 5;
        final String authToken = "validToken";
        final long userId = 5L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudFileRepository.findAllByUserId(eq(userId), any())).thenThrow(new RuntimeException());

        // Assert
        assertThrows(SearchFileException.class, () -> cloudFileService.getFileList(limit, authToken));
    }

    @Test
    @DisplayName("Test Upload File. Successful")
    void testUploadFile() {
        // Arrange
        final String fileName = "example.txt";
        final MultipartFile file = mock(MultipartFile.class);
        final String authToken = "validToken";
        final long userId = 1L;
        final CloudUser mockUser = mock(CloudUser.class);
        final CloudFile mockCloudFile = mock(CloudFile.class);

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudUserService.getUserById(eq(userId))).thenReturn(mockUser);
        when(cloudFileFactory.createCloudFile(eq(fileName), eq(mockUser), eq(file))).thenReturn(mockCloudFile);

        // Assert
        assertDoesNotThrow(() -> cloudFileService.uploadFile(fileName, file, authToken));
        verify(cloudFileRepository, times(1)).save(eq(mockCloudFile));
    }

    @Test
    @DisplayName("Test Upload file. Throws UsernameNotFoundException")
    void testUploadFileUserNotFound() {
        // Arrange
        final String fileName = "example.txt";
        final MultipartFile file = mock(MultipartFile.class);
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudUserService.getUserById(eq(userId))).thenThrow(new UsernameNotFoundException("User not found"));

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> cloudFileService.uploadFile(fileName, file, authToken));
    }

    @Test
    @DisplayName("Test Upload file. Throws CreateFileException")
    void testUploadFileCreateFileException() {
        // Arrange
        final String fileName = "example.txt";
        final MultipartFile file = mock(MultipartFile.class);
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudUserService.getUserById(eq(userId))).thenReturn(mock(CloudUser.class));
        when(cloudFileFactory.createCloudFile(any(), any(), any())).thenThrow(new CreateFileException());

        // Assert
        assertThrows(CreateFileException.class, () -> cloudFileService.uploadFile(fileName, file, authToken));
    }

    @Test
    @DisplayName("Test Upload file. Throws UploadFileException")
    void testUploadFileUploadFileException() {
        // Arrange
        final String fileName = "example.txt";
        final MultipartFile file = mock(MultipartFile.class);
        final String authToken = "validToken";
        final long userId = 1L;
        final CloudFile mockCloudFile = mock(CloudFile.class);

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudUserService.getUserById(eq(userId))).thenReturn(mock(CloudUser.class));
        when(cloudFileFactory.createCloudFile(any(), any(), any())).thenReturn(mockCloudFile);
        when(cloudFileRepository.save(eq(mockCloudFile))).thenThrow(new RuntimeException());

        // Assert
        assertThrows(UploadFileException.class, () -> cloudFileService.uploadFile(fileName, file, authToken));
    }

    @Test
    @DisplayName("Test Delete file. Successful")
    void testDeleteFile() {
        // Arrange
        final String fileName = "example.txt";
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);

        // Assert
        assertDoesNotThrow(() -> cloudFileService.deleteFile(fileName, authToken));
        verify(jwtTokenProcessor, times(1)).extractIdFromRequestToken(eq(authToken));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).deleteByFileNameAndUserId(eq(fileName), eq(userId));
    }

    @Test
    @DisplayName("Test Delete file. Throws DeleteFileException")
    void testDeleteFileWithDeleteFileException() {
        // Arrange
        final String fileName = "example.txt";
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        doThrow(new RuntimeException()).when(cloudFileRepository)
                .deleteByFileNameAndUserId(eq(fileName), eq(userId));

        // Assert
        assertThrows(DeleteFileException.class, () -> cloudFileService.deleteFile(fileName, authToken));
        verify(jwtTokenProcessor, times(1)).extractIdFromRequestToken(eq(authToken));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).deleteByFileNameAndUserId(eq(fileName), eq(userId));
    }

    @Test
    @DisplayName("Test Download file. Successful")
    void testDownloadFile() {
        // Arrange
        final String fileName = "example.txt";
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);

        // Assert
        assertDoesNotThrow(() -> cloudFileService.downloadFile(fileName, authToken));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).findByFileNameAndUserId(eq(fileName), eq(userId));
    }

    @Test
    @DisplayName("Test Download file. Throws DownloadFileException")
    void testDownloadFileWithDownloadFileException() {
        // Arrange
        final String fileName = "example.txt";
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        doThrow(new RuntimeException()).when(cloudFileRepository).findByFileNameAndUserId(eq(fileName), eq(userId));

        // Assert
        assertThrows(DownloadFileException.class, () -> cloudFileService.downloadFile(fileName, authToken));
        verify(jwtTokenProcessor, times(1)).extractIdFromRequestToken(eq(authToken));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).findByFileNameAndUserId(eq(fileName), eq(userId));
    }

    @Test
    @DisplayName("Test Edit file name. Successful")
    void testEditFileName() {
        // Arrange
        final String fileName = "example.txt";
        final String newFileName = "newExample.txt";
        final String authToken = "validToken";
        final long userId = 1L;
        final CloudFile mockCloudFile = mock(CloudFile.class);


        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        when(cloudFileRepository.findByFileNameAndUserId(eq(fileName), eq(userId))).thenReturn(mockCloudFile);

        // Assert
        assertDoesNotThrow(() -> cloudFileService.editFileName(fileName, newFileName, authToken));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).findByFileNameAndUserId(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).save(eq(mockCloudFile));
    }

    @Test
    @DisplayName("Test Download file. Throws RenameFileException")
    void testEditFileNameWithRenameFileException() {
        // Arrange
        final String fileName = "example.txt";
        final String newFileName = "newExample.txt";
        final String authToken = "validToken";
        final long userId = 1L;

        when(jwtTokenProcessor.extractIdFromRequestToken(eq(authToken))).thenReturn(userId);
        doThrow(new RuntimeException()).when(cloudFileRepository).findByFileNameAndUserId(eq(fileName), eq(userId));

        // Assert
        assertThrows(RenameFileException.class, () -> cloudFileService.editFileName(fileName, newFileName, authToken));
        verify(jwtTokenProcessor, times(1)).extractIdFromRequestToken(eq(authToken));
        verify(cloudFileChecker, times(1)).checkExistsFile(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(1)).findByFileNameAndUserId(eq(fileName), eq(userId));
        verify(cloudFileRepository, times(0)).save(any(CloudFile.class));
    }

}

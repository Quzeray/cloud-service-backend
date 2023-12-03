package com.example.cloudservicebackend.service;

import com.example.cloudservicebackend.entity.CloudFile;
import com.example.cloudservicebackend.entity.CloudFileFactory;
import com.example.cloudservicebackend.entity.CloudUser;
import com.example.cloudservicebackend.exception.*;
import com.example.cloudservicebackend.repository.CloudFileRepository;
import com.example.cloudservicebackend.utils.CloudFileChecker;
import com.example.cloudservicebackend.utils.JwtTokenProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.text.MessageFormat.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudFileService {
    private final CloudFileRepository cloudFileRepository;
    private final CloudFileFactory cloudFileFactory;
    private final CloudFileChecker cloudFileChecker;
    private final JwtTokenProcessor jwtTokenProcessor;
    private final CloudUserService cloudUserService;

    @Transactional
    public List<CloudFile> getFileList(int limit, String authToken) {
        final PageRequest pageRequest = getPageRequest(limit);
        final long userId = jwtTokenProcessor.extractIdFromRequestToken(authToken);
        try {
            final List<CloudFile> cloudFiles = cloudFileRepository.findAllByUserId(userId, pageRequest);
            log.info("Get files for user with id \"{}\" and limit \"{}\"", userId, limit);
            return cloudFiles;
        } catch (Exception e) {
            final String message = format("Error searching files for user with id \"{0}\"", userId);
            log.error(message);
            throw new SearchFileException(message, e);
        }
    }

    private PageRequest getPageRequest(int limit) {
        try {
            return PageRequest.of(0, limit);
        } catch (Exception e) {
            final String message = format("The limit \"{0}\" is set incorrectly", limit);
            log.error(message);
            throw new BadLimitException(message, e);
        }
    }

    @Transactional
    public void uploadFile(String fileName, MultipartFile file, String authToken) {
        final long userId = jwtTokenProcessor.extractIdFromRequestToken(authToken);
        final CloudUser user = cloudUserService.getUserById(userId);
        final CloudFile cloudFile = cloudFileFactory.createCloudFile(fileName, user, file);

        try {
            cloudFileRepository.save(cloudFile);
        } catch (Exception e) {
            final String message = format("Error upload file \"{0}\"", fileName);
            log.error(message);
            throw new UploadFileException(message, e);
        }
        log.info("File \"{}\" uploaded successfully", fileName);
    }

    @Transactional
    public void deleteFile(String fileName, String authToken) {
        final long userId = jwtTokenProcessor.extractIdFromRequestToken(authToken);
        cloudFileChecker.checkExistsFile(fileName, userId);

        try {
            cloudFileRepository.deleteByFileNameAndUserId(fileName, userId);
        } catch (Exception e) {
            final String message = format("Error deleting file \"{0}\"", fileName);
            log.error(message);
            throw new DeleteFileException(message, e);
        }
        log.info("File \"{}\" deletion successfully", fileName);
    }

    @Transactional
    public CloudFile downloadFile(String fileName, String authToken) {
        final long userId = jwtTokenProcessor.extractIdFromRequestToken(authToken);
        cloudFileChecker.checkExistsFile(fileName, userId);
        try {
            final CloudFile cloudFile = cloudFileRepository.findByFileNameAndUserId(fileName, userId);
            log.info("Get file \"{}\" for user with id \"{}\"", fileName, userId);
            return cloudFile;
        } catch (Exception e) {
            final String message = format("Error downloading file \"{0}\"", fileName);
            log.error(message);
            throw new DownloadFileException(message, e);
        }
    }

    @Transactional
    public void editFileName(String fileName, String newFileName, String authToken) {
        final long userId = jwtTokenProcessor.extractIdFromRequestToken(authToken);
        cloudFileChecker.checkExistsFile(fileName, userId);
        try {
            final CloudFile cloudFile = cloudFileRepository.findByFileNameAndUserId(fileName, userId);
            cloudFile.setFileName(newFileName);
            cloudFileRepository.save(cloudFile);
            log.info("Rename file \"{}\" to \"{}\" for user with id \"{}\"", fileName, newFileName, userId);
        } catch (Exception e) {
            final String message = format("Error renaming file \"{0}\"", fileName);
            log.error(message);
            throw new RenameFileException(message, e);
        }
    }
}

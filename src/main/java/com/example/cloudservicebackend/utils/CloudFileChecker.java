package com.example.cloudservicebackend.utils;

import com.example.cloudservicebackend.exception.FileNotExistsException;
import com.example.cloudservicebackend.exception.FileOperationFailedException;
import com.example.cloudservicebackend.repository.CloudFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.text.MessageFormat.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class CloudFileChecker {
    private final CloudFileRepository cloudFileRepository;

    @Transactional
    public void checkExistsFile(String fileName, long userId) {
        if (isFileNotExist(fileName, userId)) {
            final String message = format("File \"{0}\" does not exist", fileName);
            log.error(message);
            throw new FileNotExistsException(message);
        }
    }

    private boolean isFileNotExist(String fileName, long userId) {
        try {
            return !cloudFileRepository.existsByFileNameAndUserId(fileName, userId);
        } catch (Exception e) {
            final String message = format("Operation error with file \"{0}\"}", fileName);
            log.error(message);
            throw new FileOperationFailedException(message, e);
        }
    }
}

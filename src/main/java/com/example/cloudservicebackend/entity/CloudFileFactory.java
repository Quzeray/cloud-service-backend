package com.example.cloudservicebackend.entity;

import com.example.cloudservicebackend.exception.CreateFileException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static java.text.MessageFormat.format;

@Log4j2
@Component
public class CloudFileFactory {
    public CloudFile createCloudFile(String fileName, CloudUser user, MultipartFile file) {
        try {
            return CloudFile.builder()
                    .fileName(fileName)
                    .user(user)
                    .data(file.getBytes())
                    .build();
        } catch (Exception e) {
            final String message = format("Error creating file \"{0}\"", fileName);
            log.error(message);
            throw new CreateFileException(message, e);
        }
    }
}

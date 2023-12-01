package com.example.cloudservicebackend.utils;

import com.example.cloudservicebackend.exception.BadLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

import static java.text.MessageFormat.format;

@Slf4j
public class CommonUtils {
    public PageRequest getPageRequest(int limit) {
        try {
            return PageRequest.of(0, limit);
        } catch (Exception e) {
            final String message = format("The limit \"{0}\" is set incorrectly", limit);
            log.error(message);
            throw new BadLimitException(message, e);
        }
    }
}

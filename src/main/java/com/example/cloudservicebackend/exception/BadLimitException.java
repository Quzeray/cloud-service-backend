package com.example.cloudservicebackend.exception;

public class BadLimitException extends CloudServiceException {
    public BadLimitException() {
        super();
    }

    public BadLimitException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BadLimitException(String msg) {
        super(msg);
    }
}

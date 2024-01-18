package com.example.cloudservicebackend.exception;

public class UserExistsException extends CloudServiceException {
    public UserExistsException() {
    }

    public UserExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserExistsException(String msg) {
        super(msg);
    }
}

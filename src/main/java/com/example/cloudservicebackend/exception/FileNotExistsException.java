package com.example.cloudservicebackend.exception;

public class FileNotExistsException extends CloudServiceException {
    public FileNotExistsException() {
        super();
    }

    public FileNotExistsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FileNotExistsException(String msg) {
        super(msg);
    }
}

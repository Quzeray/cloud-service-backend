package com.example.cloudservicebackend.exception;

public class FileOperationFailedException extends CloudServiceException {
    public FileOperationFailedException() {
        super();
    }

    public FileOperationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public FileOperationFailedException(String msg) {
        super(msg);
    }
}

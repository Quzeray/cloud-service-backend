package com.example.cloudservicebackend.exception;

public class CloudServiceException extends RuntimeException {
    public CloudServiceException() {
        super();
    }

    public CloudServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CloudServiceException(String msg) {
        super(msg);
    }
}

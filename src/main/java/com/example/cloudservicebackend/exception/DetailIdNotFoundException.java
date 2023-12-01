package com.example.cloudservicebackend.exception;

public class DetailIdNotFoundException extends CloudServiceException {
    public DetailIdNotFoundException() {
        super();
    }

    public DetailIdNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DetailIdNotFoundException(String msg) {
        super(msg);
    }
}

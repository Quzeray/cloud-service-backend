package com.example.cloudservicebackend.exception;

public class RegistrationException extends CloudServiceException {
    public RegistrationException() {
    }

    public RegistrationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RegistrationException(String msg) {
        super(msg);
    }

}

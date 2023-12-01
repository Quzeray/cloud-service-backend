package com.example.cloudservicebackend.exception;

public class CreateFileException extends FileOperationFailedException {
    public CreateFileException() {
        super();
    }

    public CreateFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CreateFileException(String msg) {
        super(msg);
    }
}

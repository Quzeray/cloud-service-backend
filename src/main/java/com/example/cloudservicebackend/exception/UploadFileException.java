package com.example.cloudservicebackend.exception;

public class UploadFileException extends FileOperationFailedException {
    public UploadFileException() {
        super();
    }

    public UploadFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UploadFileException(String msg) {
        super(msg);
    }
}

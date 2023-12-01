package com.example.cloudservicebackend.exception;

public class DeleteFileException extends FileOperationFailedException {
    public DeleteFileException() {
        super();
    }

    public DeleteFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DeleteFileException(String msg) {
        super(msg);
    }
}

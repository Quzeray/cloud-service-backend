package com.example.cloudservicebackend.exception;

public class RenameFileException extends FileOperationFailedException {
    public RenameFileException() {
        super();
    }

    public RenameFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RenameFileException(String msg) {
        super(msg);
    }
}

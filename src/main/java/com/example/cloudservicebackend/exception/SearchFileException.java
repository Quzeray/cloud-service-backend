package com.example.cloudservicebackend.exception;

public class SearchFileException extends FileOperationFailedException {
    public SearchFileException() {
        super();
    }

    public SearchFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SearchFileException(String msg) {
        super(msg);
    }
}

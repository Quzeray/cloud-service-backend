package com.example.cloudservicebackend.exception;

public class DownloadFileException extends FileOperationFailedException {
    public DownloadFileException() {
        super();
    }

    public DownloadFileException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DownloadFileException(String msg) {
        super(msg);
    }
}

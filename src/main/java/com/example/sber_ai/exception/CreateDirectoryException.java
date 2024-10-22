package com.example.sber_ai.exception;

public class CreateDirectoryException extends RuntimeException {
    public CreateDirectoryException(String cannotCreateTempDir) {
        super(cannotCreateTempDir);
    }
}

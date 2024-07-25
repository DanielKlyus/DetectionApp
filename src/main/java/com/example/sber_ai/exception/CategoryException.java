package com.example.sber_ai.exception;

public class CategoryException extends RuntimeException {
    public CategoryException(String message, Long categoryId) {
        super(message + categoryId);
    }

    public CategoryException(String message) {
        super(message);
    }
}

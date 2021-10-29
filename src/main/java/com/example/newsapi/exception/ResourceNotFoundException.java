package com.example.newsapi.exception;

/**
 * Exception class for exceptions when resource is not present in repository
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

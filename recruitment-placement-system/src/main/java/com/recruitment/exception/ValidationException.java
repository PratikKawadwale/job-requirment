package com.recruitment.exception;

/**
 * Thrown when input data or business entity validation rules fail.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

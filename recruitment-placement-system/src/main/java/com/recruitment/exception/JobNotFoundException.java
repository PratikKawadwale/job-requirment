package com.recruitment.exception;

/**
 * Thrown when a requested job cannot be found in the database.
 */
public class JobNotFoundException extends Exception {
    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(int jobId) {
        super("Job not found with ID: " + jobId);
    }
}

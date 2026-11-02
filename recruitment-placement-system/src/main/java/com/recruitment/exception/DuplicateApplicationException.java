package com.recruitment.exception;

/**
 * Thrown when a candidate attempts to apply for the same job more than once.
 */
public class DuplicateApplicationException extends Exception {
    public DuplicateApplicationException(String message) {
        super(message);
    }

    public DuplicateApplicationException(int candidateId, int jobId) {
        super("Candidate ID " + candidateId + " has already submitted an application for Job ID " + jobId + ".");
    }
}

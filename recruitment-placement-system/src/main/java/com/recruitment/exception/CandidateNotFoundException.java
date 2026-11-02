package com.recruitment.exception;

/**
 * Thrown when a requested candidate cannot be found in the database.
 */
public class CandidateNotFoundException extends Exception {
    public CandidateNotFoundException(String message) {
        super(message);
    }

    public CandidateNotFoundException(int candidateId) {
        super("Candidate not found with ID: " + candidateId);
    }
}

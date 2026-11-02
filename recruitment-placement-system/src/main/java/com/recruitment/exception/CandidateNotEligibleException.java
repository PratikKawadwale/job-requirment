package com.recruitment.exception;

/**
 * Thrown when candidate eligibility criteria fail during application processing.
 */
public class CandidateNotEligibleException extends Exception {
    public CandidateNotEligibleException(String message) {
        super(message);
    }
}

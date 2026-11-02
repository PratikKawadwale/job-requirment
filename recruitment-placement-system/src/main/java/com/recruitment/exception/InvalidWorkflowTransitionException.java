package com.recruitment.exception;

/**
 * Thrown when an invalid status transition is attempted in the recruitment lifecycle.
 */
public class InvalidWorkflowTransitionException extends ValidationException {

    public InvalidWorkflowTransitionException(String message) {
        super(message);
    }

    public InvalidWorkflowTransitionException(String currentStatus, String targetStatus) {
        super(String.format("Invalid recruitment status transition: cannot move directly from '%s' to '%s'.", currentStatus, targetStatus));
    }
}

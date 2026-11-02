package com.recruitment.service;

import com.recruitment.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationServiceTest {

    private ApplicationService applicationService;

    @BeforeEach
    public void setUp() {
        applicationService = new ApplicationService();
    }

    @Test
    @DisplayName("Workflow Transitions: Valid state transitions pass")
    public void testValidTransitions() {
        assertDoesNotThrow(() -> applicationService.validateTransition("APPLIED", "SHORTLISTED"));
        assertDoesNotThrow(() -> applicationService.validateTransition("SHORTLISTED", "INTERVIEW"));
        assertDoesNotThrow(() -> applicationService.validateTransition("INTERVIEW", "SELECTED"));
        assertDoesNotThrow(() -> applicationService.validateTransition("SELECTED", "OFFERED"));
        assertDoesNotThrow(() -> applicationService.validateTransition("OFFERED", "JOINED"));
        assertDoesNotThrow(() -> applicationService.validateTransition("APPLIED", "REJECTED"));
    }

    @Test
    @DisplayName("Workflow Transitions: Direct transition APPLIED -> JOINED must be rejected")
    public void testIllegalDirectJumpToJoined() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> applicationService.validateTransition("APPLIED", "JOINED"));
        assertTrue(ex.getMessage().contains("Invalid recruitment status transition"));
    }

    @Test
    @DisplayName("Workflow Transitions: Transition from terminal JOINED status must be rejected")
    public void testTerminalJoinedStatusImmutable() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> applicationService.validateTransition("JOINED", "SHORTLISTED"));
        assertTrue(ex.getMessage().contains("Invalid recruitment status transition"));
    }
}

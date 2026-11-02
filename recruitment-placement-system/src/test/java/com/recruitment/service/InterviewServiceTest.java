package com.recruitment.service;

import com.recruitment.exception.ValidationException;
import com.recruitment.model.Interview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterviewServiceTest {

    private InterviewService interviewService;

    @BeforeEach
    public void setUp() {
        interviewService = new InterviewService();
    }

    private Interview createValidInterview() {
        Interview i = new Interview();
        i.setApplicationId(1);
        i.setCandidateId(1);
        i.setCompanyId(1);
        i.setJobId(1);
        i.setRoundName("Technical Round 1");
        i.setInterviewDate(LocalDate.now().plusDays(2));
        i.setInterviewTime("11:00 AM");
        i.setInterviewMode("ONLINE");
        i.setInterviewerName("John Doe");
        return i;
    }

    @Test
    @DisplayName("Interview Validation: Valid interview passes")
    public void testValidInterview() {
        Interview i = createValidInterview();
        assertDoesNotThrow(() -> interviewService.validateInterview(i));
    }

    @Test
    @DisplayName("Interview Validation: Missing application ID must throw ValidationException")
    public void testMissingApplicationId() {
        Interview i = createValidInterview();
        i.setApplicationId(0);
        ValidationException ex = assertThrows(ValidationException.class, () -> interviewService.validateInterview(i));
        assertTrue(ex.getMessage().contains("Valid application ID is mandatory"));
    }
}

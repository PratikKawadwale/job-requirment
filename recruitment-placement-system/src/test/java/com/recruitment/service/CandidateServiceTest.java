package com.recruitment.service;

import com.recruitment.exception.ValidationException;
import com.recruitment.model.Candidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CandidateServiceTest {

    private CandidateService candidateService;

    @BeforeEach
    public void setUp() {
        candidateService = new CandidateService();
    }

    private Candidate createValidCandidate() {
        Candidate c = new Candidate();
        c.setFullName("Rohan Patil");
        c.setEmail("rohan.patil@test.com");
        c.setMobile("9876543210");
        c.setDateOfBirth(LocalDate.of(2002, 1, 15));
        c.setGender("Male");
        c.setQualification("B.Tech CSE");
        c.setCollege("COEP");
        c.setUniversity("SPPU");
        c.setPassingYear(2024);
        c.setPercentage(78.5);
        c.setBacklogCount(0);
        c.setPrimaryTechnology("Java");
        c.setCurrentLocation("Pune");
        c.setPreferredLocation("Pune");
        return c;
    }

    @Test
    @DisplayName("Candidate Validation: Valid candidate should pass validation")
    public void testValidCandidatePasses() {
        Candidate c = createValidCandidate();
        assertDoesNotThrow(() -> candidateService.validateCandidate(c));
    }

    @Test
    @DisplayName("Candidate Validation: Percentage below 0 must throw ValidationException")
    public void testInvalidPercentageBelowZero() {
        Candidate c = createValidCandidate();
        c.setPercentage(-5.0);
        ValidationException ex = assertThrows(ValidationException.class, () -> candidateService.validateCandidate(c));
        assertTrue(ex.getMessage().contains("Percentage must be between 0 and 100"));
    }

    @Test
    @DisplayName("Candidate Validation: Percentage above 100 must throw ValidationException")
    public void testInvalidPercentageAboveHundred() {
        Candidate c = createValidCandidate();
        c.setPercentage(105.0);
        ValidationException ex = assertThrows(ValidationException.class, () -> candidateService.validateCandidate(c));
        assertTrue(ex.getMessage().contains("Percentage must be between 0 and 100"));
    }

    @Test
    @DisplayName("Candidate Validation: Negative backlog count must throw ValidationException")
    public void testNegativeBacklogsRejected() {
        Candidate c = createValidCandidate();
        c.setBacklogCount(-1);
        ValidationException ex = assertThrows(ValidationException.class, () -> candidateService.validateCandidate(c));
        assertTrue(ex.getMessage().contains("Backlog count cannot be negative"));
    }

    @Test
    @DisplayName("Candidate Validation: Invalid email format must throw ValidationException")
    public void testInvalidEmailRejected() {
        Candidate c = createValidCandidate();
        c.setEmail("invalid-email-address");
        ValidationException ex = assertThrows(ValidationException.class, () -> candidateService.validateCandidate(c));
        assertTrue(ex.getMessage().contains("Invalid email address format"));
    }
}

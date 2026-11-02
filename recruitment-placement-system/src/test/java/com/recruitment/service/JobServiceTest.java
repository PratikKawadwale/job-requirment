package com.recruitment.service;

import com.recruitment.exception.ValidationException;
import com.recruitment.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JobServiceTest {

    private JobService jobService;

    @BeforeEach
    public void setUp() {
        jobService = new JobService();
    }

    private Job createValidJob() {
        Job j = new Job();
        j.setJobCode("JOB-TEST-01");
        j.setCompanyId(1);
        j.setJobTitle("Software Engineer");
        j.setTechnology("Java");
        j.setOpenings(5);
        j.setLocation("Pune");
        j.setMinimumPackage(500000.0);
        j.setMaximumPackage(800000.0);
        j.setMinimumPercentage(65.0);
        j.setPassingYear(2024);
        j.setMaximumBacklogs(0);
        j.setDeadline(LocalDate.now().plusMonths(2));
        return j;
    }

    @Test
    @DisplayName("Job Validation: Valid job details pass validation")
    public void testValidJobPasses() {
        Job j = createValidJob();
        assertDoesNotThrow(() -> jobService.validateJob(j));
    }

    @Test
    @DisplayName("Job Validation: Zero openings must throw ValidationException")
    public void testZeroOpeningsRejected() {
        Job j = createValidJob();
        j.setOpenings(0);
        ValidationException ex = assertThrows(ValidationException.class, () -> jobService.validateJob(j));
        assertTrue(ex.getMessage().contains("Openings must be greater than zero"));
    }

    @Test
    @DisplayName("Job Validation: Min package exceeding max package must throw ValidationException")
    public void testMinPackageExceedsMaxPackage() {
        Job j = createValidJob();
        j.setMinimumPackage(900000.0);
        j.setMaximumPackage(600000.0);
        ValidationException ex = assertThrows(ValidationException.class, () -> jobService.validateJob(j));
        assertTrue(ex.getMessage().contains("Minimum package cannot exceed maximum package"));
    }

    @Test
    @DisplayName("Job Validation: Negative backlog limit must throw ValidationException")
    public void testNegativeBacklogsRejected() {
        Job j = createValidJob();
        j.setMaximumBacklogs(-2);
        ValidationException ex = assertThrows(ValidationException.class, () -> jobService.validateJob(j));
        assertTrue(ex.getMessage().contains("Maximum backlogs cannot be negative"));
    }
}

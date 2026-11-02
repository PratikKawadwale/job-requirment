package com.recruitment.service;

import com.recruitment.exception.ValidationException;
import com.recruitment.model.Offer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OfferAndPlacementServiceTest {

    private OfferService offerService;

    @BeforeEach
    public void setUp() {
        offerService = new OfferService();
    }

    private Offer createValidOffer() {
        Offer o = new Offer();
        o.setApplicationId(1);
        o.setCandidateId(1);
        o.setCompanyId(1);
        o.setJobId(1);
        o.setDesignation("Software Engineer");
        o.setOfferedPackage(650000.0);
        o.setOfferDate(LocalDate.now());
        o.setJoiningDate(LocalDate.now().plusMonths(1));
        return o;
    }

    @Test
    @DisplayName("Offer Validation: Valid offer passes validation")
    public void testValidOffer() {
        Offer o = createValidOffer();
        assertDoesNotThrow(() -> offerService.validateOffer(o));
    }

    @Test
    @DisplayName("Offer Validation: Zero or negative package must throw ValidationException")
    public void testNegativePackageRejected() {
        Offer o = createValidOffer();
        o.setOfferedPackage(-1000.0);
        ValidationException ex = assertThrows(ValidationException.class, () -> offerService.validateOffer(o));
        assertTrue(ex.getMessage().contains("Offered package must be greater than zero"));
    }

    @Test
    @DisplayName("Offer Validation: Expected joining date before offer date must throw ValidationException")
    public void testJoiningBeforeOfferDateRejected() {
        Offer o = createValidOffer();
        o.setOfferDate(LocalDate.now());
        o.setJoiningDate(LocalDate.now().minusDays(5));
        ValidationException ex = assertThrows(ValidationException.class, () -> offerService.validateOffer(o));
        assertTrue(ex.getMessage().contains("Expected joining date cannot be before offer date"));
    }
}

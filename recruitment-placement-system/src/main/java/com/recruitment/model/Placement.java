package com.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing a confirmed Placement record matching rpms_db.placements table.
 */
public class Placement {

    private int placementId;
    private int candidateId;
    private int companyId;
    private int jobId;
    private int offerId;
    private String designation;
    private BigDecimal finalPackageLpa;
    private LocalDate expectedJoiningDate;
    private LocalDate actualJoiningDate;
    private String joiningStatus; // PENDING, JOINED, NOT_JOINED, DEFERRED, NO_SHOW
    private String placementStatus; // ACTIVE, CANCELLED
    private LocalDateTime createdAt;

    // Joined properties for reporting and display
    private String candidateName;
    private String candidateCode;
    private String companyName;
    private String companyCode;
    private String jobTitle;

    public Placement() {
        this.joiningStatus = "PENDING";
        this.placementStatus = "ACTIVE";
        this.finalPackageLpa = BigDecimal.ZERO;
    }

    public Placement(int placementId, int candidateId, int companyId, int jobId, int offerId,
                     String designation, BigDecimal finalPackageLpa, LocalDate expectedJoiningDate,
                     LocalDate actualJoiningDate, String joiningStatus, String placementStatus) {
        this.placementId = placementId;
        this.candidateId = candidateId;
        this.companyId = companyId;
        this.jobId = jobId;
        this.offerId = offerId;
        this.designation = designation;
        this.finalPackageLpa = finalPackageLpa;
        this.expectedJoiningDate = expectedJoiningDate;
        this.actualJoiningDate = actualJoiningDate;
        this.joiningStatus = joiningStatus;
        this.placementStatus = placementStatus;
    }

    public int getPlacementId() {
        return placementId;
    }

    public void setPlacementId(int placementId) {
        this.placementId = placementId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public BigDecimal getFinalPackageLpa() {
        return finalPackageLpa;
    }

    public void setFinalPackageLpa(BigDecimal finalPackageLpa) {
        this.finalPackageLpa = finalPackageLpa;
    }

    public LocalDate getExpectedJoiningDate() {
        return expectedJoiningDate;
    }

    public void setExpectedJoiningDate(LocalDate expectedJoiningDate) {
        this.expectedJoiningDate = expectedJoiningDate;
    }

    public LocalDate getActualJoiningDate() {
        return actualJoiningDate;
    }

    public void setActualJoiningDate(LocalDate actualJoiningDate) {
        this.actualJoiningDate = actualJoiningDate;
    }

    public String getJoiningStatus() {
        return joiningStatus;
    }

    public void setJoiningStatus(String joiningStatus) {
        this.joiningStatus = joiningStatus;
    }

    public String getPlacementStatus() {
        return placementStatus;
    }

    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getFinalPackage() {
        return finalPackageLpa != null ? finalPackageLpa.doubleValue() : 0.0;
    }

    public void setFinalPackage(double finalPackage) {
        this.finalPackageLpa = BigDecimal.valueOf(finalPackage);
    }

    @Override
    public String toString() {
        return String.format("Placement [ID=%d, OfferID=%d, Candidate='%s', Company='%s', Desig='%s', Package=%s LPA, Joined=%s, JoiningStatus='%s', PlacementStatus='%s']",
                placementId, offerId, (candidateName != null ? candidateName : String.valueOf(candidateId)),
                (companyName != null ? companyName : String.valueOf(companyId)), designation, finalPackageLpa,
                (actualJoiningDate != null ? actualJoiningDate : "Pending"), joiningStatus, placementStatus);
    }
}

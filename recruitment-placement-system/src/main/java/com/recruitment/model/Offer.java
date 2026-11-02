package com.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing an Offer entity matching rpms_db.offers table.
 */
public class Offer {

    private int offerId;
    private String offerCode;
    private int applicationId;
    private String designation;
    private BigDecimal offeredPackageLpa;
    private LocalDate offerDate;
    private LocalDate expectedJoiningDate;
    private String offerStatus; // OFFERED, ACCEPTED, REJECTED, EXPIRED, WITHDRAWN
    private String remarks;
    private LocalDateTime createdAt;

    // Joined properties for reporting and display
    private int candidateId;
    private int companyId;
    private int jobId;
    private String candidateName;
    private String companyName;
    private String jobTitle;

    public Offer() {
        this.offerStatus = "OFFERED";
        this.offerDate = LocalDate.now();
        this.offeredPackageLpa = BigDecimal.ZERO;
    }

    public Offer(int offerId, String offerCode, int applicationId, String designation,
                 BigDecimal offeredPackageLpa, LocalDate offerDate, LocalDate expectedJoiningDate,
                 String offerStatus, String remarks) {
        this.offerId = offerId;
        this.offerCode = offerCode;
        this.applicationId = applicationId;
        this.designation = designation;
        this.offeredPackageLpa = offeredPackageLpa;
        this.offerDate = offerDate;
        this.expectedJoiningDate = expectedJoiningDate;
        this.offerStatus = offerStatus;
        this.remarks = remarks;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public BigDecimal getOfferedPackageLpa() {
        return offeredPackageLpa;
    }

    public void setOfferedPackageLpa(BigDecimal offeredPackageLpa) {
        this.offeredPackageLpa = offeredPackageLpa;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public LocalDate getExpectedJoiningDate() {
        return expectedJoiningDate;
    }

    public void setExpectedJoiningDate(LocalDate expectedJoiningDate) {
        this.expectedJoiningDate = expectedJoiningDate;
    }

    public String getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(String offerStatus) {
        this.offerStatus = offerStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods for joins and backward compatibility
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

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getOfferedPackage() {
        return offeredPackageLpa != null ? offeredPackageLpa.doubleValue() : 0.0;
    }

    public void setOfferedPackage(double offeredPackage) {
        this.offeredPackageLpa = BigDecimal.valueOf(offeredPackage);
    }

    public LocalDate getJoiningDate() {
        return expectedJoiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.expectedJoiningDate = joiningDate;
    }

    @Override
    public String toString() {
        return String.format("Offer [ID=%d, Code='%s', AppID=%d, Candidate='%s', Company='%s', Designation='%s', Package=%s LPA, OfferDate=%s, JoiningDate=%s, Status='%s']",
                offerId, offerCode, applicationId, (candidateName != null ? candidateName : String.valueOf(candidateId)),
                (companyName != null ? companyName : String.valueOf(companyId)), designation, offeredPackageLpa,
                offerDate, expectedJoiningDate, offerStatus);
    }
}

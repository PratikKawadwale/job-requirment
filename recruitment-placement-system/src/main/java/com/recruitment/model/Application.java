package com.recruitment.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing an Application entity matching rpms_db.applications table.
 */
public class Application {

    private int applicationId;
    private String applicationCode;
    private int candidateId;
    private int jobId;
    private LocalDateTime appliedAt;
    private String eligibilityStatus; // PENDING, ELIGIBLE, NOT_ELIGIBLE
    private String eligibilityReason;
    private String applicationStatus; // APPLIED, ELIGIBILITY_VERIFIED, SHORTLISTED, INTERVIEW_SCHEDULED, INTERVIEWED, SELECTED, REJECTED, OFFERED, JOINED, WITHDRAWN
    private String source;
    private Integer assignedRecruiterId;
    private LocalDateTime updatedAt;

    // Joined properties for reporting and display
    private String candidateCode;
    private String candidateName;
    private String jobCode;
    private String jobTitle;
    private String companyName;
    private String assignedRecruiterName;

    public Application() {
        this.appliedAt = LocalDateTime.now();
        this.applicationStatus = "APPLIED";
        this.eligibilityStatus = "PENDING";
    }

    public Application(int applicationId, String applicationCode, int candidateId, int jobId,
                       LocalDateTime appliedAt, String eligibilityStatus, String eligibilityReason,
                       String applicationStatus, String source, Integer assignedRecruiterId) {
        this.applicationId = applicationId;
        this.applicationCode = applicationCode;
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.appliedAt = appliedAt;
        this.eligibilityStatus = eligibilityStatus;
        this.eligibilityReason = eligibilityReason;
        this.applicationStatus = applicationStatus;
        this.source = source;
        this.assignedRecruiterId = assignedRecruiterId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDate getAppliedDate() {
        return appliedAt != null ? appliedAt.toLocalDate() : null;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedAt = appliedDate != null ? appliedDate.atStartOfDay() : null;
    }

    public String getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(String eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getEligibilityReason() {
        return eligibilityReason;
    }

    public void setEligibilityReason(String eligibilityReason) {
        this.eligibilityReason = eligibilityReason;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getAssignedRecruiterId() {
        return assignedRecruiterId;
    }

    public void setAssignedRecruiterId(Integer assignedRecruiterId) {
        this.assignedRecruiterId = assignedRecruiterId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAssignedRecruiterName() {
        return assignedRecruiterName;
    }

    public void setAssignedRecruiterName(String assignedRecruiterName) {
        this.assignedRecruiterName = assignedRecruiterName;
    }

    @Override
    public String toString() {
        return String.format("Application [ID=%d, Code='%s', CandidateID=%d (%s), JobID=%d (%s - %s), Applied=%s, Eligibility='%s', Status='%s']",
                applicationId, applicationCode, candidateId, (candidateName != null ? candidateName : ""),
                jobId, (jobTitle != null ? jobTitle : ""), (companyName != null ? companyName : ""),
                (appliedAt != null ? appliedAt.toLocalDate() : ""), eligibilityStatus, applicationStatus);
    }
}

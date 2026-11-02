package com.recruitment.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing an Interview round scheduled for a candidate's application.
 */
public class Interview {

    private int interviewId;
    private int applicationId;
    private int roundNumber = 1;
    private String roundType; // HR, TECHNICAL, CODING, MANAGERIAL
    private int candidateId;
    private int companyId;
    private int jobId;
    private String candidateName; // Joined property
    private String companyName;   // Joined property
    private String jobTitle;      // Joined property
    private String roundName;
    private LocalDate interviewDate;
    private String interviewTime;
    private String interviewMode; // ONLINE, OFFLINE, TELEPHONIC
    private String interviewerName;
    private String meetingOrLocation;
    private String interviewStatus; // SCHEDULED, COMPLETED, RESCHEDULED, CANCELLED, NO_SHOW
    private Integer technicalRating; // 1 to 5
    private Integer communicationRating; // 1 to 5
    private String remarks;
    private String result; // NEXT_ROUND, SELECTED, REJECTED, ON_HOLD
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Interview() {
        this.interviewMode = "ONLINE";
        this.interviewStatus = "SCHEDULED";
        this.result = "ON_HOLD";
    }

    public Interview(int interviewId, int applicationId, int candidateId, int companyId, int jobId,
                     String roundName, LocalDate interviewDate, String interviewTime, String interviewMode,
                     String interviewerName, String interviewStatus, Integer technicalRating,
                     Integer communicationRating, String remarks, String result) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.candidateId = candidateId;
        this.companyId = companyId;
        this.jobId = jobId;
        this.roundName = roundName;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.interviewMode = interviewMode;
        this.interviewerName = interviewerName;
        this.interviewStatus = interviewStatus;
        this.technicalRating = technicalRating;
        this.communicationRating = communicationRating;
        this.remarks = remarks;
        this.result = result;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getRoundType() {
        return roundType != null ? roundType : roundName;
    }

    public void setRoundType(String roundType) {
        this.roundType = roundType;
        if (this.roundName == null) this.roundName = roundType;
    }

    public String getMeetingOrLocation() {
        return meetingOrLocation;
    }

    public void setMeetingOrLocation(String meetingOrLocation) {
        this.meetingOrLocation = meetingOrLocation;
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

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getInterviewTime() {
        return interviewTime;
    }

    public void setInterviewTime(String interviewTime) {
        this.interviewTime = interviewTime;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public Integer getTechnicalRating() {
        return technicalRating;
    }

    public void setTechnicalRating(Integer technicalRating) {
        this.technicalRating = technicalRating;
    }

    public Integer getCommunicationRating() {
        return communicationRating;
    }

    public void setCommunicationRating(Integer communicationRating) {
        this.communicationRating = communicationRating;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("Interview [ID=%d, AppID=%d, Candidate='%s', Company='%s', Round='%s', Date=%s %s, Mode='%s', Status='%s', Result='%s']",
                interviewId, applicationId, (candidateName != null ? candidateName : String.valueOf(candidateId)),
                (companyName != null ? companyName : String.valueOf(companyId)), roundName, interviewDate, interviewTime,
                interviewMode, interviewStatus, result);
    }
}

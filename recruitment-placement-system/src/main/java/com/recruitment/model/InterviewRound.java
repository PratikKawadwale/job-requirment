package com.recruitment.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Model representing an interview round in interview_rounds table.
 */
public class InterviewRound {

    private int interviewId;
    private int applicationId;
    private int roundNumber;
    private String roundType; // HR, TECHNICAL, CODING, MANAGERIAL
    private LocalDate scheduledDate;
    private LocalTime scheduledTime;
    private String mode; // ONLINE, OFFLINE, TELEPHONIC
    private String interviewerName;
    private String meetingOrLocation;
    private String status; // SCHEDULED, COMPLETED, RESCHEDULED, CANCELLED, NO_SHOW
    private LocalDateTime createdAt;

    // Join helper fields
    private String candidateName;
    private String jobTitle;
    private String companyName;

    // Optional attached feedback
    private InterviewFeedback feedback;

    public InterviewRound() {
        this.status = "SCHEDULED";
        this.mode = "ONLINE";
        this.roundNumber = 1;
        this.scheduledDate = LocalDate.now();
    }

    public InterviewRound(int interviewId, int applicationId, int roundNumber, String roundType,
                          LocalDate scheduledDate, LocalTime scheduledTime, String mode,
                          String interviewerName, String meetingOrLocation, String status) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.roundNumber = roundNumber;
        this.roundType = roundType;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.mode = mode;
        this.interviewerName = interviewerName;
        this.meetingOrLocation = meetingOrLocation;
        this.status = status;
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
        return roundType;
    }

    public void setRoundType(String roundType) {
        this.roundType = roundType;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getMeetingOrLocation() {
        return meetingOrLocation;
    }

    public void setMeetingOrLocation(String meetingOrLocation) {
        this.meetingOrLocation = meetingOrLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public InterviewFeedback getFeedback() {
        return feedback;
    }

    public void setFeedback(InterviewFeedback feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return String.format("InterviewRound [ID=%d, AppID=%d, Round#%d (%s), Date=%s %s, Mode='%s', Status='%s', Interviewer='%s']",
                interviewId, applicationId, roundNumber, roundType, scheduledDate,
                (scheduledTime != null ? scheduledTime : ""), mode, status,
                (interviewerName != null ? interviewerName : "N/A"));
    }
}

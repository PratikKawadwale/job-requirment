package com.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model representing interview_feedback table.
 */
public class InterviewFeedback {

    private int feedbackId;
    private int interviewId;
    private BigDecimal technicalRating;
    private BigDecimal communicationRating;
    private BigDecimal problemSolvingRating;
    private BigDecimal overallRating;
    private String recommendation; // NEXT_ROUND, SELECTED, REJECTED, ON_HOLD
    private String remarks;
    private int submittedBy;
    private LocalDateTime submittedAt;

    // Join helper
    private String submittedByName;

    public InterviewFeedback() {
        this.recommendation = "ON_HOLD";
        this.submittedAt = LocalDateTime.now();
    }

    public InterviewFeedback(int feedbackId, int interviewId, BigDecimal technicalRating,
                             BigDecimal communicationRating, BigDecimal problemSolvingRating,
                             BigDecimal overallRating, String recommendation, String remarks,
                             int submittedBy, LocalDateTime submittedAt) {
        this.feedbackId = feedbackId;
        this.interviewId = interviewId;
        this.technicalRating = technicalRating;
        this.communicationRating = communicationRating;
        this.problemSolvingRating = problemSolvingRating;
        this.overallRating = overallRating;
        this.recommendation = recommendation;
        this.remarks = remarks;
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public BigDecimal getTechnicalRating() {
        return technicalRating;
    }

    public void setTechnicalRating(BigDecimal technicalRating) {
        this.technicalRating = technicalRating;
    }

    public BigDecimal getCommunicationRating() {
        return communicationRating;
    }

    public void setCommunicationRating(BigDecimal communicationRating) {
        this.communicationRating = communicationRating;
    }

    public BigDecimal getProblemSolvingRating() {
        return problemSolvingRating;
    }

    public void setProblemSolvingRating(BigDecimal problemSolvingRating) {
        this.problemSolvingRating = problemSolvingRating;
    }

    public BigDecimal getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(BigDecimal overallRating) {
        this.overallRating = overallRating;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(int submittedBy) {
        this.submittedBy = submittedBy;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public void setSubmittedByName(String submittedByName) {
        this.submittedByName = submittedByName;
    }

    @Override
    public String toString() {
        return String.format("Feedback [FeedbackID=%d, InterviewID=%d, Tech=%s, Comm=%s, Prob=%s, Overall=%s, Rec='%s', By User#%d]",
                feedbackId, interviewId, technicalRating, communicationRating, problemSolvingRating,
                overallRating, recommendation, submittedBy);
    }
}

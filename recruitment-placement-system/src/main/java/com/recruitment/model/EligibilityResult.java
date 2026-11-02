package com.recruitment.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result object returned by Eligibility Check evaluations.
 */
public class EligibilityResult {

    private int candidateId;
    private int jobId;
    private final boolean eligible;
    private final String status; // ELIGIBLE, NOT_ELIGIBLE
    private final List<String> reasons;

    public EligibilityResult(boolean eligible) {
        this.eligible = eligible;
        this.status = eligible ? "ELIGIBLE" : "NOT_ELIGIBLE";
        this.reasons = new ArrayList<>();
        if (eligible) {
            this.reasons.add("Candidate satisfies all job eligibility criteria.");
        }
    }

    public EligibilityResult(boolean eligible, List<String> reasons) {
        this.eligible = eligible;
        this.status = eligible ? "ELIGIBLE" : "NOT_ELIGIBLE";
        this.reasons = (reasons != null) ? new ArrayList<>(reasons) : new ArrayList<>();
    }

    public EligibilityResult(int candidateId, int jobId, boolean eligible, List<String> reasons) {
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.eligible = eligible;
        this.status = eligible ? "ELIGIBLE" : "NOT_ELIGIBLE";
        this.reasons = (reasons != null) ? new ArrayList<>(reasons) : new ArrayList<>();
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

    public boolean isEligible() {
        return eligible;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void addReason(String reason) {
        if (reason != null && !reason.isBlank()) {
            this.reasons.add(reason.trim());
        }
    }

    public String getReasonString() {
        if (reasons.isEmpty()) {
            return eligible ? "Candidate satisfies all job eligibility criteria." : "Eligibility criteria not satisfied.";
        }
        return String.join("; ", reasons);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Eligibility Status for Candidate #%d, Job #%d: %s%n", candidateId, jobId, status));
        sb.append("Reason(s):\n");
        for (String r : reasons) {
            sb.append(" - ").append(r).append("\n");
        }
        return sb.toString();
    }
}

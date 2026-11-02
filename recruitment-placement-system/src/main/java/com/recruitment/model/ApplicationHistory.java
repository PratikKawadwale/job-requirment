package com.recruitment.model;

import java.time.LocalDateTime;

/**
 * Model representing an application status change history record.
 */
public class ApplicationHistory {

    private long historyId;
    private int applicationId;
    private String oldStatus;
    private String newStatus;
    private String remarks;
    private int changedBy;
    private LocalDateTime changedAt;

    // Join helper
    private String changedByName;

    public ApplicationHistory() {
        this.changedAt = LocalDateTime.now();
    }

    public ApplicationHistory(long historyId, int applicationId, String oldStatus,
                              String newStatus, String remarks, int changedBy, LocalDateTime changedAt) {
        this.historyId = historyId;
        this.applicationId = applicationId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(int changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    @Override
    public String toString() {
        return String.format("History [#%d, AppID=%d, %s -> %s, By User#%d, At=%s, Remarks='%s']",
                historyId, applicationId, (oldStatus != null ? oldStatus : "START"),
                newStatus, changedBy, changedAt, remarks);
    }
}

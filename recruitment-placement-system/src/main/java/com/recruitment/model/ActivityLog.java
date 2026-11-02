package com.recruitment.model;

import java.time.LocalDateTime;

/**
 * Model representing activity_logs table in rpms_db.
 */
public class ActivityLog {

    private long logId;
    private Integer userId;
    private String moduleName;
    private String actionName;
    private String recordId;
    private String description;
    private LocalDateTime loggedAt;

    // Join helper
    private String username;

    public ActivityLog() {
        this.loggedAt = LocalDateTime.now();
    }

    public ActivityLog(long logId, Integer userId, String moduleName, String actionName,
                       String recordId, String description, LocalDateTime loggedAt) {
        this.logId = logId;
        this.userId = userId;
        this.moduleName = moduleName;
        this.actionName = actionName;
        this.recordId = recordId;
        this.description = description;
        this.loggedAt = loggedAt;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("ActivityLog [#%d, User=%s, Module='%s', Action='%s', Record='%s', At=%s, Desc='%s']",
                logId, (username != null ? username : (userId != null ? "User#" + userId : "SYSTEM")),
                moduleName, actionName, recordId, loggedAt, description);
    }
}

package com.recruitment.model;

import java.time.LocalDateTime;

public class Qualification {
    private Integer qualificationId;
    private String qualificationName;
    private boolean active;
    private LocalDateTime createdAt;

    public Qualification() {
    }

    public Qualification(Integer qualificationId, String qualificationName, boolean active, LocalDateTime createdAt) {
        this.qualificationId = qualificationId;
        this.qualificationName = qualificationName;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Integer getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(Integer qualificationId) {
        this.qualificationId = qualificationId;
    }

    public String getQualificationName() {
        return qualificationName;
    }

    public void setQualificationName(String qualificationName) {
        this.qualificationName = qualificationName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (Active: %s)", qualificationId, qualificationName, active ? "Yes" : "No");
    }
}

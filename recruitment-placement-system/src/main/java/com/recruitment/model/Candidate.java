package com.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a Candidate entity matching rpms_db candidates table.
 */
public class Candidate {

    private Integer candidateId;
    private String candidateCode;
    private String fullName;
    private String email;
    private String mobile;
    private LocalDate dateOfBirth;
    private String gender;
    private String qualification;
    private String college;
    private String university;
    private int passingYear;
    private BigDecimal percentage;
    private BigDecimal cgpa;
    private int backlogCount;
    private String primaryTechnology;
    private String currentLocation;
    private String preferredLocation;
    private String profileStatus; // ACTIVE, INACTIVE, ON_HOLD, BLACKLISTED
    private String placementStatus; // NOT_PLACED, PLACED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional convenience fields for candidate skills / profile level
    private String secondarySkills;
    private String skillLevel;

    // Attached skills for convenience
    private List<CandidateSkill> skills = new ArrayList<>();

    public Candidate() {
        this.profileStatus = "ACTIVE";
        this.placementStatus = "NOT_PLACED";
        this.backlogCount = 0;
    }

    public Candidate(Integer candidateId, String candidateCode, String fullName, String email, String mobile,
                     LocalDate dateOfBirth, String gender, String qualification, String college, String university,
                     int passingYear, BigDecimal percentage, BigDecimal cgpa, int backlogCount,
                     String primaryTechnology, String currentLocation, String preferredLocation,
                     String profileStatus, String placementStatus) {
        this.candidateId = candidateId;
        this.candidateCode = candidateCode;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.qualification = qualification;
        this.college = college;
        this.university = university;
        this.passingYear = passingYear;
        this.percentage = percentage;
        this.cgpa = cgpa;
        this.backlogCount = backlogCount;
        this.primaryTechnology = primaryTechnology;
        this.currentLocation = currentLocation;
        this.preferredLocation = preferredLocation;
        this.profileStatus = profileStatus != null ? profileStatus : "ACTIVE";
        this.placementStatus = placementStatus != null ? placementStatus : "NOT_PLACED";
    }

    public Integer getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Integer candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateCode() {
        return candidateCode;
    }

    public void setCandidateCode(String candidateCode) {
        this.candidateCode = candidateCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public int getPassingYear() {
        return passingYear;
    }

    public void setPassingYear(int passingYear) {
        this.passingYear = passingYear;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public double getDoublePercentage() {
        return percentage != null ? percentage.doubleValue() : 0.0;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = BigDecimal.valueOf(percentage);
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = BigDecimal.valueOf(cgpa);
    }

    public int getBacklogCount() {
        return backlogCount;
    }

    public void setBacklogCount(int backlogCount) {
        this.backlogCount = backlogCount;
    }

    public String getPrimaryTechnology() {
        return primaryTechnology;
    }

    public void setPrimaryTechnology(String primaryTechnology) {
        this.primaryTechnology = primaryTechnology;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }

    public String getStatus() {
        return profileStatus;
    }

    public void setStatus(String status) {
        this.profileStatus = status;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CandidateSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<CandidateSkill> skills) {
        this.skills = skills;
    }

    public String getSecondarySkills() {
        return secondarySkills;
    }

    public void setSecondarySkills(String secondarySkills) {
        this.secondarySkills = secondarySkills;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | %s | Year: %d | %s%% | Backlogs: %d | Status: %s | Placement: %s",
                candidateCode != null ? candidateCode : ("ID:" + candidateId),
                fullName,
                primaryTechnology != null ? primaryTechnology : "N/A",
                qualification,
                currentLocation != null ? currentLocation : "N/A",
                passingYear,
                percentage != null ? percentage.toString() : "N/A",
                backlogCount,
                profileStatus,
                placementStatus);
    }
}

package com.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model representing a Job Opening entity matching rpms_db.jobs table.
 */
public class Job {

    private int jobId;
    private String jobCode;
    private int companyId;
    private String companyName; // Populated from join for convenience
    private String jobTitle;
    private String employmentType; // FULL_TIME, INTERNSHIP, CONTRACT, PART_TIME
    private String location;
    private int openings;
    private BigDecimal minPackageLpa;
    private BigDecimal maxPackageLpa;
    private BigDecimal minPercentage;
    private Integer minPassingYear;
    private Integer maxPassingYear;
    private int maxBacklogs;
    private BigDecimal experienceMinYears;
    private String qualificationRule;
    private LocalDate postedDate;
    private LocalDate applicationDeadline;
    private String status; // DRAFT, OPEN, CLOSED, CANCELLED
    private int createdBy;
    private LocalDateTime createdAt;

    // Helper for legacy/convenience
    private String technology;

    public Job() {
        this.status = "OPEN";
        this.employmentType = "FULL_TIME";
        this.maxBacklogs = 0;
        this.experienceMinYears = BigDecimal.ZERO;
        this.postedDate = LocalDate.now();
        this.applicationDeadline = LocalDate.now().plusMonths(1);
    }

    public Job(int jobId, String jobCode, int companyId, String jobTitle, String employmentType,
               String location, int openings, BigDecimal minPackageLpa, BigDecimal maxPackageLpa,
               BigDecimal minPercentage, Integer minPassingYear, Integer maxPassingYear,
               int maxBacklogs, BigDecimal experienceMinYears, String qualificationRule,
               LocalDate postedDate, LocalDate applicationDeadline, String status, int createdBy) {
        this.jobId = jobId;
        this.jobCode = jobCode;
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.employmentType = employmentType;
        this.location = location;
        this.openings = openings;
        this.minPackageLpa = minPackageLpa;
        this.maxPackageLpa = maxPackageLpa;
        this.minPercentage = minPercentage;
        this.minPassingYear = minPassingYear;
        this.maxPassingYear = maxPassingYear;
        this.maxBacklogs = maxBacklogs;
        this.experienceMinYears = experienceMinYears != null ? experienceMinYears : BigDecimal.ZERO;
        this.qualificationRule = qualificationRule;
        this.postedDate = postedDate;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.createdBy = createdBy;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
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

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOpenings() {
        return openings;
    }

    public void setOpenings(int openings) {
        this.openings = openings;
    }

    public BigDecimal getMinPackageLpa() {
        return minPackageLpa;
    }

    public void setMinPackageLpa(BigDecimal minPackageLpa) {
        this.minPackageLpa = minPackageLpa;
    }

    public BigDecimal getMaxPackageLpa() {
        return maxPackageLpa;
    }

    public void setMaxPackageLpa(BigDecimal maxPackageLpa) {
        this.maxPackageLpa = maxPackageLpa;
    }

    public BigDecimal getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(BigDecimal minPercentage) {
        this.minPercentage = minPercentage;
    }

    public Integer getMinPassingYear() {
        return minPassingYear;
    }

    public void setMinPassingYear(Integer minPassingYear) {
        this.minPassingYear = minPassingYear;
    }

    public Integer getMaxPassingYear() {
        return maxPassingYear;
    }

    public void setMaxPassingYear(Integer maxPassingYear) {
        this.maxPassingYear = maxPassingYear;
    }

    public int getMaxBacklogs() {
        return maxBacklogs;
    }

    public void setMaxBacklogs(int maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
    }

    public BigDecimal getExperienceMinYears() {
        return experienceMinYears;
    }

    public void setExperienceMinYears(BigDecimal experienceMinYears) {
        this.experienceMinYears = experienceMinYears;
    }

    public String getQualificationRule() {
        return qualificationRule;
    }

    public void setQualificationRule(String qualificationRule) {
        this.qualificationRule = qualificationRule;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Compatibility getters & setters
    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public double getMinimumPackage() {
        return minPackageLpa != null ? minPackageLpa.doubleValue() : 0.0;
    }

    public void setMinimumPackage(double minimumPackage) {
        this.minPackageLpa = BigDecimal.valueOf(minimumPackage);
    }

    public double getMaximumPackage() {
        return maxPackageLpa != null ? maxPackageLpa.doubleValue() : 0.0;
    }

    public void setMaximumPackage(double maximumPackage) {
        this.maxPackageLpa = BigDecimal.valueOf(maximumPackage);
    }

    public double getMinimumPercentage() {
        return minPercentage != null ? minPercentage.doubleValue() : 0.0;
    }

    public void setMinimumPercentage(double minimumPercentage) {
        this.minPercentage = BigDecimal.valueOf(minimumPercentage);
    }

    public int getPassingYear() {
        return minPassingYear != null ? minPassingYear : 0;
    }

    public void setPassingYear(int passingYear) {
        this.minPassingYear = passingYear;
        this.maxPassingYear = passingYear;
    }

    public int getMaximumBacklogs() {
        return maxBacklogs;
    }

    public void setMaximumBacklogs(int maximumBacklogs) {
        this.maxBacklogs = maximumBacklogs;
    }

    public LocalDate getDeadline() {
        return applicationDeadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.applicationDeadline = deadline;
    }

    @Override
    public String toString() {
        return String.format("Job [ID=%d, Code='%s', Title='%s', CompanyID=%d, Loc='%s', Openings=%d, MinPct=%s%%, PassYear=%s-%s, MaxBacklogs=%d, Deadline=%s, Status='%s']",
                jobId, jobCode, jobTitle, companyId, location, openings,
                (minPercentage != null ? minPercentage : "N/A"),
                (minPassingYear != null ? minPassingYear : "Any"),
                (maxPassingYear != null ? maxPassingYear : "Any"),
                maxBacklogs, applicationDeadline, status);
    }
}

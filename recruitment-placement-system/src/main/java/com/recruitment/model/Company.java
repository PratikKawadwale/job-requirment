package com.recruitment.model;

import java.time.LocalDateTime;

/**
 * Model representing a Company entity in the recruitment system.
 */
public class Company {

    private int companyId;
    private String companyCode;
    private String companyName;
    private String industry;
    private String website;
    private String location;
    private String hrName;
    private String hrEmail;
    private String hrMobile;
    private String companyType; // MNC, STARTUP, MID_SIZE, CONSULTANCY
    private String status;      // ACTIVE, INACTIVE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Company() {
        this.companyType = "MNC";
        this.status = "ACTIVE";
    }

    public Company(int companyId, String companyCode, String companyName, String industry, String website, String location,
                   String hrName, String hrEmail, String hrMobile, String companyType, String status) {
        this.companyId = companyId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.industry = industry;
        this.website = website;
        this.location = location;
        this.hrName = hrName;
        this.hrEmail = hrEmail;
        this.hrMobile = hrMobile;
        this.companyType = companyType;
        this.status = status;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHrName() {
        return hrName;
    }

    public void setHrName(String hrName) {
        this.hrName = hrName;
    }

    public String getHrEmail() {
        return hrEmail;
    }

    public void setHrEmail(String hrEmail) {
        this.hrEmail = hrEmail;
    }

    public String getHrMobile() {
        return hrMobile;
    }

    public void setHrMobile(String hrMobile) {
        this.hrMobile = hrMobile;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("Company [ID=%d, Code='%s', Name='%s', Industry='%s', Location='%s', HR='%s' (%s), Type='%s', Status='%s']",
                companyId, companyCode, companyName, industry, location, hrName, hrEmail, companyType, status);
    }
}

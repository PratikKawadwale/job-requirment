package com.recruitment.model;

/**
 * Model representing a job_skills requirement record.
 */
public class JobSkill {

    private int jobSkillId;
    private int jobId;
    private int skillId;
    private String minimumLevel; // BASIC, INTERMEDIATE, ADVANCED
    private boolean mandatory;

    // Join helper fields
    private String skillName;
    private String category;

    public JobSkill() {
        this.mandatory = true;
    }

    public JobSkill(int jobSkillId, int jobId, int skillId, String minimumLevel, boolean mandatory) {
        this.jobSkillId = jobSkillId;
        this.jobId = jobId;
        this.skillId = skillId;
        this.minimumLevel = minimumLevel;
        this.mandatory = mandatory;
    }

    public int getJobSkillId() {
        return jobSkillId;
    }

    public void setJobSkillId(int jobSkillId) {
        this.jobSkillId = jobSkillId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(String minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("JobSkill [ID=%d, Skill='%s', MinLevel='%s', Mandatory=%b]",
                jobSkillId, (skillName != null ? skillName : "Skill#" + skillId),
                minimumLevel, mandatory);
    }
}

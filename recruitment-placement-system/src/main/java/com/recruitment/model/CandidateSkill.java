package com.recruitment.model;

import java.math.BigDecimal;

/**
 * Model representing a candidate_skills record.
 */
public class CandidateSkill {

    private int candidateSkillId;
    private int candidateId;
    private int skillId;
    private String proficiencyLevel; // BASIC, INTERMEDIATE, ADVANCED
    private BigDecimal yearsExperience;
    private boolean primary;

    // Extra join fields for display
    private String skillName;
    private String category;

    public CandidateSkill() {
        this.yearsExperience = BigDecimal.ZERO;
        this.primary = false;
    }

    public CandidateSkill(int candidateSkillId, int candidateId, int skillId,
                          String proficiencyLevel, BigDecimal yearsExperience, boolean primary) {
        this.candidateSkillId = candidateSkillId;
        this.candidateId = candidateId;
        this.skillId = skillId;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsExperience = yearsExperience != null ? yearsExperience : BigDecimal.ZERO;
        this.primary = primary;
    }

    public int getCandidateSkillId() {
        return candidateSkillId;
    }

    public void setCandidateSkillId(int candidateSkillId) {
        this.candidateSkillId = candidateSkillId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public BigDecimal getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(BigDecimal yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
        return String.format("CandidateSkill [ID=%d, Skill='%s', Level='%s', Exp=%s yrs, Primary=%b]",
                candidateSkillId, (skillName != null ? skillName : "Skill#" + skillId),
                proficiencyLevel, yearsExperience, primary);
    }
}

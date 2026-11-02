package com.recruitment.model;

/**
 * Model representing a Skill master entity.
 */
public class Skill {

    private int skillId;
    private String skillName;
    private String category;
    private boolean active;

    public Skill() {
        this.active = true;
    }

    public Skill(int skillId, String skillName, String category, boolean active) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.active = active;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("Skill [ID=%d, Name='%s', Category='%s', Active=%b]",
                skillId, skillName, category, active);
    }
}

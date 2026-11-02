package com.recruitment.service;

import com.recruitment.dao.CandidateSkillDAO;
import com.recruitment.dao.JobSkillDAO;
import com.recruitment.dao.SkillDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.CandidateSkill;
import com.recruitment.model.JobSkill;
import com.recruitment.model.Skill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing skills, candidate skills, and job skills.
 */
public class SkillService {

    private final SkillDAO skillDAO;
    private final CandidateSkillDAO candidateSkillDAO;
    private final JobSkillDAO jobSkillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
        this.candidateSkillDAO = new CandidateSkillDAO();
        this.jobSkillDAO = new JobSkillDAO();
    }

    public SkillService(SkillDAO skillDAO, CandidateSkillDAO candidateSkillDAO, JobSkillDAO jobSkillDAO) {
        this.skillDAO = skillDAO;
        this.candidateSkillDAO = candidateSkillDAO;
        this.jobSkillDAO = jobSkillDAO;
    }

    // Skills Master Operations
    public List<Skill> getAllSkills() throws SQLException {
        return skillDAO.findAll();
    }

    public List<Skill> getActiveSkills() throws SQLException {
        return skillDAO.findActive();
    }

    public Optional<Skill> getSkillById(int skillId) throws SQLException {
        return skillDAO.findById(skillId);
    }

    public Optional<Skill> getSkillByName(String skillName) throws SQLException {
        return skillDAO.findByName(skillName);
    }

    public int addSkill(Skill skill) throws SQLException, ValidationException {
        if (skill.getSkillName() == null || skill.getSkillName().isBlank()) {
            throw new ValidationException("Skill name is required.");
        }
        if (skillDAO.findByName(skill.getSkillName().trim()).isPresent()) {
            throw new ValidationException("Skill already exists: " + skill.getSkillName());
        }
        return skillDAO.insert(skill);
    }

    // Candidate Skills Operations
    public List<CandidateSkill> getSkillsByCandidate(int candidateId) throws SQLException {
        return candidateSkillDAO.findByCandidateId(candidateId);
    }

    public int addCandidateSkill(int candidateId, int skillId, String proficiencyLevel,
                                 BigDecimal yearsExperience, boolean isPrimary) throws SQLException, ValidationException {
        if (yearsExperience != null && yearsExperience.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Years of experience cannot be negative.");
        }
        if (proficiencyLevel != null && !proficiencyLevel.isBlank()) {
            String p = proficiencyLevel.toUpperCase();
            if (!p.equals("BASIC") && !p.equals("INTERMEDIATE") && !p.equals("ADVANCED")) {
                throw new ValidationException("Proficiency must be BASIC, INTERMEDIATE, or ADVANCED.");
            }
        }
        if (candidateSkillDAO.findByCandidateAndSkill(candidateId, skillId).isPresent()) {
            throw new ValidationException("Candidate already has this skill recorded.");
        }

        CandidateSkill cs = new CandidateSkill(0, candidateId, skillId, proficiencyLevel, yearsExperience, isPrimary);
        return candidateSkillDAO.insert(cs);
    }

    public boolean updateCandidateSkill(CandidateSkill cs) throws SQLException, ValidationException {
        if (cs.getYearsExperience() != null && cs.getYearsExperience().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Years of experience cannot be negative.");
        }
        return candidateSkillDAO.update(cs);
    }

    public boolean removeCandidateSkill(int candidateSkillId) throws SQLException {
        return candidateSkillDAO.delete(candidateSkillId);
    }

    // Job Skills Operations
    public List<JobSkill> getSkillsByJob(int jobId) throws SQLException {
        return jobSkillDAO.findByJobId(jobId);
    }

    public int addJobSkill(int jobId, int skillId, String minimumLevel, boolean mandatory) throws SQLException, ValidationException {
        if (minimumLevel != null && !minimumLevel.isBlank()) {
            String p = minimumLevel.toUpperCase();
            if (!p.equals("BASIC") && !p.equals("INTERMEDIATE") && !p.equals("ADVANCED")) {
                throw new ValidationException("Minimum level must be BASIC, INTERMEDIATE, or ADVANCED.");
            }
        }
        if (jobSkillDAO.findByJobAndSkill(jobId, skillId).isPresent()) {
            throw new ValidationException("Job already has this skill requirement specified.");
        }

        JobSkill js = new JobSkill(0, jobId, skillId, minimumLevel, mandatory);
        return jobSkillDAO.insert(js);
    }

    public boolean updateJobSkill(JobSkill js) throws SQLException {
        return jobSkillDAO.update(js);
    }

    public boolean removeJobSkill(int jobSkillId) throws SQLException {
        return jobSkillDAO.delete(jobSkillId);
    }
}

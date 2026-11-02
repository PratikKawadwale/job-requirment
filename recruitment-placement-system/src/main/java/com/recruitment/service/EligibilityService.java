package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.CandidateSkillDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.dao.JobSkillDAO;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.model.Candidate;
import com.recruitment.model.CandidateSkill;
import com.recruitment.model.EligibilityResult;
import com.recruitment.model.Job;
import com.recruitment.model.JobSkill;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dedicated service for checking candidate eligibility against job requirements.
 */
public class EligibilityService {

    private final CandidateDAO candidateDAO;
    private final JobDAO jobDAO;
    private final ApplicationDAO applicationDAO;
    private final CandidateSkillDAO candidateSkillDAO;
    private final JobSkillDAO jobSkillDAO;

    public EligibilityService() {
        this.candidateDAO = new CandidateDAO();
        this.jobDAO = new JobDAO();
        this.applicationDAO = new ApplicationDAO();
        this.candidateSkillDAO = new CandidateSkillDAO();
        this.jobSkillDAO = new JobSkillDAO();
    }

    public EligibilityService(CandidateDAO candidateDAO, JobDAO jobDAO, ApplicationDAO applicationDAO,
                              CandidateSkillDAO candidateSkillDAO, JobSkillDAO jobSkillDAO) {
        this.candidateDAO = candidateDAO;
        this.jobDAO = jobDAO;
        this.applicationDAO = applicationDAO;
        this.candidateSkillDAO = candidateSkillDAO;
        this.jobSkillDAO = jobSkillDAO;
    }

    /**
     * Evaluates all eligibility rules for a candidate applying to a job.
     */
    public EligibilityResult checkEligibility(int candidateId, int jobId)
            throws SQLException, CandidateNotFoundException, JobNotFoundException {

        Candidate candidate = candidateDAO.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate with ID " + candidateId + " not found."));

        Job job = jobDAO.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));

        return evaluate(candidate, job);
    }

    /**
     * Evaluates eligibility given loaded candidate and job instances.
     */
    public EligibilityResult evaluate(Candidate candidate, Job job) throws SQLException {
        List<String> reasons = new ArrayList<>();
        int candidateId = candidate.getCandidateId();
        int jobId = job.getJobId();

        // 1. Candidate active check
        if (!"ACTIVE".equalsIgnoreCase(candidate.getProfileStatus())) {
            reasons.add("Candidate profile is not active (status: " + candidate.getProfileStatus() + ").");
        }

        // 2. Job open status check
        if (!"OPEN".equalsIgnoreCase(job.getStatus())) {
            reasons.add("Job is not open for applications (status: " + job.getStatus() + ").");
        }

        // 3. Application deadline check
        LocalDate deadline = job.getApplicationDeadline();
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            reasons.add("Job application deadline expired on " + deadline + ".");
        }

        // 4. Duplicate application check
        if (candidateId > 0 && jobId > 0 && applicationDAO.findByCandidateAndJob(candidateId, jobId).isPresent()) {
            reasons.add("Candidate has already applied for this job.");
        }

        // 5. Minimum percentage check
        BigDecimal minPercentage = job.getMinPercentage();
        if (minPercentage != null && minPercentage.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal candidatePercentage = candidate.getPercentage();
            if (candidatePercentage == null || candidatePercentage.compareTo(minPercentage) < 0) {
                reasons.add(String.format("Candidate percentage (%s%%) is below job requirement (%s%%).",
                        candidatePercentage != null ? candidatePercentage : "N/A", minPercentage));
            }
        }

        // 6. Passing year check (min_passing_year and max_passing_year)
        int candidatePassingYear = candidate.getPassingYear();
        Integer minYear = job.getMinPassingYear();
        Integer maxYear = job.getMaxPassingYear();
        if (minYear != null && candidatePassingYear < minYear) {
            reasons.add(String.format("Candidate passing year (%d) is earlier than minimum allowed (%d).",
                    candidatePassingYear, minYear));
        }
        if (maxYear != null && candidatePassingYear > maxYear) {
            reasons.add(String.format("Candidate passing year (%d) exceeds maximum allowed (%d).",
                    candidatePassingYear, maxYear));
        }

        // 7. Maximum backlog check
        if (candidate.getBacklogCount() > job.getMaxBacklogs()) {
            reasons.add(String.format("Candidate backlogs (%d) exceed maximum allowed (%d).",
                    candidate.getBacklogCount(), job.getMaxBacklogs()));
        }

        // 8. Qualification rule check
        String qualRule = job.getQualificationRule();
        if (qualRule != null && !qualRule.isBlank() && !qualRule.equalsIgnoreCase("ANY")) {
            String candidateQual = candidate.getQualification() != null ? candidate.getQualification().trim().toUpperCase() : "";
            String[] allowedQuals = qualRule.toUpperCase().split("[,/|]");
            boolean qualMatch = false;
            for (String q : allowedQuals) {
                if (candidateQual.contains(q.trim())) {
                    qualMatch = true;
                    break;
                }
            }
            if (!qualMatch) {
                reasons.add(String.format("Candidate qualification '%s' does not satisfy qualification requirement: %s.",
                        candidate.getQualification(), qualRule));
            }
        }

        // 9. Mandatory skills check
        if (candidateId > 0 && jobId > 0) {
            List<JobSkill> jobSkills = jobSkillDAO.findByJobId(jobId);
            List<JobSkill> mandatorySkills = jobSkills.stream()
                    .filter(JobSkill::isMandatory)
                    .collect(Collectors.toList());

            if (!mandatorySkills.isEmpty()) {
                List<CandidateSkill> candSkills = candidateSkillDAO.findByCandidateId(candidateId);
                Set<Integer> candSkillIds = candSkills.stream()
                        .map(CandidateSkill::getSkillId)
                        .collect(Collectors.toSet());

                for (JobSkill ms : mandatorySkills) {
                    if (!candSkillIds.contains(ms.getSkillId())) {
                        String skillLabel = ms.getSkillName() != null ? ms.getSkillName() : ("Skill ID " + ms.getSkillId());
                        reasons.add("Candidate lacks mandatory required skill: " + skillLabel + ".");
                    }
                }
            }
        }

        boolean eligible = reasons.isEmpty();
        return new EligibilityResult(candidateId, jobId, eligible, reasons);
    }
}

package com.recruitment.service;

import com.recruitment.dao.CompanyDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Company;
import com.recruitment.model.Job;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer for Job management, opening validations, and lifecycle tracking.
 */
public class JobService {

    private final JobDAO jobDAO;
    private final CompanyDAO companyDAO;
    private final ActivityLogService activityLogService;

    public JobService() {
        this.jobDAO = new JobDAO();
        this.companyDAO = new CompanyDAO();
        this.activityLogService = new ActivityLogService();
    }

    public JobService(JobDAO jobDAO, CompanyDAO companyDAO, ActivityLogService activityLogService) {
        this.jobDAO = jobDAO;
        this.companyDAO = companyDAO;
        this.activityLogService = activityLogService;
    }

    public int createJob(Job job) throws ValidationException, SQLException {
        validateJob(job);

        // Auto-generate job code if not supplied
        if (job.getJobCode() == null || job.getJobCode().isBlank()) {
            job.setJobCode("JOB-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 10000));
        }

        // Check company exists and is ACTIVE
        Optional<Company> company = companyDAO.findById(job.getCompanyId());
        if (company.isEmpty()) {
            throw new ValidationException("Error: Associated company ID " + job.getCompanyId() + " does not exist.");
        }
        if (!"ACTIVE".equalsIgnoreCase(company.get().getStatus())) {
            throw new ValidationException("Error: Cannot create job for inactive company: " + company.get().getCompanyName());
        }

        // Check unique job code
        Optional<Job> existingCode = jobDAO.findByCode(job.getJobCode().trim());
        if (existingCode.isPresent()) {
            throw new ValidationException("Error: Job code already exists: " + job.getJobCode());
        }

        int newId = jobDAO.insert(job);
        if (newId > 0) {
            activityLogService.log(job.getCreatedBy() > 0 ? job.getCreatedBy() : 1,
                    "JOB", "CREATE", String.valueOf(newId),
                    "Job opening posted: " + job.getJobTitle() + " (" + job.getJobCode() + ")");
        }
        return newId;
    }

    public boolean updateJob(Job job) throws JobNotFoundException, ValidationException, SQLException {
        if (job == null || job.getJobId() <= 0) {
            throw new ValidationException("Error: Invalid job ID for update.");
        }

        Optional<Job> existing = jobDAO.findById(job.getJobId());
        if (existing.isEmpty()) {
            throw new JobNotFoundException("Job with ID " + job.getJobId() + " not found.");
        }

        validateJob(job);

        // Check unique job code if altered
        Optional<Job> byCode = jobDAO.findByCode(job.getJobCode().trim());
        if (byCode.isPresent() && byCode.get().getJobId() != job.getJobId()) {
            throw new ValidationException("Error: Job code already in use by another job: " + job.getJobCode());
        }

        boolean updated = jobDAO.update(job);
        if (updated) {
            activityLogService.log(1, "JOB", "UPDATE", String.valueOf(job.getJobId()),
                    "Job opening updated: " + job.getJobTitle() + " (" + job.getJobCode() + ")");
        }
        return updated;
    }

    public Job getJobById(int jobId) throws JobNotFoundException, SQLException {
        return jobDAO.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job with ID " + jobId + " not found."));
    }

    public Optional<Job> getJobByCode(String code) throws SQLException {
        return jobDAO.findByCode(code);
    }

    public List<Job> getAllJobs() throws SQLException {
        return jobDAO.findAll();
    }

    public List<Job> getOpenJobs() throws SQLException {
        return jobDAO.findOpenJobs();
    }

    public List<Job> getClosedJobs() throws SQLException {
        return jobDAO.findClosedJobs();
    }

    public List<Job> getJobsByCompany(int companyId) throws SQLException {
        return jobDAO.findByCompanyId(companyId);
    }

    public List<Job> searchJobs(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllJobs();
        }
        return jobDAO.search(keyword);
    }

    public boolean closeJob(int jobId) throws JobNotFoundException, SQLException {
        Optional<Job> existing = jobDAO.findById(jobId);
        if (existing.isEmpty()) {
            throw new JobNotFoundException("Job with ID " + jobId + " not found.");
        }
        boolean ok = jobDAO.closeJob(jobId);
        if (ok) {
            activityLogService.log(1, "JOB", "CLOSE", String.valueOf(jobId),
                    "Job closed: ID " + jobId);
        }
        return ok;
    }

    public void validateJob(Job job) throws ValidationException {
        if (job == null) {
            throw new ValidationException("Error: Job details cannot be null.");
        }
        if (job.getCompanyId() <= 0) {
            throw new ValidationException("Error: Valid company ID is mandatory.");
        }
        if (job.getJobTitle() == null || job.getJobTitle().isBlank()) {
            throw new ValidationException("Error: Job title is mandatory.");
        }
        if (job.getLocation() == null || job.getLocation().isBlank()) {
            throw new ValidationException("Error: Location is mandatory.");
        }
        if (job.getOpenings() <= 0) {
            throw new ValidationException("Error: Openings must be greater than zero.");
        }

        BigDecimal minPkg = job.getMinPackageLpa();
        BigDecimal maxPkg = job.getMaxPackageLpa();
        if (minPkg != null && minPkg.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Error: Minimum package cannot be negative.");
        }
        if (maxPkg != null && maxPkg.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Error: Maximum package cannot be negative.");
        }
        if (minPkg != null && maxPkg != null && minPkg.compareTo(maxPkg) > 0) {
            throw new ValidationException("Error: Minimum package cannot exceed maximum package.");
        }

        BigDecimal minPct = job.getMinPercentage();
        if (minPct != null && (minPct.compareTo(BigDecimal.ZERO) < 0 || minPct.compareTo(new BigDecimal("100")) > 0)) {
            throw new ValidationException("Error: Minimum percentage must be between 0 and 100.");
        }

        Integer minYr = job.getMinPassingYear();
        Integer maxYr = job.getMaxPassingYear();
        if (minYr != null && maxYr != null && minYr > maxYr) {
            throw new ValidationException("Error: Minimum passing year cannot exceed maximum passing year.");
        }

        if (job.getMaxBacklogs() < 0) {
            throw new ValidationException("Error: Maximum backlogs cannot be negative.");
        }

        LocalDate deadline = job.getApplicationDeadline();
        LocalDate posted = job.getPostedDate();
        if (deadline == null) {
            throw new ValidationException("Error: Application deadline is mandatory.");
        }
        if (posted != null && deadline.isBefore(posted)) {
            throw new ValidationException("Error: Application deadline cannot precede posted date.");
        }
    }
}

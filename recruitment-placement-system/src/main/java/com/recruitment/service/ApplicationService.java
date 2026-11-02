package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.ApplicationHistoryDAO;
import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.exception.CandidateNotEligibleException;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.DuplicateApplicationException;
import com.recruitment.exception.InvalidWorkflowTransitionException;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.ApplicationHistory;
import com.recruitment.model.EligibilityResult;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer for Eligibility Checks, Application Submissions,
 * Workflow Lifecycle Transitions, and Database Transaction Management.
 */
public class ApplicationService {

    private final ApplicationDAO applicationDAO;
    private final ApplicationHistoryDAO historyDAO;
    private final CandidateDAO candidateDAO;
    private final JobDAO jobDAO;
    private final EligibilityService eligibilityService;
    private final ActivityLogService activityLogService;

    public ApplicationService() {
        this.applicationDAO = new ApplicationDAO();
        this.historyDAO = new ApplicationHistoryDAO();
        this.candidateDAO = new CandidateDAO();
        this.jobDAO = new JobDAO();
        this.eligibilityService = new EligibilityService();
        this.activityLogService = new ActivityLogService();
    }

    public ApplicationService(ApplicationDAO applicationDAO, ApplicationHistoryDAO historyDAO,
                              CandidateDAO candidateDAO, JobDAO jobDAO,
                              EligibilityService eligibilityService, ActivityLogService activityLogService) {
        this.applicationDAO = applicationDAO;
        this.historyDAO = historyDAO;
        this.candidateDAO = candidateDAO;
        this.jobDAO = jobDAO;
        this.eligibilityService = eligibilityService;
        this.activityLogService = activityLogService;
    }

    /**
     * Checks eligibility using the dedicated EligibilityService.
     */
    public EligibilityResult checkEligibility(int candidateId, int jobId)
            throws CandidateNotFoundException, JobNotFoundException, SQLException {
        return eligibilityService.checkEligibility(candidateId, jobId);
    }

    /**
     * Submits an application within a JDBC transaction after validating full eligibility.
     */
    public int applyForJob(int candidateId, int jobId)
            throws CandidateNotFoundException, JobNotFoundException, DuplicateApplicationException,
            CandidateNotEligibleException, SQLException {

        // 1. Check duplicate application first
        if (applicationDAO.findByCandidateAndJob(candidateId, jobId).isPresent()) {
            throw new DuplicateApplicationException("Candidate " + candidateId + " has already applied for job " + jobId);
        }

        // 2. Evaluate eligibility
        EligibilityResult result = checkEligibility(candidateId, jobId);
        if (!result.isEligible()) {
            throw new CandidateNotEligibleException(result.getReasonString());
        }

        String appCode = "APP-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 100000);

        Application app = new Application();
        app.setApplicationCode(appCode);
        app.setCandidateId(candidateId);
        app.setJobId(jobId);
        app.setAppliedAt(LocalDateTime.now());
        app.setEligibilityStatus("ELIGIBLE");
        app.setEligibilityReason(result.getReasonString());
        app.setApplicationStatus("APPLIED");
        app.setSource("PORTAL");

        // Transaction handling: application + initial history + activity log
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int appId = applicationDAO.insert(conn, app);
                if (appId <= 0) {
                    conn.rollback();
                    throw new SQLException("Failed to create application record.");
                }

                ApplicationHistory history = new ApplicationHistory();
                history.setApplicationId(appId);
                history.setOldStatus(null);
                history.setNewStatus("APPLIED");
                history.setRemarks("Application submitted successfully. Eligibility verified.");
                history.setChangedBy(1);
                historyDAO.insert(conn, history);

                activityLogService.log(conn, 1, "APPLICATION", "CREATE", String.valueOf(appId),
                        "Application submitted: " + appCode + " (Candidate: " + candidateId + ", Job: " + jobId + ")");

                conn.commit();
                return appId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Updates application workflow status within a database transaction, recording history and log.
     */
    public boolean updateStatus(int applicationId, String targetStatus, String remarks, int changedBy)
            throws ValidationException, InvalidWorkflowTransitionException, SQLException {

        Application app = getApplicationById(applicationId);
        String currentStatus = app.getApplicationStatus();

        validateTransition(currentStatus, targetStatus);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean updated = applicationDAO.updateStatus(conn, applicationId, targetStatus);
                if (!updated) {
                    conn.rollback();
                    return false;
                }

                ApplicationHistory history = new ApplicationHistory();
                history.setApplicationId(applicationId);
                history.setOldStatus(currentStatus);
                history.setNewStatus(targetStatus);
                history.setRemarks(remarks != null ? remarks : "Status transitioned to " + targetStatus);
                history.setChangedBy(changedBy > 0 ? changedBy : 1);
                historyDAO.insert(conn, history);

                activityLogService.log(conn, changedBy > 0 ? changedBy : 1, "APPLICATION", "STATUS_CHANGE",
                        String.valueOf(applicationId),
                        "Application #" + applicationId + " (" + app.getApplicationCode() + ") status changed: "
                                + currentStatus + " -> " + targetStatus);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean shortlistCandidate(int applicationId) throws ValidationException, SQLException {
        return updateStatus(applicationId, "SHORTLISTED", "Candidate shortlisted for interview", 1);
    }

    public boolean rejectApplication(int applicationId) throws ValidationException, SQLException {
        return updateStatus(applicationId, "REJECTED", "Application rejected", 1);
    }

    public Application getApplicationById(int applicationId) throws ValidationException, SQLException {
        return applicationDAO.findById(applicationId)
                .orElseThrow(() -> new ValidationException("Error: Application not found with ID: " + applicationId));
    }

    public Optional<Application> getApplicationByCode(String code) throws SQLException {
        return applicationDAO.findByCode(code);
    }

    public List<Application> getAllApplications() throws SQLException {
        return applicationDAO.findAll();
    }

    public List<Application> getApplicationsByCandidate(int candidateId) throws SQLException {
        return applicationDAO.findByCandidateId(candidateId);
    }

    public List<Application> getApplicationsByJob(int jobId) throws SQLException {
        return applicationDAO.findByJobId(jobId);
    }

    public List<Application> getApplicationsByRecruiter(int recruiterId) throws SQLException {
        return applicationDAO.findByRecruiterId(recruiterId);
    }

    public List<Application> getApplicationsByStatus(String status) throws SQLException {
        return applicationDAO.findByStatus(status);
    }

    public List<Application> getApplicationsByEligibility(String eligibility) throws SQLException {
        return applicationDAO.findByEligibility(eligibility);
    }

    public List<Application> searchApplications(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllApplications();
        }
        return applicationDAO.search(keyword);
    }

    public List<ApplicationHistory> getApplicationHistory(int applicationId) throws SQLException {
        return historyDAO.findByApplicationId(applicationId);
    }

    /**
     * Validates transitions between RPMS application lifecycle statuses:
     * APPLIED -> ELIGIBILITY_VERIFIED -> SHORTLISTED -> INTERVIEW_SCHEDULED -> INTERVIEWED -> SELECTED -> OFFERED -> JOINED
     * (REJECTED and WITHDRAWN allowed from non-terminal states)
     */
    public void validateTransition(String currentStatus, String targetStatus)
            throws InvalidWorkflowTransitionException {

        if (currentStatus == null || targetStatus == null) {
            throw new InvalidWorkflowTransitionException("Application status cannot be null.");
        }

        if (currentStatus.equalsIgnoreCase(targetStatus)) {
            return;
        }

        String cur = currentStatus.toUpperCase();
        String tgt = targetStatus.toUpperCase();

        // Terminal statuses cannot transition
        if (cur.equals("JOINED") || cur.equals("REJECTED") || cur.equals("WITHDRAWN")) {
            throw new InvalidWorkflowTransitionException(
                    "Cannot transition from terminal status '" + cur + "' to '" + tgt + "'.");
        }

        // WITHDRAWN and REJECTED are permissible from any non-terminal status
        if (tgt.equals("WITHDRAWN") || tgt.equals("REJECTED")) {
            return;
        }

        boolean allowed = false;

        switch (cur) {
            case "APPLIED":
                allowed = tgt.equals("ELIGIBILITY_VERIFIED") || tgt.equals("SHORTLISTED");
                break;
            case "ELIGIBILITY_VERIFIED":
                allowed = tgt.equals("SHORTLISTED");
                break;
            case "SHORTLISTED":
                allowed = tgt.equals("INTERVIEW_SCHEDULED") || tgt.equals("INTERVIEW");
                break;
            case "INTERVIEW_SCHEDULED":
            case "INTERVIEW":
                allowed = tgt.equals("INTERVIEWED") || tgt.equals("SELECTED");
                break;
            case "INTERVIEWED":
                // Can move to selected or scheduled for next round
                allowed = tgt.equals("SELECTED") || tgt.equals("INTERVIEW_SCHEDULED") || tgt.equals("INTERVIEW");
                break;
            case "SELECTED":
                allowed = tgt.equals("OFFERED");
                break;
            case "OFFERED":
                allowed = tgt.equals("JOINED");
                break;
            default:
                allowed = false;
        }

        if (!allowed) {
            throw new InvalidWorkflowTransitionException(
                    String.format("Invalid recruitment status transition: cannot move directly from '%s' to '%s'.", cur, tgt));
        }
    }
}

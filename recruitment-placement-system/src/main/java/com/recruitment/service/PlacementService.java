package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.ApplicationHistoryDAO;
import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.OfferDAO;
import com.recruitment.dao.PlacementDAO;
import com.recruitment.dao.ReportDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.ApplicationHistory;
import com.recruitment.model.Candidate;
import com.recruitment.model.Offer;
import com.recruitment.model.Placement;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer for Placement management, confirming joining events,
 * and coordinating atomic transaction updates to applications, candidates, and placements.
 */
public class PlacementService {

    private final PlacementDAO placementDAO;
    private final OfferDAO offerDAO;
    private final ApplicationDAO applicationDAO;
    private final ApplicationHistoryDAO historyDAO;
    private final CandidateDAO candidateDAO;
    private final ReportDAO reportDAO;
    private final ActivityLogService activityLogService;

    public PlacementService() {
        this.placementDAO = new PlacementDAO();
        this.offerDAO = new OfferDAO();
        this.applicationDAO = new ApplicationDAO();
        this.historyDAO = new ApplicationHistoryDAO();
        this.candidateDAO = new CandidateDAO();
        this.reportDAO = new ReportDAO();
        this.activityLogService = new ActivityLogService();
    }

    public PlacementService(PlacementDAO placementDAO, OfferDAO offerDAO, ApplicationDAO applicationDAO,
                            ApplicationHistoryDAO historyDAO, CandidateDAO candidateDAO,
                            ReportDAO reportDAO, ActivityLogService activityLogService) {
        this.placementDAO = placementDAO;
        this.offerDAO = offerDAO;
        this.applicationDAO = applicationDAO;
        this.historyDAO = historyDAO;
        this.candidateDAO = candidateDAO;
        this.reportDAO = reportDAO;
        this.activityLogService = activityLogService;
    }

    /**
     * Confirms joining for an accepted offer inside a database transaction:
     * - Inserts placement record
     * - If joining_status is JOINED:
     *     - Updates application status to JOINED
     *     - Updates candidate placement_status to PLACED
     *     - Records application history entry
     * - Records activity log
     * - Commits transaction
     */
    public int confirmJoining(int offerId, LocalDate actualJoiningDate, String joiningStatus)
            throws ValidationException, SQLException {

        if (offerId <= 0) {
            throw new ValidationException("Error: Valid offer ID is required.");
        }

        Optional<Offer> offerOpt = offerDAO.findById(offerId);
        if (offerOpt.isEmpty()) {
            throw new ValidationException("Error: Offer not found with ID: " + offerId);
        }
        Offer offer = offerOpt.get();

        if (!"ACCEPTED".equalsIgnoreCase(offer.getOfferStatus())) {
            throw new ValidationException("Error: Offer must be in 'ACCEPTED' status before joining confirmation. Current offer status: "
                    + offer.getOfferStatus());
        }

        // Check duplicate placement record for offer (unique constraint on offer_id)
        Optional<Placement> existingPlc = placementDAO.findByOfferId(offerId);
        if (existingPlc.isPresent()) {
            throw new ValidationException("Error: Placement record already exists for offer ID: " + offerId);
        }

        Optional<Application> appOpt = applicationDAO.findById(offer.getApplicationId());
        if (appOpt.isEmpty()) {
            throw new ValidationException("Error: Application not found with ID: " + offer.getApplicationId());
        }
        Application app = appOpt.get();

        String status = (joiningStatus != null && !joiningStatus.isBlank()) ? joiningStatus.toUpperCase().trim() : "JOINED";

        Placement p = new Placement();
        p.setOfferId(offerId);
        p.setCandidateId(app.getCandidateId());
        p.setCompanyId(offer.getCompanyId());
        p.setJobId(app.getJobId());
        p.setDesignation(offer.getDesignation());
        p.setFinalPackageLpa(offer.getOfferedPackageLpa());
        p.setExpectedJoiningDate(offer.getExpectedJoiningDate());
        p.setActualJoiningDate(actualJoiningDate != null ? actualJoiningDate : LocalDate.now());
        p.setJoiningStatus(status);
        p.setPlacementStatus("ACTIVE");

        // Transactional placement creation + application status + candidate placement_status
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int placementId = placementDAO.insert(conn, p);
                if (placementId <= 0) {
                    conn.rollback();
                    throw new SQLException("Failed to create placement record.");
                }

                if ("JOINED".equalsIgnoreCase(status)) {
                    // Update application status to JOINED
                    boolean appUpdated = applicationDAO.updateStatus(conn, offer.getApplicationId(), "JOINED");
                    if (!appUpdated) {
                        conn.rollback();
                        throw new SQLException("Failed to update application status to JOINED.");
                    }

                    // Update candidate placement_status to PLACED
                    boolean candUpdated = candidateDAO.updatePlacementStatus(app.getCandidateId(), "PLACED", conn);
                    if (!candUpdated) {
                        conn.rollback();
                        throw new SQLException("Failed to update candidate placement_status to PLACED.");
                    }

                    // Insert application history
                    ApplicationHistory history = new ApplicationHistory();
                    history.setApplicationId(offer.getApplicationId());
                    history.setOldStatus(app.getApplicationStatus());
                    history.setNewStatus("JOINED");
                    history.setRemarks("Candidate joined organization successfully. Final Package: " + offer.getOfferedPackageLpa() + " LPA.");
                    history.setChangedBy(1);
                    historyDAO.insert(conn, history);
                }

                activityLogService.log(conn, 1, "PLACEMENT", "CONFIRM_JOINING", String.valueOf(placementId),
                        "Joining confirmed: Candidate #" + app.getCandidateId() + " at Company #" + offer.getCompanyId()
                                + " with status " + status);

                conn.commit();
                return placementId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean updateJoiningStatus(int placementId, String newStatus, LocalDate actualJoiningDate) throws ValidationException, SQLException {
        Optional<Placement> existing = placementDAO.findById(placementId);
        if (existing.isEmpty()) {
            throw new ValidationException("Error: Placement record not found with ID: " + placementId);
        }
        boolean ok = placementDAO.updateJoiningStatus(placementId, newStatus, actualJoiningDate);
        if (ok) {
            activityLogService.log(1, "PLACEMENT", "STATUS_CHANGE", String.valueOf(placementId),
                    "Placement status updated: PLC" + placementId + " -> " + newStatus);
        }
        return ok;
    }

    public boolean updateJoiningStatus(int placementId, String newStatus) throws ValidationException, SQLException {
        LocalDate date = "JOINED".equalsIgnoreCase(newStatus) ? LocalDate.now() : null;
        return updateJoiningStatus(placementId, newStatus, date);
    }

    public Placement getPlacementById(int placementId) throws ValidationException, SQLException {
        return placementDAO.findById(placementId)
                .orElseThrow(() -> new ValidationException("Error: Placement record not found with ID: " + placementId));
    }

    public List<Placement> getAllPlacements() throws SQLException {
        return placementDAO.findAll();
    }

    public List<Placement> getPlacedCandidates() throws SQLException {
        return placementDAO.findPlacedCandidates();
    }

    public List<Placement> getNotJoinedCandidates() throws SQLException {
        return placementDAO.findNotJoinedCandidates();
    }

    public List<Candidate> getUnplacedCandidates() throws SQLException {
        return reportDAO.getUnplacedCandidates();
    }

    public List<Placement> getPlacementsByCandidate(int candidateId) throws SQLException {
        return placementDAO.findByCandidateId(candidateId);
    }

    public List<Placement> getPlacementsByCompany(int companyId) throws SQLException {
        return placementDAO.findByCompanyId(companyId);
    }

    public List<Placement> getPlacementsByJob(int jobId) throws SQLException {
        return placementDAO.findByJobId(jobId);
    }

    public List<Placement> searchPlacements(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllPlacements();
        }
        return placementDAO.search(keyword);
    }
}

package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.ApplicationHistoryDAO;
import com.recruitment.dao.OfferDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.ApplicationHistory;
import com.recruitment.model.Offer;
import com.recruitment.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer for Job Offer issuance, acceptance, rejection, and atomic transaction updates.
 */
public class OfferService {

    private final OfferDAO offerDAO;
    private final ApplicationDAO applicationDAO;
    private final ApplicationHistoryDAO historyDAO;
    private final ActivityLogService activityLogService;

    public OfferService() {
        this.offerDAO = new OfferDAO();
        this.applicationDAO = new ApplicationDAO();
        this.historyDAO = new ApplicationHistoryDAO();
        this.activityLogService = new ActivityLogService();
    }

    public OfferService(OfferDAO offerDAO, ApplicationDAO applicationDAO,
                        ApplicationHistoryDAO historyDAO, ActivityLogService activityLogService) {
        this.offerDAO = offerDAO;
        this.applicationDAO = applicationDAO;
        this.historyDAO = historyDAO;
        this.activityLogService = activityLogService;
    }

    /**
     * Creates an offer inside a database transaction and advances the application status to OFFERED.
     */
    public int createOffer(Offer offer) throws ValidationException, SQLException {
        validateOffer(offer);

        Optional<Application> appOpt = applicationDAO.findById(offer.getApplicationId());
        if (appOpt.isEmpty()) {
            throw new ValidationException("Error: Application not found with ID: " + offer.getApplicationId());
        }
        Application app = appOpt.get();

        if (!"SELECTED".equalsIgnoreCase(app.getApplicationStatus())) {
            throw new ValidationException("Error: Cannot create offer. Application must be in 'SELECTED' stage, but current status is: "
                    + app.getApplicationStatus());
        }

        // Prevent duplicate active offer for same application
        Optional<Offer> existingOffer = offerDAO.findByApplicationId(offer.getApplicationId());
        if (existingOffer.isPresent()) {
            String st = existingOffer.get().getOfferStatus();
            if ("OFFERED".equalsIgnoreCase(st) || "ACCEPTED".equalsIgnoreCase(st)) {
                throw new ValidationException("Error: An active offer (" + st + ") already exists for application ID: " + offer.getApplicationId());
            }
        }

        if (offer.getOfferCode() == null || offer.getOfferCode().isBlank()) {
            offer.setOfferCode("OFF-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 100000));
        }

        // Perform transactional offer insertion and application status update
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int offerId = offerDAO.insert(conn, offer);
                if (offerId <= 0) {
                    conn.rollback();
                    throw new SQLException("Failed to create offer record.");
                }

                boolean updatedApp = applicationDAO.updateStatus(conn, app.getApplicationId(), "OFFERED");
                if (!updatedApp) {
                    conn.rollback();
                    throw new SQLException("Failed to update application status to OFFERED.");
                }

                ApplicationHistory history = new ApplicationHistory();
                history.setApplicationId(app.getApplicationId());
                history.setOldStatus("SELECTED");
                history.setNewStatus("OFFERED");
                history.setRemarks("Offer letter issued: " + offer.getOfferCode() + " (" + offer.getDesignation() + ", " + offer.getOfferedPackageLpa() + " LPA)");
                history.setChangedBy(1);
                historyDAO.insert(conn, history);

                activityLogService.log(conn, 1, "OFFER", "CREATE", String.valueOf(offerId),
                        "Offer issued: " + offer.getOfferCode() + " for Application #" + app.getApplicationId());

                conn.commit();
                return offerId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean acceptOffer(int offerId) throws ValidationException, SQLException {
        Offer offer = getOfferById(offerId);
        if (!"OFFERED".equalsIgnoreCase(offer.getOfferStatus())) {
            throw new ValidationException("Error: Only offers in 'OFFERED' status can be accepted. Current status is: " + offer.getOfferStatus());
        }

        boolean ok = offerDAO.updateStatus(offerId, "ACCEPTED");
        if (ok) {
            activityLogService.log(1, "OFFER", "ACCEPT", String.valueOf(offerId),
                    "Offer accepted: " + offer.getOfferCode());
        }
        return ok;
    }

    public boolean rejectOffer(int offerId) throws ValidationException, SQLException {
        Offer offer = getOfferById(offerId);
        boolean ok = offerDAO.updateStatus(offerId, "REJECTED");
        if (ok) {
            activityLogService.log(1, "OFFER", "REJECT", String.valueOf(offerId),
                    "Offer rejected: " + offer.getOfferCode());
        }
        return ok;
    }

    public boolean expireOffer(int offerId) throws ValidationException, SQLException {
        Offer offer = getOfferById(offerId);
        boolean ok = offerDAO.updateStatus(offerId, "EXPIRED");
        if (ok) {
            activityLogService.log(1, "OFFER", "EXPIRE", String.valueOf(offerId),
                    "Offer expired: " + offer.getOfferCode());
        }
        return ok;
    }

    public boolean withdrawOffer(int offerId) throws ValidationException, SQLException {
        Offer offer = getOfferById(offerId);
        boolean ok = offerDAO.updateStatus(offerId, "WITHDRAWN");
        if (ok) {
            activityLogService.log(1, "OFFER", "WITHDRAW", String.valueOf(offerId),
                    "Offer withdrawn: " + offer.getOfferCode());
        }
        return ok;
    }

    public Offer getOfferById(int offerId) throws ValidationException, SQLException {
        return offerDAO.findById(offerId)
                .orElseThrow(() -> new ValidationException("Error: Offer not found with ID: " + offerId));
    }

    public Optional<Offer> getOfferByCode(String code) throws SQLException {
        return offerDAO.findByCode(code);
    }

    public List<Offer> getAllOffers() throws SQLException {
        return offerDAO.findAll();
    }

    public List<Offer> searchOffers(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllOffers();
        }
        return offerDAO.search(keyword);
    }

    public List<Offer> getOffersByCandidate(int candidateId) throws SQLException {
        return offerDAO.findByCandidateId(candidateId);
    }

    public List<Offer> getOffersByCompany(int companyId) throws SQLException {
        return offerDAO.findByCompanyId(companyId);
    }

    public void validateOffer(Offer offer) throws ValidationException {
        if (offer == null) {
            throw new ValidationException("Error: Offer cannot be null.");
        }
        if (offer.getApplicationId() <= 0) {
            throw new ValidationException("Error: Valid application ID is mandatory.");
        }
        if (offer.getDesignation() == null || offer.getDesignation().isBlank()) {
            throw new ValidationException("Error: Designation is mandatory.");
        }
        BigDecimal pkg = offer.getOfferedPackageLpa();
        if (pkg == null || pkg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Error: Offered package must be greater than zero.");
        }
        if (offer.getOfferDate() == null) {
            throw new ValidationException("Error: Offer date is mandatory.");
        }
        LocalDate joining = offer.getExpectedJoiningDate();
        if (joining != null && joining.isBefore(offer.getOfferDate())) {
            throw new ValidationException("Error: Expected joining date cannot be before offer date.");
        }
    }
}

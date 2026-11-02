package com.recruitment.service;

import com.recruitment.dao.CandidateDAO;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Candidate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service Layer for Candidate management, business validations, and audit logging.
 */
public class CandidateService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    private final CandidateDAO candidateDAO;
    private final ActivityLogService activityLogService;

    public CandidateService() {
        this.candidateDAO = new CandidateDAO();
        this.activityLogService = new ActivityLogService();
    }

    public CandidateService(CandidateDAO candidateDAO, ActivityLogService activityLogService) {
        this.candidateDAO = candidateDAO;
        this.activityLogService = activityLogService;
    }

    /**
     * Registers a new candidate after enforcing data integrity and unique constraints.
     */
    public int registerCandidate(Candidate candidate) throws ValidationException, SQLException {
        validateCandidate(candidate);

        // Auto-generate candidate_code if not supplied
        if (candidate.getCandidateCode() == null || candidate.getCandidateCode().isBlank()) {
            candidate.setCandidateCode("CAN-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 100000));
        }

        // Check for duplicate candidate_code
        if (candidateDAO.findByCandidateCode(candidate.getCandidateCode().trim()).isPresent()) {
            throw new ValidationException("Error: Candidate code already exists: " + candidate.getCandidateCode());
        }

        // Check for duplicate email
        Optional<Candidate> existingEmail = candidateDAO.findByEmail(candidate.getEmail().trim());
        if (existingEmail.isPresent()) {
            throw new ValidationException("Error: Email address already exists: " + candidate.getEmail());
        }

        // Check for duplicate mobile
        Optional<Candidate> existingMobile = candidateDAO.findByMobile(candidate.getMobile().trim());
        if (existingMobile.isPresent()) {
            throw new ValidationException("Error: Mobile number already exists: " + candidate.getMobile());
        }

        int newId = candidateDAO.insert(candidate);
        if (newId > 0) {
            activityLogService.log(1, "CANDIDATE", "CREATE", String.valueOf(newId),
                    "Candidate registered: " + candidate.getFullName() + " (" + candidate.getCandidateCode() + ")");
        }
        return newId;
    }

    /**
     * Updates an existing candidate profile.
     */
    public boolean updateCandidate(Candidate candidate) throws CandidateNotFoundException, ValidationException, SQLException {
        if (candidate == null || candidate.getCandidateId() <= 0) {
            throw new ValidationException("Error: Invalid candidate ID for update.");
        }

        Optional<Candidate> existing = candidateDAO.findById(candidate.getCandidateId());
        if (existing.isEmpty()) {
            throw new CandidateNotFoundException("Candidate with ID " + candidate.getCandidateId() + " not found.");
        }

        validateCandidate(candidate);

        // Check duplicate email if modified
        Optional<Candidate> byEmail = candidateDAO.findByEmail(candidate.getEmail().trim());
        if (byEmail.isPresent() && byEmail.get().getCandidateId() != candidate.getCandidateId()) {
            throw new ValidationException("Error: Email address already belongs to another candidate: " + candidate.getEmail());
        }

        // Check duplicate mobile if modified
        Optional<Candidate> byMobile = candidateDAO.findByMobile(candidate.getMobile().trim());
        if (byMobile.isPresent() && byMobile.get().getCandidateId() != candidate.getCandidateId()) {
            throw new ValidationException("Error: Mobile number already belongs to another candidate: " + candidate.getMobile());
        }

        boolean updated = candidateDAO.update(candidate);
        if (updated) {
            activityLogService.log(1, "CANDIDATE", "UPDATE", String.valueOf(candidate.getCandidateId()),
                    "Candidate profile updated: " + candidate.getFullName());
        }
        return updated;
    }

    /**
     * Finds a candidate by ID or throws CandidateNotFoundException.
     */
    public Candidate getCandidateById(int candidateId) throws CandidateNotFoundException, SQLException {
        return candidateDAO.findById(candidateId)
                .orElseThrow(() -> new CandidateNotFoundException("Candidate with ID " + candidateId + " not found."));
    }

    public Optional<Candidate> getCandidateByCode(String candidateCode) throws SQLException {
        return candidateDAO.findByCandidateCode(candidateCode);
    }

    public Optional<Candidate> getCandidateByEmail(String email) throws SQLException {
        return candidateDAO.findByEmail(email);
    }

    public Optional<Candidate> getCandidateByMobile(String mobile) throws SQLException {
        return candidateDAO.findByMobile(mobile);
    }

    public List<Candidate> getAllCandidates() throws SQLException {
        return candidateDAO.findAll();
    }

    public List<Candidate> getActiveCandidates() throws SQLException {
        return candidateDAO.findActive();
    }

    public List<Candidate> searchCandidates(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllCandidates();
        }
        return candidateDAO.search(keyword);
    }

    public List<Candidate> getCandidatesByTechnology(String tech) throws SQLException {
        return candidateDAO.findByTechnology(tech);
    }

    public List<Candidate> getCandidatesByPassingYear(int year) throws SQLException {
        return candidateDAO.findByPassingYear(year);
    }

    public List<Candidate> getCandidatesByQualification(String qual) throws SQLException {
        return candidateDAO.findByQualification(qual);
    }

    public boolean deactivateCandidate(int candidateId) throws CandidateNotFoundException, SQLException {
        Optional<Candidate> candidate = candidateDAO.findById(candidateId);
        if (candidate.isEmpty()) {
            throw new CandidateNotFoundException("Candidate with ID " + candidateId + " not found.");
        }
        boolean deactivated = candidateDAO.updateProfileStatus(candidateId, "INACTIVE");
        if (deactivated) {
            activityLogService.log(1, "CANDIDATE", "DEACTIVATE", String.valueOf(candidateId),
                    "Candidate deactivated: ID " + candidateId);
        }
        return deactivated;
    }

    public void validateCandidate(Candidate candidate) throws ValidationException {
        if (candidate == null) {
            throw new ValidationException("Error: Candidate details cannot be null.");
        }
        if (candidate.getFullName() == null || candidate.getFullName().isBlank()) {
            throw new ValidationException("Error: Full name is mandatory.");
        }
        if (candidate.getEmail() == null || !EMAIL_PATTERN.matcher(candidate.getEmail().trim()).matches()) {
            throw new ValidationException("Error: Invalid email address format.");
        }
        if (candidate.getMobile() == null || !PHONE_PATTERN.matcher(candidate.getMobile().trim().replaceAll("[^0-9]", "")).matches()) {
            throw new ValidationException("Error: Invalid mobile number format. Must contain 10 to 15 digits.");
        }
        if (candidate.getQualification() == null || candidate.getQualification().isBlank()) {
            throw new ValidationException("Error: Qualification is mandatory.");
        }
        if (candidate.getPassingYear() < 1990 || candidate.getPassingYear() > 2040) {
            throw new ValidationException("Error: Passing year must be between 1990 and 2040.");
        }
        BigDecimal pct = candidate.getPercentage();
        if (pct != null && (pct.compareTo(BigDecimal.ZERO) < 0 || pct.compareTo(new BigDecimal("100")) > 0)) {
            throw new ValidationException("Error: Percentage must be between 0 and 100.");
        }
        if (candidate.getBacklogCount() < 0) {
            throw new ValidationException("Error: Backlog count cannot be negative.");
        }
    }
}

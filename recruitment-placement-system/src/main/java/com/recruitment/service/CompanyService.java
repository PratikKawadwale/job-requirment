package com.recruitment.service;

import com.recruitment.dao.CompanyDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Company;
import com.recruitment.model.Job;

import java.sql.SQLException;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service Layer for Company management, validations, and logging.
 */
public class CompanyService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final CompanyDAO companyDAO;
    private final JobDAO jobDAO;
    private final ActivityLogService activityLogService;

    public CompanyService() {
        this.companyDAO = new CompanyDAO();
        this.jobDAO = new JobDAO();
        this.activityLogService = new ActivityLogService();
    }

    public CompanyService(CompanyDAO companyDAO, JobDAO jobDAO, ActivityLogService activityLogService) {
        this.companyDAO = companyDAO;
        this.jobDAO = jobDAO;
        this.activityLogService = activityLogService;
    }

    public int addCompany(Company company) throws ValidationException, SQLException {
        validateCompany(company);

        // Auto-generate company_code if not supplied
        if (company.getCompanyCode() == null || company.getCompanyCode().isBlank()) {
            company.setCompanyCode("COM-" + Year.now().getValue() + "-" + (System.currentTimeMillis() % 10000));
        }

        Optional<Company> existingCode = companyDAO.findByCompanyCode(company.getCompanyCode().trim());
        if (existingCode.isPresent()) {
            throw new ValidationException("Error: Company code '" + company.getCompanyCode() + "' already exists.");
        }

        Optional<Company> existing = companyDAO.findByName(company.getCompanyName().trim());
        if (existing.isPresent()) {
            throw new ValidationException("Error: Company with name '" + company.getCompanyName() + "' already exists.");
        }

        int newId = companyDAO.insert(company);
        if (newId > 0) {
            activityLogService.log(1, "COMPANY", "CREATE", String.valueOf(newId),
                    "Company created: " + company.getCompanyName() + " (" + company.getCompanyCode() + ")");
        }
        return newId;
    }

    public boolean updateCompany(Company company) throws ValidationException, SQLException {
        if (company == null || company.getCompanyId() <= 0) {
            throw new ValidationException("Error: Invalid company ID for update.");
        }

        Optional<Company> existing = companyDAO.findById(company.getCompanyId());
        if (existing.isEmpty()) {
            throw new ValidationException("Error: Company not found with ID: " + company.getCompanyId());
        }

        validateCompany(company);

        Optional<Company> byName = companyDAO.findByName(company.getCompanyName().trim());
        if (byName.isPresent() && byName.get().getCompanyId() != company.getCompanyId()) {
            throw new ValidationException("Error: Another company already uses the name: " + company.getCompanyName());
        }

        boolean updated = companyDAO.update(company);
        if (updated) {
            activityLogService.log(1, "COMPANY", "UPDATE", String.valueOf(company.getCompanyId()),
                    "Company updated: " + company.getCompanyName());
        }
        return updated;
    }

    public Company getCompanyById(int companyId) throws ValidationException, SQLException {
        return companyDAO.findById(companyId)
                .orElseThrow(() -> new ValidationException("Error: Company not found with ID: " + companyId));
    }

    public Optional<Company> getCompanyByCode(String code) throws SQLException {
        return companyDAO.findByCompanyCode(code);
    }

    public Optional<Company> getCompanyByName(String name) throws SQLException {
        return companyDAO.findByName(name);
    }

    public List<Company> getAllCompanies() throws SQLException {
        return companyDAO.findAll();
    }

    public List<Company> getActiveCompanies() throws SQLException {
        return companyDAO.findActive();
    }

    public List<Company> searchCompanies(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllCompanies();
        }
        return companyDAO.search(keyword);
    }

    public List<Job> getJobsByCompany(int companyId) throws SQLException {
        return jobDAO.findByCompanyId(companyId);
    }

    public boolean deactivateCompany(int companyId) throws ValidationException, SQLException {
        return setCompanyStatus(companyId, "INACTIVE");
    }

    public boolean setCompanyStatus(int companyId, String status) throws ValidationException, SQLException {
        Optional<Company> existing = companyDAO.findById(companyId);
        if (existing.isEmpty()) {
            throw new ValidationException("Error: Company not found with ID: " + companyId);
        }
        boolean ok = companyDAO.updateStatus(companyId, status);
        if (ok) {
            activityLogService.log(1, "COMPANY", "STATUS_CHANGE", String.valueOf(companyId),
                    "Company status changed: COM" + companyId + " -> " + status);
        }
        return ok;
    }

    public void validateCompany(Company company) throws ValidationException {
        if (company == null) {
            throw new ValidationException("Error: Company cannot be null.");
        }
        if (company.getCompanyName() == null || company.getCompanyName().isBlank()) {
            throw new ValidationException("Error: Company name is mandatory.");
        }
        if (company.getLocation() == null || company.getLocation().isBlank()) {
            throw new ValidationException("Error: Location is mandatory.");
        }
        if (company.getHrEmail() != null && !company.getHrEmail().isBlank() && !EMAIL_PATTERN.matcher(company.getHrEmail().trim()).matches()) {
            throw new ValidationException("Error: Valid HR email address is required.");
        }
    }
}

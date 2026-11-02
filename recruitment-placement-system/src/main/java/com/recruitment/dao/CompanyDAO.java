package com.recruitment.dao;

import com.recruitment.model.Company;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for companies table in rpms_db.
 */
public class CompanyDAO {

    public int insert(Company company) throws SQLException {
        String sql = "INSERT INTO companies (company_code, company_name, industry, website, location, " +
                "company_type, hr_name, hr_email, hr_mobile, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, company.getCompanyCode());
            stmt.setString(2, company.getCompanyName());
            stmt.setString(3, company.getIndustry());
            stmt.setString(4, company.getWebsite());
            stmt.setString(5, company.getLocation());
            stmt.setString(6, company.getCompanyType() != null ? company.getCompanyType() : "MNC");
            stmt.setString(7, company.getHrName());
            stmt.setString(8, company.getHrEmail());
            stmt.setString(9, company.getHrMobile());
            stmt.setString(10, company.getStatus() != null ? company.getStatus() : "ACTIVE");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        company.setCompanyId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean update(Company company) throws SQLException {
        String sql = "UPDATE companies SET company_code = ?, company_name = ?, industry = ?, website = ?, " +
                "location = ?, company_type = ?, hr_name = ?, hr_email = ?, hr_mobile = ?, status = ? " +
                "WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getCompanyCode());
            stmt.setString(2, company.getCompanyName());
            stmt.setString(3, company.getIndustry());
            stmt.setString(4, company.getWebsite());
            stmt.setString(5, company.getLocation());
            stmt.setString(6, company.getCompanyType());
            stmt.setString(7, company.getHrName());
            stmt.setString(8, company.getHrEmail());
            stmt.setString(9, company.getHrMobile());
            stmt.setString(10, company.getStatus());
            stmt.setInt(11, company.getCompanyId());

            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Company> findById(int companyId) throws SQLException {
        String sql = "SELECT * FROM companies WHERE company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Company> findByCompanyCode(String companyCode) throws SQLException {
        String sql = "SELECT * FROM companies WHERE company_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Company> findByName(String companyName) throws SQLException {
        String sql = "SELECT * FROM companies WHERE company_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCompany(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Company> findAll() throws SQLException {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies ORDER BY company_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCompany(rs));
            }
        }
        return list;
    }

    public List<Company> findActive() throws SQLException {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE status = 'ACTIVE' ORDER BY company_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCompany(rs));
            }
        }
        return list;
    }

    public List<Company> search(String keyword) throws SQLException {
        List<Company> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM companies WHERE company_code LIKE ? OR company_name LIKE ? " +
                "OR industry LIKE ? OR location LIKE ? OR hr_name LIKE ? ORDER BY company_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCompany(rs));
                }
            }
        }
        return list;
    }

    public boolean updateStatus(int companyId, String status) throws SQLException {
        String sql = "UPDATE companies SET status = ? WHERE company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, companyId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setCompanyId(rs.getInt("company_id"));
        c.setCompanyCode(rs.getString("company_code"));
        c.setCompanyName(rs.getString("company_name"));
        c.setIndustry(rs.getString("industry"));
        c.setWebsite(rs.getString("website"));
        c.setLocation(rs.getString("location"));
        c.setCompanyType(rs.getString("company_type"));
        c.setHrName(rs.getString("hr_name"));
        c.setHrEmail(rs.getString("hr_email"));
        c.setHrMobile(rs.getString("hr_mobile"));
        c.setStatus(rs.getString("status"));
        if (rs.getTimestamp("created_at") != null) {
            c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return c;
    }
}

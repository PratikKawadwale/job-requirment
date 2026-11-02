package com.recruitment.dao;

import com.recruitment.model.Application;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for applications table in rpms_db.
 */
public class ApplicationDAO {

    public int insert(Application app) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, app);
        }
    }

    public int insert(Connection conn, Application app) throws SQLException {
        String sql = "INSERT INTO applications (application_code, candidate_id, job_id, applied_at, " +
                "eligibility_status, eligibility_reason, application_status, source, assigned_recruiter_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, app.getApplicationCode());
            stmt.setInt(2, app.getCandidateId());
            stmt.setInt(3, app.getJobId());
            stmt.setTimestamp(4, app.getAppliedAt() != null ? Timestamp.valueOf(app.getAppliedAt()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(5, app.getEligibilityStatus() != null ? app.getEligibilityStatus() : "PENDING");
            stmt.setString(6, app.getEligibilityReason());
            stmt.setString(7, app.getApplicationStatus() != null ? app.getApplicationStatus() : "APPLIED");
            stmt.setString(8, app.getSource() != null ? app.getSource() : "PORTAL");

            if (app.getAssignedRecruiterId() != null) {
                stmt.setInt(9, app.getAssignedRecruiterId());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        app.setApplicationId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean updateStatus(int applicationId, String newStatus) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return updateStatus(conn, applicationId, newStatus);
        }
    }

    public boolean updateStatus(Connection conn, int applicationId, String newStatus) throws SQLException {
        String sql = "UPDATE applications SET application_status = ? WHERE application_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, applicationId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateEligibility(Connection conn, int applicationId, String eligibilityStatus, String reason) throws SQLException {
        String sql = "UPDATE applications SET eligibility_status = ?, eligibility_reason = ? WHERE application_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eligibilityStatus);
            stmt.setString(2, reason);
            stmt.setInt(3, applicationId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean assignRecruiter(int applicationId, int recruiterId) throws SQLException {
        String sql = "UPDATE applications SET assigned_recruiter_id = ? WHERE application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recruiterId);
            stmt.setInt(2, applicationId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Application> findById(int applicationId) throws SQLException {
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.application_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToApplication(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Application> findByCode(String applicationCode) throws SQLException {
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.application_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, applicationCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToApplication(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Application> findByCandidateAndJob(int candidateId, int jobId) throws SQLException {
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.candidate_id = ? AND a.job_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            stmt.setInt(2, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToApplication(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Application> findAll() throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
        }
        return list;
    }

    public List<Application> findByCandidateId(int candidateId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.candidate_id = ? ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findByJobId(int jobId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.job_id = ? ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findByRecruiterId(int recruiterId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.assigned_recruiter_id = ? ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recruiterId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findByStatus(String status) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.application_status = ? ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findByEligibility(String eligibilityStatus) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.eligibility_status = ? ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, eligibilityStatus);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    public List<Application> search(String keyword) throws SQLException {
        List<Application> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT a.*, c.candidate_code, c.full_name AS candidate_name, " +
                "j.job_code, j.job_title, comp.company_name, u.full_name AS recruiter_name " +
                "FROM applications a " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN users u ON a.assigned_recruiter_id = u.user_id " +
                "WHERE a.application_code LIKE ? OR c.full_name LIKE ? OR j.job_title LIKE ? " +
                "OR comp.company_name LIKE ? OR a.application_status LIKE ? ORDER BY a.application_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    public Application mapResultSetToApplication(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setApplicationId(rs.getInt("application_id"));
        a.setApplicationCode(rs.getString("application_code"));
        a.setCandidateId(rs.getInt("candidate_id"));
        a.setJobId(rs.getInt("job_id"));

        try {
            a.setCandidateCode(rs.getString("candidate_code"));
            a.setCandidateName(rs.getString("candidate_name"));
            a.setJobCode(rs.getString("job_code"));
            a.setJobTitle(rs.getString("job_title"));
            a.setCompanyName(rs.getString("company_name"));
            a.setAssignedRecruiterName(rs.getString("recruiter_name"));
        } catch (SQLException ignored) {
        }

        Timestamp appliedAt = rs.getTimestamp("applied_at");
        if (appliedAt != null) {
            a.setAppliedAt(appliedAt.toLocalDateTime());
        }

        a.setEligibilityStatus(rs.getString("eligibility_status"));
        a.setEligibilityReason(rs.getString("eligibility_reason"));
        a.setApplicationStatus(rs.getString("application_status"));
        a.setSource(rs.getString("source"));

        int recruiterId = rs.getInt("assigned_recruiter_id");
        if (!rs.wasNull()) {
            a.setAssignedRecruiterId(recruiterId);
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            a.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return a;
    }
}

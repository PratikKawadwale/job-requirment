package com.recruitment.dao;

import com.recruitment.model.Job;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for jobs table in rpms_db.
 */
public class JobDAO {

    public int insert(Job job) throws SQLException {
        String sql = "INSERT INTO jobs (job_code, company_id, job_title, employment_type, location, openings, " +
                "min_package_lpa, max_package_lpa, min_percentage, min_passing_year, max_passing_year, " +
                "max_backlogs, experience_min_years, qualification_rule, posted_date, application_deadline, " +
                "status, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParameters(stmt, job);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        job.setJobId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean update(Job job) throws SQLException {
        String sql = "UPDATE jobs SET job_code = ?, company_id = ?, job_title = ?, employment_type = ?, " +
                "location = ?, openings = ?, min_package_lpa = ?, max_package_lpa = ?, min_percentage = ?, " +
                "min_passing_year = ?, max_passing_year = ?, max_backlogs = ?, experience_min_years = ?, " +
                "qualification_rule = ?, posted_date = ?, application_deadline = ?, status = ?, created_by = ? " +
                "WHERE job_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParameters(stmt, job);
            stmt.setInt(19, job.getJobId());
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Job> findById(int jobId) throws SQLException {
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id WHERE j.job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJob(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Job> findByCode(String jobCode) throws SQLException {
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id WHERE j.job_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jobCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJob(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Job> findAll() throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id ORDER BY j.job_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToJob(rs));
            }
        }
        return list;
    }

    public List<Job> findOpenJobs() throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE j.status = 'OPEN' AND j.application_deadline >= CURDATE() ORDER BY j.application_deadline ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToJob(rs));
            }
        }
        return list;
    }

    public List<Job> findClosedJobs() throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE j.status = 'CLOSED' OR j.application_deadline < CURDATE() ORDER BY j.job_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToJob(rs));
            }
        }
        return list;
    }

    public List<Job> findByCompanyId(int companyId) throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id WHERE j.company_id = ? ORDER BY j.job_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJob(rs));
                }
            }
        }
        return list;
    }

    public List<Job> search(String keyword) throws SQLException {
        List<Job> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE j.job_code LIKE ? OR j.job_title LIKE ? " +
                "OR c.company_name LIKE ? OR j.location LIKE ? ORDER BY j.job_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJob(rs));
                }
            }
        }
        return list;
    }

    public boolean updateStatus(int jobId, String status) throws SQLException {
        String sql = "UPDATE jobs SET status = ? WHERE job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, jobId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean closeJob(int jobId) throws SQLException {
        return updateStatus(jobId, "CLOSED");
    }

    private void setStatementParameters(PreparedStatement stmt, Job job) throws SQLException {
        stmt.setString(1, job.getJobCode());
        stmt.setInt(2, job.getCompanyId());
        stmt.setString(3, job.getJobTitle());
        stmt.setString(4, job.getEmploymentType() != null ? job.getEmploymentType() : "FULL_TIME");
        stmt.setString(5, job.getLocation());
        stmt.setInt(6, job.getOpenings());

        if (job.getMinPackageLpa() != null) {
            stmt.setBigDecimal(7, job.getMinPackageLpa());
        } else {
            stmt.setNull(7, Types.DECIMAL);
        }

        if (job.getMaxPackageLpa() != null) {
            stmt.setBigDecimal(8, job.getMaxPackageLpa());
        } else {
            stmt.setNull(8, Types.DECIMAL);
        }

        if (job.getMinPercentage() != null) {
            stmt.setBigDecimal(9, job.getMinPercentage());
        } else {
            stmt.setNull(9, Types.DECIMAL);
        }

        if (job.getMinPassingYear() != null) {
            stmt.setInt(10, job.getMinPassingYear());
        } else {
            stmt.setNull(10, Types.SMALLINT);
        }

        if (job.getMaxPassingYear() != null) {
            stmt.setInt(11, job.getMaxPassingYear());
        } else {
            stmt.setNull(11, Types.SMALLINT);
        }

        stmt.setInt(12, job.getMaxBacklogs());
        stmt.setBigDecimal(13, job.getExperienceMinYears() != null ? job.getExperienceMinYears() : java.math.BigDecimal.ZERO);
        stmt.setString(14, job.getQualificationRule());
        stmt.setDate(15, job.getPostedDate() != null ? Date.valueOf(job.getPostedDate()) : Date.valueOf(java.time.LocalDate.now()));
        stmt.setDate(16, job.getApplicationDeadline() != null ? Date.valueOf(job.getApplicationDeadline()) : Date.valueOf(java.time.LocalDate.now().plusMonths(1)));
        stmt.setString(17, job.getStatus() != null ? job.getStatus() : "OPEN");
        stmt.setInt(18, job.getCreatedBy() > 0 ? job.getCreatedBy() : 1);
    }

    public Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setJobId(rs.getInt("job_id"));
        j.setJobCode(rs.getString("job_code"));
        j.setCompanyId(rs.getInt("company_id"));
        try {
            j.setCompanyName(rs.getString("company_name"));
        } catch (SQLException ignored) {
        }
        j.setJobTitle(rs.getString("job_title"));
        j.setEmploymentType(rs.getString("employment_type"));
        j.setLocation(rs.getString("location"));
        j.setOpenings(rs.getInt("openings"));
        j.setMinPackageLpa(rs.getBigDecimal("min_package_lpa"));
        j.setMaxPackageLpa(rs.getBigDecimal("max_package_lpa"));
        j.setMinPercentage(rs.getBigDecimal("min_percentage"));

        int minPy = rs.getInt("min_passing_year");
        if (!rs.wasNull()) j.setMinPassingYear(minPy);

        int maxPy = rs.getInt("max_passing_year");
        if (!rs.wasNull()) j.setMaxPassingYear(maxPy);

        j.setMaxBacklogs(rs.getInt("max_backlogs"));
        j.setExperienceMinYears(rs.getBigDecimal("experience_min_years"));
        j.setQualificationRule(rs.getString("qualification_rule"));

        Date posted = rs.getDate("posted_date");
        if (posted != null) j.setPostedDate(posted.toLocalDate());

        Date deadline = rs.getDate("application_deadline");
        if (deadline != null) j.setApplicationDeadline(deadline.toLocalDate());

        j.setStatus(rs.getString("status"));
        j.setCreatedBy(rs.getInt("created_by"));

        if (rs.getTimestamp("created_at") != null) {
            j.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return j;
    }
}

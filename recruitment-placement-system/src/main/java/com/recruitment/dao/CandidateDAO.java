package com.recruitment.dao;

import com.recruitment.model.Candidate;
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
 * Data Access Object for candidates table in rpms_db.
 */
public class CandidateDAO {

    /**
     * Inserts a new candidate record into rpms_db.candidates.
     */
    public int insert(Candidate candidate) throws SQLException {
        String sql = "INSERT INTO candidates (candidate_code, full_name, email, mobile, date_of_birth, gender, " +
                "qualification, college, university, passing_year, percentage, cgpa, backlog_count, " +
                "primary_technology, current_location, preferred_location, profile_status, placement_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParameters(stmt, candidate);
            stmt.setString(17, candidate.getProfileStatus() != null ? candidate.getProfileStatus() : "ACTIVE");
            stmt.setString(18, candidate.getPlacementStatus() != null ? candidate.getPlacementStatus() : "NOT_PLACED");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        candidate.setCandidateId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Updates an existing candidate's profile information.
     */
    public boolean update(Candidate candidate) throws SQLException {
        String sql = "UPDATE candidates SET candidate_code = ?, full_name = ?, email = ?, mobile = ?, " +
                "date_of_birth = ?, gender = ?, qualification = ?, college = ?, university = ?, passing_year = ?, " +
                "percentage = ?, cgpa = ?, backlog_count = ?, primary_technology = ?, current_location = ?, " +
                "preferred_location = ?, profile_status = ?, placement_status = ? " +
                "WHERE candidate_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParameters(stmt, candidate);
            stmt.setString(17, candidate.getProfileStatus() != null ? candidate.getProfileStatus() : "ACTIVE");
            stmt.setString(18, candidate.getPlacementStatus() != null ? candidate.getPlacementStatus() : "NOT_PLACED");
            stmt.setInt(19, candidate.getCandidateId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Finds a candidate by primary key ID.
     */
    public Optional<Candidate> findById(int candidateId) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE candidate_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCandidate(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a candidate by candidate code.
     */
    public Optional<Candidate> findByCandidateCode(String candidateCode) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE candidate_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, candidateCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCandidate(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a candidate by email (unique constraint check).
     */
    public Optional<Candidate> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCandidate(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a candidate by mobile (unique constraint check).
     */
    public Optional<Candidate> findByMobile(String mobile) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE mobile = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mobile);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCandidate(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all candidates ordered by candidate_id.
     */
    public List<Candidate> findAll() throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY candidate_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCandidate(rs));
            }
        }
        return list;
    }

    /**
     * Returns all active candidates (profile_status = 'ACTIVE').
     */
    public List<Candidate> findActive() throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE profile_status = 'ACTIVE' ORDER BY candidate_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToCandidate(rs));
            }
        }
        return list;
    }

    /**
     * Searches candidates by keyword in name, email, mobile, qualification, or primary technology.
     */
    public List<Candidate> search(String keyword) throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = "SELECT * FROM candidates WHERE full_name LIKE ? OR email LIKE ? " +
                "OR mobile LIKE ? OR primary_technology LIKE ? OR college LIKE ? OR qualification LIKE ? " +
                "OR candidate_code LIKE ? ORDER BY candidate_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);
            stmt.setString(6, pattern);
            stmt.setString(7, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCandidate(rs));
                }
            }
        }
        return list;
    }

    /**
     * Searches candidates by primary technology.
     */
    public List<Candidate> findByTechnology(String technology) throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE primary_technology LIKE ? ORDER BY candidate_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + technology.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCandidate(rs));
                }
            }
        }
        return list;
    }

    /**
     * Searches candidates by passing year.
     */
    public List<Candidate> findByPassingYear(int passingYear) throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE passing_year = ? ORDER BY candidate_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, passingYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCandidate(rs));
                }
            }
        }
        return list;
    }

    /**
     * Searches candidates by qualification.
     */
    public List<Candidate> findByQualification(String qualification) throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE qualification LIKE ? ORDER BY candidate_id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + qualification.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCandidate(rs));
                }
            }
        }
        return list;
    }

    /**
     * Updates profile status of candidate (e.g. INACTIVE, ON_HOLD, BLACKLISTED).
     */
    public boolean updateProfileStatus(int candidateId, String profileStatus) throws SQLException {
        String sql = "UPDATE candidates SET profile_status = ? WHERE candidate_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profileStatus);
            stmt.setInt(2, candidateId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates placement status of candidate (NOT_PLACED, PLACED).
     */
    public boolean updatePlacementStatus(int candidateId, String placementStatus, Connection conn) throws SQLException {
        String sql = "UPDATE candidates SET placement_status = ? WHERE candidate_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placementStatus);
            stmt.setInt(2, candidateId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int candidateId, String status) throws SQLException {
        return updateProfileStatus(candidateId, status);
    }

    private void setStatementParameters(PreparedStatement stmt, Candidate candidate) throws SQLException {
        stmt.setString(1, candidate.getCandidateCode());
        stmt.setString(2, candidate.getFullName());
        stmt.setString(3, candidate.getEmail());
        stmt.setString(4, candidate.getMobile());
        stmt.setDate(5, candidate.getDateOfBirth() != null ? Date.valueOf(candidate.getDateOfBirth()) : null);
        stmt.setString(6, candidate.getGender());
        stmt.setString(7, candidate.getQualification());
        stmt.setString(8, candidate.getCollege());
        stmt.setString(9, candidate.getUniversity());
        stmt.setInt(10, candidate.getPassingYear());
        if (candidate.getPercentage() != null) {
            stmt.setBigDecimal(11, candidate.getPercentage());
        } else {
            stmt.setNull(11, Types.DECIMAL);
        }
        if (candidate.getCgpa() != null) {
            stmt.setBigDecimal(12, candidate.getCgpa());
        } else {
            stmt.setNull(12, Types.DECIMAL);
        }
        stmt.setInt(13, candidate.getBacklogCount());
        stmt.setString(14, candidate.getPrimaryTechnology());
        stmt.setString(15, candidate.getCurrentLocation());
        stmt.setString(16, candidate.getPreferredLocation());
    }

    public Candidate mapResultSetToCandidate(ResultSet rs) throws SQLException {
        Candidate c = new Candidate();
        c.setCandidateId(rs.getInt("candidate_id"));
        c.setCandidateCode(rs.getString("candidate_code"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setMobile(rs.getString("mobile"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            c.setDateOfBirth(dob.toLocalDate());
        }
        c.setGender(rs.getString("gender"));
        c.setQualification(rs.getString("qualification"));
        c.setCollege(rs.getString("college"));
        c.setUniversity(rs.getString("university"));
        c.setPassingYear(rs.getInt("passing_year"));
        c.setPercentage(rs.getBigDecimal("percentage"));
        c.setCgpa(rs.getBigDecimal("cgpa"));
        c.setBacklogCount(rs.getInt("backlog_count"));
        c.setPrimaryTechnology(rs.getString("primary_technology"));
        c.setCurrentLocation(rs.getString("current_location"));
        c.setPreferredLocation(rs.getString("preferred_location"));
        c.setProfileStatus(rs.getString("profile_status"));
        c.setPlacementStatus(rs.getString("placement_status"));
        if (rs.getTimestamp("created_at") != null) {
            c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return c;
    }
}

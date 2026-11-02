package com.recruitment.dao;

import com.recruitment.model.Placement;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for placements table in rpms_db.
 */
public class PlacementDAO {

    public int insert(Placement p) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, p);
        }
    }

    public int insert(Connection conn, Placement p) throws SQLException {
        String sql = "INSERT INTO placements (candidate_id, company_id, job_id, offer_id, designation, " +
                "final_package_lpa, expected_joining_date, actual_joining_date, joining_status, placement_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, p.getCandidateId());
            stmt.setInt(2, p.getCompanyId());
            stmt.setInt(3, p.getJobId());
            stmt.setInt(4, p.getOfferId());
            stmt.setString(5, p.getDesignation());
            stmt.setBigDecimal(6, p.getFinalPackageLpa() != null ? p.getFinalPackageLpa() : java.math.BigDecimal.ZERO);

            if (p.getExpectedJoiningDate() != null) {
                stmt.setDate(7, Date.valueOf(p.getExpectedJoiningDate()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            if (p.getActualJoiningDate() != null) {
                stmt.setDate(8, Date.valueOf(p.getActualJoiningDate()));
            } else {
                stmt.setNull(8, Types.DATE);
            }

            stmt.setString(9, p.getJoiningStatus() != null ? p.getJoiningStatus() : "PENDING");
            stmt.setString(10, p.getPlacementStatus() != null ? p.getPlacementStatus() : "ACTIVE");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        p.setPlacementId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean updateJoiningStatus(int placementId, String joiningStatus, LocalDate actualJoiningDate) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return updateJoiningStatus(conn, placementId, joiningStatus, actualJoiningDate);
        }
    }

    public boolean updateJoiningStatus(Connection conn, int placementId, String joiningStatus, LocalDate actualJoiningDate) throws SQLException {
        String sql = "UPDATE placements SET joining_status = ?, actual_joining_date = ? WHERE placement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, joiningStatus);
            if (actualJoiningDate != null) {
                stmt.setDate(2, Date.valueOf(actualJoiningDate));
            } else {
                stmt.setNull(2, Types.DATE);
            }
            stmt.setInt(3, placementId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePlacementStatus(Connection conn, int placementId, String placementStatus) throws SQLException {
        String sql = "UPDATE placements SET placement_status = ? WHERE placement_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placementStatus);
            stmt.setInt(2, placementId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Placement> findById(int placementId) throws SQLException {
        String sql = baseSelectSql() + " WHERE p.placement_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, placementId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPlacement(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Placement> findByOfferId(int offerId) throws SQLException {
        String sql = baseSelectSql() + " WHERE p.offer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, offerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPlacement(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Placement> findAll() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = baseSelectSql() + " ORDER BY p.placement_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPlacement(rs));
            }
        }
        return list;
    }

    public List<Placement> findByCandidateId(int candidateId) throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE p.candidate_id = ? ORDER BY p.placement_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPlacement(rs));
                }
            }
        }
        return list;
    }

    public List<Placement> findByCompanyId(int companyId) throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE p.company_id = ? ORDER BY p.placement_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPlacement(rs));
                }
            }
        }
        return list;
    }

    public List<Placement> findByJobId(int jobId) throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE p.job_id = ? ORDER BY p.placement_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPlacement(rs));
                }
            }
        }
        return list;
    }

    public List<Placement> findPlacedCandidates() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE p.joining_status = 'JOINED' AND p.placement_status = 'ACTIVE' " +
                "ORDER BY p.actual_joining_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPlacement(rs));
            }
        }
        return list;
    }

    public List<Placement> findNotJoinedCandidates() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE p.joining_status IN ('NOT_JOINED', 'NO_SHOW', 'DEFERRED') " +
                "ORDER BY p.placement_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPlacement(rs));
            }
        }
        return list;
    }

    public List<Placement> search(String keyword) throws SQLException {
        List<Placement> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = baseSelectSql() + " WHERE c.full_name LIKE ? OR c.candidate_code LIKE ? " +
                "OR comp.company_name LIKE ? OR p.designation LIKE ? OR p.joining_status LIKE ? " +
                "ORDER BY p.placement_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPlacement(rs));
                }
            }
        }
        return list;
    }

    private String baseSelectSql() {
        return "SELECT p.*, c.candidate_code, c.full_name AS candidate_name, " +
                "comp.company_code, comp.company_name, j.job_title " +
                "FROM placements p " +
                "JOIN candidates c ON p.candidate_id = c.candidate_id " +
                "JOIN companies comp ON p.company_id = comp.company_id " +
                "JOIN jobs j ON p.job_id = j.job_id";
    }

    public Placement mapResultSetToPlacement(ResultSet rs) throws SQLException {
        Placement p = new Placement();
        p.setPlacementId(rs.getInt("placement_id"));
        p.setCandidateId(rs.getInt("candidate_id"));
        p.setCompanyId(rs.getInt("company_id"));
        p.setJobId(rs.getInt("job_id"));
        p.setOfferId(rs.getInt("offer_id"));

        try {
            p.setCandidateCode(rs.getString("candidate_code"));
            p.setCandidateName(rs.getString("candidate_name"));
            p.setCompanyCode(rs.getString("company_code"));
            p.setCompanyName(rs.getString("company_name"));
            p.setJobTitle(rs.getString("job_title"));
        } catch (SQLException ignored) {
        }

        p.setDesignation(rs.getString("designation"));
        p.setFinalPackageLpa(rs.getBigDecimal("final_package_lpa"));

        Date ejd = rs.getDate("expected_joining_date");
        if (ejd != null) {
            p.setExpectedJoiningDate(ejd.toLocalDate());
        }

        Date ajd = rs.getDate("actual_joining_date");
        if (ajd != null) {
            p.setActualJoiningDate(ajd.toLocalDate());
        }

        p.setJoiningStatus(rs.getString("joining_status"));
        p.setPlacementStatus(rs.getString("placement_status"));

        if (rs.getTimestamp("created_at") != null) {
            p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return p;
    }
}

package com.recruitment.dao;

import com.recruitment.model.Offer;
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
 * Data Access Object for offers table in rpms_db.
 */
public class OfferDAO {

    public int insert(Offer offer) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, offer);
        }
    }

    public int insert(Connection conn, Offer offer) throws SQLException {
        String sql = "INSERT INTO offers (offer_code, application_id, designation, offered_package_lpa, " +
                "offer_date, expected_joining_date, offer_status, remarks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, offer.getOfferCode());
            stmt.setInt(2, offer.getApplicationId());
            stmt.setString(3, offer.getDesignation());
            stmt.setBigDecimal(4, offer.getOfferedPackageLpa() != null ? offer.getOfferedPackageLpa() : java.math.BigDecimal.ZERO);
            stmt.setDate(5, offer.getOfferDate() != null ? Date.valueOf(offer.getOfferDate()) : Date.valueOf(LocalDate.now()));

            if (offer.getExpectedJoiningDate() != null) {
                stmt.setDate(6, Date.valueOf(offer.getExpectedJoiningDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, offer.getOfferStatus() != null ? offer.getOfferStatus() : "OFFERED");
            stmt.setString(8, offer.getRemarks());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        offer.setOfferId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean updateStatus(int offerId, String status) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return updateStatus(conn, offerId, status);
        }
    }

    public boolean updateStatus(Connection conn, int offerId, String status) throws SQLException {
        String sql = "UPDATE offers SET offer_status = ? WHERE offer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, offerId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Offer> findById(int offerId) throws SQLException {
        String sql = baseSelectSql() + " WHERE o.offer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, offerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOffer(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Offer> findByCode(String offerCode) throws SQLException {
        String sql = baseSelectSql() + " WHERE o.offer_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, offerCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOffer(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Offer> findByApplicationId(int applicationId) throws SQLException {
        String sql = baseSelectSql() + " WHERE o.application_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOffer(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Offer> findAll() throws SQLException {
        List<Offer> list = new ArrayList<>();
        String sql = baseSelectSql() + " ORDER BY o.offer_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToOffer(rs));
            }
        }
        return list;
    }

    public List<Offer> search(String keyword) throws SQLException {
        List<Offer> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = baseSelectSql() + " WHERE o.offer_code LIKE ? OR c.full_name LIKE ? " +
                "OR comp.company_name LIKE ? OR o.designation LIKE ? OR o.offer_status LIKE ? " +
                "ORDER BY o.offer_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOffer(rs));
                }
            }
        }
        return list;
    }

    public List<Offer> findByCandidateId(int candidateId) throws SQLException {
        List<Offer> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE a.candidate_id = ? ORDER BY o.offer_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOffer(rs));
                }
            }
        }
        return list;
    }

    public List<Offer> findByCompanyId(int companyId) throws SQLException {
        List<Offer> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE j.company_id = ? ORDER BY o.offer_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToOffer(rs));
                }
            }
        }
        return list;
    }

    private String baseSelectSql() {
        return "SELECT o.*, a.candidate_id, a.job_id, j.company_id, " +
                "c.full_name AS candidate_name, comp.company_name, j.job_title " +
                "FROM offers o " +
                "JOIN applications a ON o.application_id = a.application_id " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id";
    }

    public Offer mapResultSetToOffer(ResultSet rs) throws SQLException {
        Offer o = new Offer();
        o.setOfferId(rs.getInt("offer_id"));
        o.setOfferCode(rs.getString("offer_code"));
        o.setApplicationId(rs.getInt("application_id"));
        o.setDesignation(rs.getString("designation"));
        o.setOfferedPackageLpa(rs.getBigDecimal("offered_package_lpa"));

        Date od = rs.getDate("offer_date");
        if (od != null) {
            o.setOfferDate(od.toLocalDate());
        }

        Date jd = rs.getDate("expected_joining_date");
        if (jd != null) {
            o.setExpectedJoiningDate(jd.toLocalDate());
        }

        o.setOfferStatus(rs.getString("offer_status"));
        o.setRemarks(rs.getString("remarks"));

        try {
            o.setCandidateId(rs.getInt("candidate_id"));
            o.setJobId(rs.getInt("job_id"));
            o.setCompanyId(rs.getInt("company_id"));
            o.setCandidateName(rs.getString("candidate_name"));
            o.setCompanyName(rs.getString("company_name"));
            o.setJobTitle(rs.getString("job_title"));
        } catch (SQLException ignored) {
        }

        if (rs.getTimestamp("created_at") != null) {
            o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return o;
    }
}

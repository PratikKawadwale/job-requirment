package com.recruitment.dao;

import com.recruitment.model.Qualification;
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
 * Data Access Object for qualifications master table in rpms_db.
 */
public class QualificationDAO {

    public int insert(Qualification qualification) throws SQLException {
        String sql = "INSERT INTO qualifications (qualification_name, is_active) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, qualification.getQualificationName());
            stmt.setBoolean(2, qualification.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        qualification.setQualificationId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public List<Qualification> findAll() throws SQLException {
        List<Qualification> list = new ArrayList<>();
        String sql = "SELECT * FROM qualifications ORDER BY qualification_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToQualification(rs));
            }
        }
        return list;
    }

    public List<Qualification> findActive() throws SQLException {
        List<Qualification> list = new ArrayList<>();
        String sql = "SELECT * FROM qualifications WHERE is_active = 1 ORDER BY qualification_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToQualification(rs));
            }
        }
        return list;
    }

    public Optional<Qualification> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM qualifications WHERE qualification_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToQualification(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Qualification mapResultSetToQualification(ResultSet rs) throws SQLException {
        Qualification q = new Qualification();
        q.setQualificationId(rs.getInt("qualification_id"));
        q.setQualificationName(rs.getString("qualification_name"));
        q.setActive(rs.getBoolean("is_active"));
        if (rs.getTimestamp("created_at") != null) {
            q.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return q;
    }
}

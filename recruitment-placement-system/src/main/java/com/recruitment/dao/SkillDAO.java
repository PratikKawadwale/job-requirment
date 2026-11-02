package com.recruitment.dao;

import com.recruitment.model.Skill;
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
 * Data Access Object for skills table in rpms_db.
 */
public class SkillDAO {

    public int insert(Skill skill) throws SQLException {
        String sql = "INSERT INTO skills (skill_name, category, is_active) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, skill.getSkillName());
            stmt.setString(2, skill.getCategory());
            stmt.setBoolean(3, skill.isActive());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        skill.setSkillId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public List<Skill> findAll() throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills ORDER BY skill_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSkill(rs));
            }
        }
        return list;
    }

    public List<Skill> findActive() throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE is_active = 1 ORDER BY skill_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSkill(rs));
            }
        }
        return list;
    }

    public Optional<Skill> findById(int skillId) throws SQLException {
        String sql = "SELECT * FROM skills WHERE skill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, skillId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Skill> findByName(String skillName) throws SQLException {
        String sql = "SELECT * FROM skills WHERE skill_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, skillName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Skill> search(String keyword) throws SQLException {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE skill_name LIKE ? OR category LIKE ? ORDER BY skill_name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword.trim() + "%");
            stmt.setString(2, "%" + keyword.trim() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSkill(rs));
                }
            }
        }
        return list;
    }

    private Skill mapResultSetToSkill(ResultSet rs) throws SQLException {
        Skill s = new Skill();
        s.setSkillId(rs.getInt("skill_id"));
        s.setSkillName(rs.getString("skill_name"));
        s.setCategory(rs.getString("category"));
        s.setActive(rs.getBoolean("is_active"));
        return s;
    }
}

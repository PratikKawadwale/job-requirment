package com.recruitment.dao;

import com.recruitment.model.CandidateSkill;
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
 * Data Access Object for candidate_skills table in rpms_db.
 */
public class CandidateSkillDAO {

    public int insert(CandidateSkill cs) throws SQLException {
        String sql = "INSERT INTO candidate_skills (candidate_id, skill_id, proficiency_level, years_experience, is_primary) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cs.getCandidateId());
            stmt.setInt(2, cs.getSkillId());
            stmt.setString(3, cs.getProficiencyLevel() != null ? cs.getProficiencyLevel() : "INTERMEDIATE");
            stmt.setBigDecimal(4, cs.getYearsExperience() != null ? cs.getYearsExperience() : java.math.BigDecimal.ZERO);
            stmt.setBoolean(5, cs.isPrimary());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        cs.setCandidateSkillId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean update(CandidateSkill cs) throws SQLException {
        String sql = "UPDATE candidate_skills SET proficiency_level = ?, years_experience = ?, is_primary = ? " +
                "WHERE candidate_skill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cs.getProficiencyLevel());
            stmt.setBigDecimal(2, cs.getYearsExperience());
            stmt.setBoolean(3, cs.isPrimary());
            stmt.setInt(4, cs.getCandidateSkillId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int candidateSkillId) throws SQLException {
        String sql = "DELETE FROM candidate_skills WHERE candidate_skill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateSkillId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<CandidateSkill> findByCandidateId(int candidateId) throws SQLException {
        List<CandidateSkill> list = new ArrayList<>();
        String sql = "SELECT cs.*, s.skill_name, s.category FROM candidate_skills cs " +
                "JOIN skills s ON cs.skill_id = s.skill_id " +
                "WHERE cs.candidate_id = ? ORDER BY cs.is_primary DESC, s.skill_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCandidateSkill(rs));
                }
            }
        }
        return list;
    }

    public Optional<CandidateSkill> findByCandidateAndSkill(int candidateId, int skillId) throws SQLException {
        String sql = "SELECT cs.*, s.skill_name, s.category FROM candidate_skills cs " +
                "JOIN skills s ON cs.skill_id = s.skill_id " +
                "WHERE cs.candidate_id = ? AND cs.skill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidateId);
            stmt.setInt(2, skillId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCandidateSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    private CandidateSkill mapResultSetToCandidateSkill(ResultSet rs) throws SQLException {
        CandidateSkill cs = new CandidateSkill();
        cs.setCandidateSkillId(rs.getInt("candidate_skill_id"));
        cs.setCandidateId(rs.getInt("candidate_id"));
        cs.setSkillId(rs.getInt("skill_id"));
        cs.setProficiencyLevel(rs.getString("proficiency_level"));
        cs.setYearsExperience(rs.getBigDecimal("years_experience"));
        cs.setPrimary(rs.getBoolean("is_primary"));

        try {
            cs.setSkillName(rs.getString("skill_name"));
            cs.setCategory(rs.getString("category"));
        } catch (SQLException ignored) {
        }
        return cs;
    }
}

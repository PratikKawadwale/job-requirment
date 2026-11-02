package com.recruitment.dao;

import com.recruitment.model.JobSkill;
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
 * Data Access Object for job_skills table in rpms_db.
 */
public class JobSkillDAO {

    public int insert(JobSkill js) throws SQLException {
        String sql = "INSERT INTO job_skills (job_id, skill_id, minimum_level, mandatory_flag) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, js.getJobId());
            stmt.setInt(2, js.getSkillId());
            stmt.setString(3, js.getMinimumLevel() != null ? js.getMinimumLevel() : "INTERMEDIATE");
            stmt.setBoolean(4, js.isMandatory());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        js.setJobSkillId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean update(JobSkill js) throws SQLException {
        String sql = "UPDATE job_skills SET minimum_level = ?, mandatory_flag = ? WHERE job_skill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, js.getMinimumLevel());
            stmt.setBoolean(2, js.isMandatory());
            stmt.setInt(3, js.getJobSkillId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int jobSkillId) throws SQLException {
        String sql = "DELETE FROM job_skills WHERE job_skill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobSkillId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<JobSkill> findByJobId(int jobId) throws SQLException {
        List<JobSkill> list = new ArrayList<>();
        String sql = "SELECT js.*, s.skill_name, s.category FROM job_skills js " +
                "JOIN skills s ON js.skill_id = s.skill_id " +
                "WHERE js.job_id = ? ORDER BY js.mandatory_flag DESC, s.skill_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToJobSkill(rs));
                }
            }
        }
        return list;
    }

    public Optional<JobSkill> findByJobAndSkill(int jobId, int skillId) throws SQLException {
        String sql = "SELECT js.*, s.skill_name, s.category FROM job_skills js " +
                "JOIN skills s ON js.skill_id = s.skill_id " +
                "WHERE js.job_id = ? AND js.skill_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jobId);
            stmt.setInt(2, skillId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToJobSkill(rs));
                }
            }
        }
        return Optional.empty();
    }

    private JobSkill mapResultSetToJobSkill(ResultSet rs) throws SQLException {
        JobSkill js = new JobSkill();
        js.setJobSkillId(rs.getInt("job_skill_id"));
        js.setJobId(rs.getInt("job_id"));
        js.setSkillId(rs.getInt("skill_id"));
        js.setMinimumLevel(rs.getString("minimum_level"));
        js.setMandatory(rs.getBoolean("mandatory_flag"));

        try {
            js.setSkillName(rs.getString("skill_name"));
            js.setCategory(rs.getString("category"));
        } catch (SQLException ignored) {
        }
        return js;
    }
}

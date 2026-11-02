package com.recruitment.dao;

import com.recruitment.model.InterviewFeedback;
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
 * Data Access Object for interview_feedback table in rpms_db.
 */
public class InterviewFeedbackDAO {

    public int insert(InterviewFeedback feedback) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, feedback);
        }
    }

    public int insert(Connection conn, InterviewFeedback feedback) throws SQLException {
        String sql = "INSERT INTO interview_feedback (interview_id, technical_rating, communication_rating, " +
                "problem_solving_rating, overall_rating, recommendation, remarks, submitted_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feedback.getInterviewId());

            if (feedback.getTechnicalRating() != null) {
                stmt.setBigDecimal(2, feedback.getTechnicalRating());
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }

            if (feedback.getCommunicationRating() != null) {
                stmt.setBigDecimal(3, feedback.getCommunicationRating());
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }

            if (feedback.getProblemSolvingRating() != null) {
                stmt.setBigDecimal(4, feedback.getProblemSolvingRating());
            } else {
                stmt.setNull(4, Types.DECIMAL);
            }

            if (feedback.getOverallRating() != null) {
                stmt.setBigDecimal(5, feedback.getOverallRating());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }

            stmt.setString(6, feedback.getRecommendation() != null ? feedback.getRecommendation() : "ON_HOLD");
            stmt.setString(7, feedback.getRemarks());
            stmt.setInt(8, feedback.getSubmittedBy() > 0 ? feedback.getSubmittedBy() : 1);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        feedback.setFeedbackId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public Optional<InterviewFeedback> findByInterviewId(int interviewId) throws SQLException {
        String sql = "SELECT fb.*, u.full_name AS submitted_by_name FROM interview_feedback fb " +
                "LEFT JOIN users u ON fb.submitted_by = u.user_id WHERE fb.interview_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFeedback(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<InterviewFeedback> findAll() throws SQLException {
        List<InterviewFeedback> list = new ArrayList<>();
        String sql = "SELECT fb.*, u.full_name AS submitted_by_name FROM interview_feedback fb " +
                "LEFT JOIN users u ON fb.submitted_by = u.user_id ORDER BY fb.feedback_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToFeedback(rs));
            }
        }
        return list;
    }

    private InterviewFeedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        InterviewFeedback fb = new InterviewFeedback();
        fb.setFeedbackId(rs.getInt("feedback_id"));
        fb.setInterviewId(rs.getInt("interview_id"));
        fb.setTechnicalRating(rs.getBigDecimal("technical_rating"));
        fb.setCommunicationRating(rs.getBigDecimal("communication_rating"));
        fb.setProblemSolvingRating(rs.getBigDecimal("problem_solving_rating"));
        fb.setOverallRating(rs.getBigDecimal("overall_rating"));
        fb.setRecommendation(rs.getString("recommendation"));
        fb.setRemarks(rs.getString("remarks"));
        fb.setSubmittedBy(rs.getInt("submitted_by"));

        try {
            fb.setSubmittedByName(rs.getString("submitted_by_name"));
        } catch (SQLException ignored) {
        }

        Timestamp ts = rs.getTimestamp("submitted_at");
        if (ts != null) {
            fb.setSubmittedAt(ts.toLocalDateTime());
        }
        return fb;
    }
}

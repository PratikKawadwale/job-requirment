package com.recruitment.dao;

import com.recruitment.model.Interview;
import com.recruitment.model.InterviewRound;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for interview_rounds table in rpms_db.
 */
public class InterviewDAO {

    public int insert(Interview interview) throws SQLException {
        String sql = "INSERT INTO interview_rounds (application_id, round_number, round_type, scheduled_date, " +
                "scheduled_time, mode, interviewer_name, meeting_or_location, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, interview.getApplicationId());
            stmt.setInt(2, interview.getRoundNumber() > 0 ? interview.getRoundNumber() : 1);
            stmt.setString(3, interview.getRoundType() != null ? interview.getRoundType() : "TECHNICAL");
            stmt.setDate(4, interview.getInterviewDate() != null ? Date.valueOf(interview.getInterviewDate()) : Date.valueOf(LocalDate.now()));

            if (interview.getInterviewTime() != null && !interview.getInterviewTime().isBlank()) {
                String tStr = interview.getInterviewTime().trim();
                if (tStr.length() == 5) tStr += ":00";
                stmt.setTime(5, Time.valueOf(tStr));
            } else {
                stmt.setTime(5, Time.valueOf(LocalTime.of(10, 0)));
            }

            stmt.setString(6, interview.getInterviewMode() != null ? interview.getInterviewMode() : "ONLINE");
            stmt.setString(7, interview.getInterviewerName());
            stmt.setString(8, interview.getMeetingOrLocation());
            stmt.setString(9, interview.getInterviewStatus() != null ? interview.getInterviewStatus() : "SCHEDULED");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        interview.setInterviewId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public int insertRound(InterviewRound round) throws SQLException {
        String sql = "INSERT INTO interview_rounds (application_id, round_number, round_type, scheduled_date, " +
                "scheduled_time, mode, interviewer_name, meeting_or_location, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, round.getApplicationId());
            stmt.setInt(2, round.getRoundNumber() > 0 ? round.getRoundNumber() : 1);
            stmt.setString(3, round.getRoundType() != null ? round.getRoundType() : "TECHNICAL");
            stmt.setDate(4, round.getScheduledDate() != null ? Date.valueOf(round.getScheduledDate()) : Date.valueOf(LocalDate.now()));

            if (round.getScheduledTime() != null) {
                stmt.setTime(5, Time.valueOf(round.getScheduledTime()));
            } else {
                stmt.setTime(5, Time.valueOf(LocalTime.of(10, 0)));
            }

            stmt.setString(6, round.getMode() != null ? round.getMode() : "ONLINE");
            stmt.setString(7, round.getInterviewerName());
            stmt.setString(8, round.getMeetingOrLocation());
            stmt.setString(9, round.getStatus() != null ? round.getStatus() : "SCHEDULED");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        round.setInterviewId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public boolean reschedule(int interviewId, LocalDate newDate, String newTime) throws SQLException {
        String sql = "UPDATE interview_rounds SET scheduled_date = ?, scheduled_time = ?, status = 'RESCHEDULED' WHERE interview_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(newDate));
            if (newTime != null && !newTime.isBlank()) {
                String tStr = newTime.trim();
                if (tStr.length() == 5) tStr += ":00";
                stmt.setTime(2, Time.valueOf(tStr));
            } else {
                stmt.setTime(2, Time.valueOf(LocalTime.of(10, 0)));
            }
            stmt.setInt(3, interviewId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int interviewId, String status) throws SQLException {
        String sql = "UPDATE interview_rounds SET status = ? WHERE interview_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, interviewId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Interview> findById(int interviewId) throws SQLException {
        String sql = baseSelectSql() + " WHERE ir.interview_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, interviewId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInterview(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Interview> findTodayInterviews() throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE ir.scheduled_date = CURDATE() ORDER BY ir.scheduled_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToInterview(rs));
            }
        }
        return list;
    }

    public List<Interview> findUpcomingInterviews() throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE ir.scheduled_date >= CURDATE() AND ir.status = 'SCHEDULED' " +
                "ORDER BY ir.scheduled_date ASC, ir.scheduled_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToInterview(rs));
            }
        }
        return list;
    }

    public List<Interview> findByApplicationId(int applicationId) throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = baseSelectSql() + " WHERE ir.application_id = ? ORDER BY ir.round_number ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToInterview(rs));
                }
            }
        }
        return list;
    }

    public List<Interview> findAll() throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = baseSelectSql() + " ORDER BY ir.interview_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToInterview(rs));
            }
        }
        return list;
    }

    public List<Interview> search(String keyword) throws SQLException {
        List<Interview> list = new ArrayList<>();
        String pattern = "%" + keyword.trim() + "%";
        String sql = baseSelectSql() + " WHERE c.full_name LIKE ? OR comp.company_name LIKE ? " +
                "OR j.job_title LIKE ? OR ir.round_type LIKE ? OR ir.interviewer_name LIKE ? " +
                "ORDER BY ir.interview_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToInterview(rs));
                }
            }
        }
        return list;
    }

    private String baseSelectSql() {
        return "SELECT ir.*, a.candidate_id, a.job_id, j.company_id, " +
                "c.full_name AS candidate_name, comp.company_name, j.job_title, " +
                "fb.technical_rating, fb.communication_rating, fb.problem_solving_rating, fb.overall_rating, " +
                "fb.recommendation, fb.remarks AS feedback_remarks " +
                "FROM interview_rounds ir " +
                "JOIN applications a ON ir.application_id = a.application_id " +
                "JOIN candidates c ON a.candidate_id = c.candidate_id " +
                "JOIN jobs j ON a.job_id = j.job_id " +
                "JOIN companies comp ON j.company_id = comp.company_id " +
                "LEFT JOIN interview_feedback fb ON ir.interview_id = fb.interview_id";
    }

    public Interview mapResultSetToInterview(ResultSet rs) throws SQLException {
        Interview i = new Interview();
        i.setInterviewId(rs.getInt("interview_id"));
        i.setApplicationId(rs.getInt("application_id"));
        i.setRoundNumber(rs.getInt("round_number"));
        i.setRoundType(rs.getString("round_type"));
        i.setRoundName(rs.getString("round_type") + " (Round " + rs.getInt("round_number") + ")");
        i.setCandidateId(rs.getInt("candidate_id"));
        i.setCompanyId(rs.getInt("company_id"));
        i.setJobId(rs.getInt("job_id"));

        try {
            i.setCandidateName(rs.getString("candidate_name"));
            i.setCompanyName(rs.getString("company_name"));
            i.setJobTitle(rs.getString("job_title"));
        } catch (SQLException ignored) {
        }

        Date dt = rs.getDate("scheduled_date");
        if (dt != null) {
            i.setInterviewDate(dt.toLocalDate());
        }

        Time tm = rs.getTime("scheduled_time");
        if (tm != null) {
            i.setInterviewTime(tm.toString());
        }

        i.setInterviewMode(rs.getString("mode"));
        i.setInterviewerName(rs.getString("interviewer_name"));
        i.setMeetingOrLocation(rs.getString("meeting_or_location"));
        i.setInterviewStatus(rs.getString("status"));

        java.math.BigDecimal tech = rs.getBigDecimal("technical_rating");
        if (tech != null) i.setTechnicalRating(tech.intValue());

        java.math.BigDecimal comm = rs.getBigDecimal("communication_rating");
        if (comm != null) i.setCommunicationRating(comm.intValue());

        i.setRemarks(rs.getString("feedback_remarks"));
        i.setResult(rs.getString("recommendation") != null ? rs.getString("recommendation") : "ON_HOLD");

        if (rs.getTimestamp("created_at") != null) {
            i.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return i;
    }
}

package com.recruitment.dao;

import com.recruitment.model.ApplicationHistory;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for application_history table in rpms_db.
 */
public class ApplicationHistoryDAO {

    public long insert(ApplicationHistory history) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, history);
        }
    }

    public long insert(Connection conn, ApplicationHistory history) throws SQLException {
        String sql = "INSERT INTO application_history (application_id, old_status, new_status, remarks, changed_by) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, history.getApplicationId());
            stmt.setString(2, history.getOldStatus());
            stmt.setString(3, history.getNewStatus());
            stmt.setString(4, history.getRemarks());
            stmt.setInt(5, history.getChangedBy() > 0 ? history.getChangedBy() : 1);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        history.setHistoryId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public List<ApplicationHistory> findByApplicationId(int applicationId) throws SQLException {
        List<ApplicationHistory> list = new ArrayList<>();
        String sql = "SELECT ah.*, u.full_name AS changed_by_name FROM application_history ah " +
                "LEFT JOIN users u ON ah.changed_by = u.user_id " +
                "WHERE ah.application_id = ? ORDER BY ah.changed_at ASC, ah.history_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHistory(rs));
                }
            }
        }
        return list;
    }

    private ApplicationHistory mapResultSetToHistory(ResultSet rs) throws SQLException {
        ApplicationHistory ah = new ApplicationHistory();
        ah.setHistoryId(rs.getLong("history_id"));
        ah.setApplicationId(rs.getInt("application_id"));
        ah.setOldStatus(rs.getString("old_status"));
        ah.setNewStatus(rs.getString("new_status"));
        ah.setRemarks(rs.getString("remarks"));
        ah.setChangedBy(rs.getInt("changed_by"));

        try {
            ah.setChangedByName(rs.getString("changed_by_name"));
        } catch (SQLException ignored) {
        }

        Timestamp ts = rs.getTimestamp("changed_at");
        if (ts != null) {
            ah.setChangedAt(ts.toLocalDateTime());
        }
        return ah;
    }
}

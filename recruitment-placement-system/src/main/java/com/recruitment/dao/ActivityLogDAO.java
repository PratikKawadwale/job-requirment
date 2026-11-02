package com.recruitment.dao;

import com.recruitment.model.ActivityLog;
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

/**
 * Data Access Object for activity_logs table in rpms_db.
 */
public class ActivityLogDAO {

    public long insert(ActivityLog log) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            return insert(conn, log);
        }
    }

    public long insert(Connection conn, ActivityLog log) throws SQLException {
        String sql = "INSERT INTO activity_logs (user_id, module_name, action_name, record_id, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) {
                stmt.setInt(1, log.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, log.getModuleName());
            stmt.setString(3, log.getActionName());
            stmt.setString(4, log.getRecordId());
            stmt.setString(5, log.getDescription());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        log.setLogId(id);
                        return id;
                    }
                }
            }
            return -1;
        }
    }

    public List<ActivityLog> findAll() throws SQLException {
        return findRecent(100);
    }

    public List<ActivityLog> findRecent(int limit) throws SQLException {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                "LEFT JOIN users u ON al.user_id = u.user_id " +
                "ORDER BY al.logged_at DESC, al.log_id DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        }
        return list;
    }

    public List<ActivityLog> findByModule(String module) throws SQLException {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT al.*, u.username FROM activity_logs al " +
                "LEFT JOIN users u ON al.user_id = u.user_id " +
                "WHERE al.module_name = ? ORDER BY al.logged_at DESC, al.log_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, module);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
        }
        return list;
    }

    private ActivityLog mapResultSetToLog(ResultSet rs) throws SQLException {
        ActivityLog l = new ActivityLog();
        l.setLogId(rs.getLong("log_id"));
        int uid = rs.getInt("user_id");
        if (!rs.wasNull()) {
            l.setUserId(uid);
        }
        l.setModuleName(rs.getString("module_name"));
        l.setActionName(rs.getString("action_name"));
        l.setRecordId(rs.getString("record_id"));
        l.setDescription(rs.getString("description"));

        try {
            l.setUsername(rs.getString("username"));
        } catch (SQLException ignored) {
        }

        Timestamp ts = rs.getTimestamp("logged_at");
        if (ts != null) {
            l.setLoggedAt(ts.toLocalDateTime());
        }
        return l;
    }
}

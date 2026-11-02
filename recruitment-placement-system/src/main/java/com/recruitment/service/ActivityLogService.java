package com.recruitment.service;

import com.recruitment.dao.ActivityLogDAO;
import com.recruitment.model.ActivityLog;
import com.recruitment.util.ActivityLogger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing activity logs in rpms_db.activity_logs and activity_log.txt.
 */
public class ActivityLogService {

    private final ActivityLogDAO activityLogDAO;

    public ActivityLogService() {
        this.activityLogDAO = new ActivityLogDAO();
    }

    public ActivityLogService(ActivityLogDAO activityLogDAO) {
        this.activityLogDAO = activityLogDAO;
    }

    public void log(Integer userId, String moduleName, String actionName, String recordId, String description) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId != null && userId > 0 ? userId : 1);
        log.setModuleName(moduleName);
        log.setActionName(actionName);
        log.setRecordId(recordId);
        log.setDescription(description);

        try {
            activityLogDAO.insert(log);
        } catch (SQLException e) {
            System.err.println("[WARN] Failed to insert DB activity log: " + e.getMessage());
        }

        // Also append to activity_log.txt
        ActivityLogger.log(String.format("[%s - %s] (Record: %s, User: %s) %s",
                moduleName, actionName, recordId, (userId != null ? userId : "SYS"), description));
    }

    public void log(Connection conn, Integer userId, String moduleName, String actionName, String recordId, String description) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId != null && userId > 0 ? userId : 1);
        log.setModuleName(moduleName);
        log.setActionName(actionName);
        log.setRecordId(recordId);
        log.setDescription(description);

        activityLogDAO.insert(conn, log);

        // Also append to file
        ActivityLogger.log(String.format("[%s - %s] (Record: %s, User: %s) %s",
                moduleName, actionName, recordId, (userId != null ? userId : "SYS"), description));
    }

    public List<ActivityLog> getRecentLogs(int limit) throws SQLException {
        return activityLogDAO.findRecent(limit);
    }

    public List<ActivityLog> getLogsByModule(String module) throws SQLException {
        return activityLogDAO.findByModule(module);
    }
}

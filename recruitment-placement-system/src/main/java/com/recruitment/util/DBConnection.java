package com.recruitment.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class to manage JDBC database connections using configuration files
 * and environment variable overrides for RPMS.
 */
public class DBConnection {

    private static String jdbcUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean initialized = false;

    private DBConnection() {
        // Prevent direct instantiation
    }

    private static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Explicitly load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            Properties props = new Properties();
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    props.load(input);
                }
            } catch (Exception e) {
                System.err.println("[WARN] Could not load db.properties, using fallback defaults or environment variables.");
            }

            String host = System.getenv("DB_HOST");
            if (host == null || host.isBlank()) {
                host = props.getProperty("db.host", "localhost");
            }

            String port = System.getenv("DB_PORT");
            if (port == null || port.isBlank()) {
                port = props.getProperty("db.port", "3306");
            }

            String dbName = System.getenv("DB_NAME");
            if (dbName == null || dbName.isBlank()) {
                dbName = props.getProperty("db.name", "rpms_db");
            }

            dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.isBlank()) {
                dbUser = props.getProperty("db.user", "root");
            }

            dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword == null) {
                dbPassword = props.getProperty("db.password", "root");
            }

            jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                    host, port, dbName);

            initialized = true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver (com.mysql.cj.jdbc.Driver) not found in classpath.", e);
        }
    }

    /**
     * Obtains a new database connection from the driver manager.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }

    /**
     * Tests the database connectivity and prints a helpful status message.
     *
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[ERROR] Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public static String getJdbcUrl() {
        if (!initialized) {
            initialize();
        }
        return jdbcUrl;
    }

    public static String getDbUser() {
        if (!initialized) {
            initialize();
        }
        return dbUser;
    }
}

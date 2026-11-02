package com.recruitment.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File handling utility for logging important business events into activity_log.txt.
 * Fails safely without interrupting the primary transaction or business flow.
 */
public class ActivityLogger {

    private static final String DEFAULT_LOG_FILE = "activity_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ActivityLogger() {
        // Utility class
    }

    /**
     * Appends an event to the default activity_log.txt file.
     *
     * @param actionDescription description of the successful business activity
     */
    public static void log(String actionDescription) {
        log(DEFAULT_LOG_FILE, actionDescription);
    }

    /**
     * Appends an event to a specified log file path.
     *
     * @param filePath relative or absolute path of the target log file
     * @param actionDescription description of the activity
     */
    public static synchronized void log(String filePath, String actionDescription) {
        if (actionDescription == null || actionDescription.isBlank()) {
            return;
        }

        try {
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            String timestamp = LocalDateTime.now().format(FORMATTER);
            String logEntry = String.format("[%s] %s%n", timestamp, actionDescription.trim());

            Files.writeString(
                    path,
                    logEntry,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // Log failure to standard error without crashing the running application
            System.err.println("[WARN] Failed to write to activity log: " + e.getMessage());
        }
    }
}

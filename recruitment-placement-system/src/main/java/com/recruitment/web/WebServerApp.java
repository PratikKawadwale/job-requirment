package com.recruitment.web;

import com.recruitment.util.DBConnection;

import java.io.File;

/**
 * Main launcher for the Web UI Server.
 * Runs on http://localhost:8080/
 */
public class WebServerApp {

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        // Detect UI root folder
        File uiDir = new File("../recruitment-placement-ui");
        if (!uiDir.exists()) {
            uiDir = new File("recruitment-placement-ui");
        }
        if (!uiDir.exists()) {
            uiDir = new File("../../recruitment-placement-ui");
        }

        System.out.println("==================================================================");
        System.out.println(" Starting Recruitment & Placement System (Web UI Mode)...        ");
        System.out.println("==================================================================");
        System.out.print("[INFO] Verifying database connectivity: ");
        if (DBConnection.testConnection()) {
            System.out.println("SUCCESS (Connected to rpms_db)");
        } else {
            System.out.println("FAILED!");
            System.out.println("[WARN] Please ensure MySQL is running (docker container 'mysql8').");
        }

        try {
            WebServer server = new WebServer(port, uiDir);
            server.start();

            System.out.println("\n[READY] Open your browser and navigate to:");
            System.out.println(">> http://localhost:" + port + "/ <<\n");
            System.out.println("Press Ctrl+C to stop the web server.\n");

            // Keep main thread alive
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

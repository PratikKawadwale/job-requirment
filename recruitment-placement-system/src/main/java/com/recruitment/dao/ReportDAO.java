package com.recruitment.dao;

import com.recruitment.model.Candidate;
import com.recruitment.model.Job;
import com.recruitment.model.Placement;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for aggregating and generating management reports against rpms_db.
 */
public class ReportDAO {

    private final JobDAO jobDAO = new JobDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final PlacementDAO placementDAO = new PlacementDAO();

    /**
     * Report 1: Candidates count grouped by primary technology.
     */
    public Map<String, Integer> getCandidatesByTechnology() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        String sql = "SELECT COALESCE(primary_technology, 'Unspecified') AS tech, COUNT(*) AS total " +
                "FROM candidates GROUP BY primary_technology ORDER BY total DESC, tech ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("tech"), rs.getInt("total"));
            }
        }
        return report;
    }

    /**
     * Report 2: Currently open jobs with valid deadlines.
     */
    public List<Job> getOpenJobs() throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT j.*, c.company_name FROM jobs j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE j.status = 'OPEN' AND j.application_deadline >= CURDATE() " +
                "ORDER BY j.application_deadline ASC, j.job_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(jobDAO.mapResultSetToJob(rs));
            }
        }
        return list;
    }

    /**
     * Report 3: Application counts grouped by status.
     */
    public Map<String, Integer> getApplicationsByStatus() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        // Seed exact RPMS status enum values
        report.put("APPLIED", 0);
        report.put("ELIGIBILITY_VERIFIED", 0);
        report.put("SHORTLISTED", 0);
        report.put("INTERVIEW_SCHEDULED", 0);
        report.put("INTERVIEWED", 0);
        report.put("SELECTED", 0);
        report.put("OFFERED", 0);
        report.put("JOINED", 0);
        report.put("REJECTED", 0);
        report.put("WITHDRAWN", 0);

        String sql = "SELECT application_status, COUNT(*) AS total FROM applications GROUP BY application_status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("application_status"), rs.getInt("total"));
            }
        }
        return report;
    }

    /**
     * Report 4: Company-wise confirmed placements.
     */
    public Map<String, Integer> getCompanyWisePlacements() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        String sql = "SELECT c.company_name, COUNT(p.placement_id) AS total_placed " +
                "FROM companies c " +
                "LEFT JOIN placements p ON c.company_id = p.company_id AND p.joining_status = 'JOINED' " +
                "GROUP BY c.company_id, c.company_name ORDER BY total_placed DESC, c.company_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("company_name"), rs.getInt("total_placed"));
            }
        }
        return report;
    }

    /**
     * Report 5: Confirmed placed candidates with full joining and salary details.
     */
    public List<Placement> getPlacedCandidates() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = "SELECT p.*, c.candidate_code, c.full_name AS candidate_name, " +
                "comp.company_code, comp.company_name, j.job_title " +
                "FROM placements p " +
                "JOIN candidates c ON p.candidate_id = c.candidate_id " +
                "JOIN companies comp ON p.company_id = comp.company_id " +
                "JOIN jobs j ON p.job_id = j.job_id " +
                "WHERE p.joining_status = 'JOINED' AND p.placement_status = 'ACTIVE' " +
                "ORDER BY p.actual_joining_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(placementDAO.mapResultSetToPlacement(rs));
            }
        }
        return list;
    }

    /**
     * Report 6: Active candidates who do not have a confirmed successful placement.
     */
    public List<Candidate> getUnplacedCandidates() throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT c.* FROM candidates c " +
                "WHERE c.profile_status = 'ACTIVE' AND (c.placement_status = 'NOT_PLACED' OR NOT EXISTS ( " +
                "    SELECT 1 FROM placements p " +
                "    WHERE p.candidate_id = c.candidate_id AND p.joining_status = 'JOINED' " +
                ")) ORDER BY c.percentage DESC, c.candidate_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(candidateDAO.mapResultSetToCandidate(rs));
            }
        }
        return list;
    }

    /**
     * Additional Report: Skill distribution across candidates.
     */
    public Map<String, Integer> getSkillDistribution() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        String sql = "SELECT s.skill_name, COUNT(cs.candidate_id) AS total_candidates " +
                "FROM skills s " +
                "LEFT JOIN candidate_skills cs ON s.skill_id = cs.skill_id " +
                "GROUP BY s.skill_id, s.skill_name ORDER BY total_candidates DESC, s.skill_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("skill_name"), rs.getInt("total_candidates"));
            }
        }
        return report;
    }

    /**
     * Additional Report: Jobs count by company.
     */
    public Map<String, Integer> getJobsByCompany() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        String sql = "SELECT c.company_name, COUNT(j.job_id) AS total_jobs " +
                "FROM companies c " +
                "LEFT JOIN jobs j ON c.company_id = j.company_id " +
                "GROUP BY c.company_id, c.company_name ORDER BY total_jobs DESC, c.company_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("company_name"), rs.getInt("total_jobs"));
            }
        }
        return report;
    }

    /**
     * Additional Report: Offers count by status.
     */
    public Map<String, Integer> getOffersByStatus() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        report.put("OFFERED", 0);
        report.put("ACCEPTED", 0);
        report.put("REJECTED", 0);
        report.put("EXPIRED", 0);
        report.put("WITHDRAWN", 0);

        String sql = "SELECT offer_status, COUNT(*) AS total FROM offers GROUP BY offer_status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("offer_status"), rs.getInt("total"));
            }
        }
        return report;
    }

    /**
     * Additional Report: Joining status summary.
     */
    public Map<String, Integer> getJoiningStatusSummary() throws SQLException {
        Map<String, Integer> report = new LinkedHashMap<>();
        report.put("PENDING", 0);
        report.put("JOINED", 0);
        report.put("NOT_JOINED", 0);
        report.put("DEFERRED", 0);
        report.put("NO_SHOW", 0);

        String sql = "SELECT joining_status, COUNT(*) AS total FROM placements GROUP BY joining_status";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                report.put(rs.getString("joining_status"), rs.getInt("total"));
            }
        }
        return report;
    }
}

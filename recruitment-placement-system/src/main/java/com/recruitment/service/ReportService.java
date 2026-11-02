package com.recruitment.service;

import com.recruitment.dao.ReportDAO;
import com.recruitment.model.Candidate;
import com.recruitment.model.Job;
import com.recruitment.model.Placement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service Layer for generating aggregated recruitment and placement analytics reports.
 */
public class ReportService {

    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     * Report 1: Candidates grouped by Primary Technology.
     */
    public Map<String, Integer> getCandidatesByTechnology() throws SQLException {
        return reportDAO.getCandidatesByTechnology();
    }

    /**
     * Report 2: Open jobs with non-expired deadlines.
     */
    public List<Job> getOpenJobs() throws SQLException {
        return reportDAO.getOpenJobs();
    }

    /**
     * Report 3: Applications count grouped by workflow status.
     */
    public Map<String, Integer> getApplicationsByStatus() throws SQLException {
        return reportDAO.getApplicationsByStatus();
    }

    /**
     * Report 4: Placed candidate counts per employer company.
     */
    public Map<String, Integer> getCompanyWisePlacements() throws SQLException {
        return reportDAO.getCompanyWisePlacements();
    }

    /**
     * Report 5: Confirmed placed candidates with full details.
     */
    public List<Placement> getPlacedCandidates() throws SQLException {
        return reportDAO.getPlacedCandidates();
    }

    /**
     * Report 6: Active candidates who are not yet placed.
     */
    public List<Candidate> getUnplacedCandidates() throws SQLException {
        return reportDAO.getUnplacedCandidates();
    }

    /**
     * Additional Report: Skill distribution across candidates.
     */
    public Map<String, Integer> getSkillDistribution() throws SQLException {
        return reportDAO.getSkillDistribution();
    }

    /**
     * Additional Report: Jobs count by company.
     */
    public Map<String, Integer> getJobsByCompany() throws SQLException {
        return reportDAO.getJobsByCompany();
    }

    /**
     * Additional Report: Offers count by status.
     */
    public Map<String, Integer> getOffersByStatus() throws SQLException {
        return reportDAO.getOffersByStatus();
    }

    /**
     * Additional Report: Joining status summary.
     */
    public Map<String, Integer> getJoiningStatusSummary() throws SQLException {
        return reportDAO.getJoiningStatusSummary();
    }
}

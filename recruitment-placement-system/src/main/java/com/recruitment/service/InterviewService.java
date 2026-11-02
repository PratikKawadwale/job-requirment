package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.InterviewDAO;
import com.recruitment.dao.InterviewFeedbackDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.Interview;
import com.recruitment.model.InterviewFeedback;
import com.recruitment.model.InterviewRound;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer for scheduling, rescheduling, conducting interviews, and recording feedback.
 */
public class InterviewService {

    private final InterviewDAO interviewDAO;
    private final InterviewFeedbackDAO feedbackDAO;
    private final ApplicationDAO applicationDAO;
    private final ActivityLogService activityLogService;

    public InterviewService() {
        this.interviewDAO = new InterviewDAO();
        this.feedbackDAO = new InterviewFeedbackDAO();
        this.applicationDAO = new ApplicationDAO();
        this.activityLogService = new ActivityLogService();
    }

    public InterviewService(InterviewDAO interviewDAO, InterviewFeedbackDAO feedbackDAO,
                            ApplicationDAO applicationDAO, ActivityLogService activityLogService) {
        this.interviewDAO = interviewDAO;
        this.feedbackDAO = feedbackDAO;
        this.applicationDAO = applicationDAO;
        this.activityLogService = activityLogService;
    }

    public int scheduleInterview(Interview interview) throws ValidationException, SQLException {
        validateInterview(interview);

        Optional<Application> appOpt = applicationDAO.findById(interview.getApplicationId());
        if (appOpt.isEmpty()) {
            throw new ValidationException("Error: Application not found with ID: " + interview.getApplicationId());
        }
        Application app = appOpt.get();

        String currentStatus = app.getApplicationStatus();
        if (!"SHORTLISTED".equalsIgnoreCase(currentStatus) &&
                !"INTERVIEWED".equalsIgnoreCase(currentStatus) &&
                !"INTERVIEW_SCHEDULED".equalsIgnoreCase(currentStatus)) {
            throw new ValidationException("Error: Cannot schedule interview. Application status must be SHORTLISTED or INTERVIEWED, but is: " + currentStatus);
        }

        // Determine round number if not specified
        if (interview.getRoundNumber() <= 0) {
            List<Interview> existing = interviewDAO.findByApplicationId(interview.getApplicationId());
            interview.setRoundNumber(existing.size() + 1);
        }

        int intId = interviewDAO.insert(interview);
        if (intId > 0) {
            applicationDAO.updateStatus(app.getApplicationId(), "INTERVIEW_SCHEDULED");
            activityLogService.log(1, "INTERVIEW", "SCHEDULE", String.valueOf(intId),
                    "Interview Round #" + interview.getRoundNumber() + " scheduled for Application #" + interview.getApplicationId());
        }
        return intId;
    }

    public boolean rescheduleInterview(int interviewId, LocalDate newDate, String newTime) throws ValidationException, SQLException {
        if (interviewId <= 0) {
            throw new ValidationException("Error: Invalid interview ID.");
        }
        if (newDate == null) {
            throw new ValidationException("Error: Reschedule date is mandatory.");
        }

        Optional<Interview> interview = interviewDAO.findById(interviewId);
        if (interview.isEmpty()) {
            throw new ValidationException("Error: Interview not found with ID: " + interviewId);
        }

        boolean ok = interviewDAO.reschedule(interviewId, newDate, newTime);
        if (ok) {
            activityLogService.log(1, "INTERVIEW", "RESCHEDULE", String.valueOf(interviewId),
                    "Interview #" + interviewId + " rescheduled to " + newDate + " " + (newTime != null ? newTime : ""));
        }
        return ok;
    }

    public boolean cancelInterview(int interviewId) throws ValidationException, SQLException {
        Optional<Interview> interview = interviewDAO.findById(interviewId);
        if (interview.isEmpty()) {
            throw new ValidationException("Error: Interview not found with ID: " + interviewId);
        }
        boolean ok = interviewDAO.updateStatus(interviewId, "CANCELLED");
        if (ok) {
            activityLogService.log(1, "INTERVIEW", "CANCEL", String.valueOf(interviewId),
                    "Interview cancelled: #" + interviewId);
        }
        return ok;
    }

    public boolean markNoShow(int interviewId) throws ValidationException, SQLException {
        Optional<Interview> interview = interviewDAO.findById(interviewId);
        if (interview.isEmpty()) {
            throw new ValidationException("Error: Interview not found with ID: " + interviewId);
        }
        boolean ok = interviewDAO.updateStatus(interviewId, "NO_SHOW");
        if (ok) {
            activityLogService.log(1, "INTERVIEW", "NO_SHOW", String.valueOf(interviewId),
                    "Interview marked NO_SHOW: #" + interviewId);
        }
        return ok;
    }

    public boolean completeInterview(int interviewId) throws ValidationException, SQLException {
        Optional<Interview> interview = interviewDAO.findById(interviewId);
        if (interview.isEmpty()) {
            throw new ValidationException("Error: Interview not found with ID: " + interviewId);
        }
        return interviewDAO.updateStatus(interviewId, "COMPLETED");
    }

    public boolean recordFeedback(int interviewId, BigDecimal technicalRating, BigDecimal communicationRating,
                                  BigDecimal problemSolvingRating, BigDecimal overallRating,
                                  String recommendation, String remarks, int submittedBy,
                                  boolean advanceWorkflow) throws ValidationException, SQLException {

        Optional<Interview> intOpt = interviewDAO.findById(interviewId);
        if (intOpt.isEmpty()) {
            throw new ValidationException("Error: Interview not found with ID: " + interviewId);
        }
        Interview interview = intOpt.get();

        // Valid ratings: 0.0 to 5.0 (schema constraint)
        validateRating("Technical rating", technicalRating);
        validateRating("Communication rating", communicationRating);
        validateRating("Problem solving rating", problemSolvingRating);
        validateRating("Overall rating", overallRating);

        if (recommendation == null || recommendation.isBlank()) {
            throw new ValidationException("Error: Interview recommendation is mandatory (NEXT_ROUND, SELECTED, REJECTED, ON_HOLD).");
        }
        String rec = recommendation.toUpperCase().trim();
        if (!rec.equals("NEXT_ROUND") && !rec.equals("SELECTED") && !rec.equals("REJECTED") && !rec.equals("ON_HOLD")) {
            throw new ValidationException("Error: Invalid recommendation. Must be NEXT_ROUND, SELECTED, REJECTED, or ON_HOLD.");
        }

        InterviewFeedback feedback = new InterviewFeedback();
        feedback.setInterviewId(interviewId);
        feedback.setTechnicalRating(technicalRating);
        feedback.setCommunicationRating(communicationRating);
        feedback.setProblemSolvingRating(problemSolvingRating);
        feedback.setOverallRating(overallRating);
        feedback.setRecommendation(rec);
        feedback.setRemarks(remarks);
        feedback.setSubmittedBy(submittedBy > 0 ? submittedBy : 1);

        int fbId = feedbackDAO.insert(feedback);
        if (fbId > 0) {
            interviewDAO.updateStatus(interviewId, "COMPLETED");
            activityLogService.log(submittedBy > 0 ? submittedBy : 1, "INTERVIEW", "FEEDBACK",
                    String.valueOf(interviewId), "Feedback submitted for Interview #" + interviewId + ", Rec: " + rec);

            // Update application status
            if (advanceWorkflow) {
                if ("SELECTED".equalsIgnoreCase(rec)) {
                    applicationDAO.updateStatus(interview.getApplicationId(), "SELECTED");
                } else if ("REJECTED".equalsIgnoreCase(rec)) {
                    applicationDAO.updateStatus(interview.getApplicationId(), "REJECTED");
                } else {
                    applicationDAO.updateStatus(interview.getApplicationId(), "INTERVIEWED");
                }
            } else {
                applicationDAO.updateStatus(interview.getApplicationId(), "INTERVIEWED");
            }
            return true;
        }
        return false;
    }

    public boolean recordFeedback(int interviewId, Integer technicalRating, Integer communicationRating,
                                  String remarks, String result, boolean advanceToSelected) throws ValidationException, SQLException {
        BigDecimal tech = technicalRating != null ? BigDecimal.valueOf(technicalRating) : null;
        BigDecimal comm = communicationRating != null ? BigDecimal.valueOf(communicationRating) : null;
        BigDecimal overall = (tech != null && comm != null) ? tech.add(comm).divide(BigDecimal.valueOf(2), 1, java.math.RoundingMode.HALF_UP) : tech;
        return recordFeedback(interviewId, tech, comm, tech, overall, result, remarks, 1, advanceToSelected);
    }

    public Interview getInterviewById(int interviewId) throws ValidationException, SQLException {
        return interviewDAO.findById(interviewId)
                .orElseThrow(() -> new ValidationException("Error: Interview not found with ID: " + interviewId));
    }

    public List<Interview> getTodayInterviews() throws SQLException {
        return interviewDAO.findTodayInterviews();
    }

    public List<Interview> getUpcomingInterviews() throws SQLException {
        return interviewDAO.findUpcomingInterviews();
    }

    public List<Interview> getInterviewsByApplication(int applicationId) throws SQLException {
        return interviewDAO.findByApplicationId(applicationId);
    }

    public List<Interview> getAllInterviews() throws SQLException {
        return interviewDAO.findAll();
    }

    public List<Interview> searchInterviews(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            return getAllInterviews();
        }
        return interviewDAO.search(keyword);
    }

    public Optional<InterviewFeedback> getFeedback(int interviewId) throws SQLException {
        return feedbackDAO.findByInterviewId(interviewId);
    }

    private void validateRating(String name, BigDecimal rating) throws ValidationException {
        if (rating != null) {
            if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5.0")) > 0) {
                throw new ValidationException("Error: " + name + " must be between 0 and 5.");
            }
        }
    }

    public void validateInterview(Interview interview) throws ValidationException {
        if (interview == null) {
            throw new ValidationException("Error: Interview details cannot be null.");
        }
        if (interview.getApplicationId() <= 0) {
            throw new ValidationException("Error: Valid application ID is mandatory.");
        }
        if (interview.getInterviewDate() == null) {
            throw new ValidationException("Error: Interview date is mandatory.");
        }
    }
}

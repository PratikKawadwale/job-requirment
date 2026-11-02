package com.recruitment.app;

import com.recruitment.exception.CandidateNotEligibleException;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.DuplicateApplicationException;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.Candidate;
import com.recruitment.model.Company;
import com.recruitment.model.EligibilityResult;
import com.recruitment.model.Interview;
import com.recruitment.model.Job;
import com.recruitment.model.Offer;
import com.recruitment.model.Placement;
import com.recruitment.service.ApplicationService;
import com.recruitment.service.CandidateService;
import com.recruitment.service.CompanyService;
import com.recruitment.service.InterviewService;
import com.recruitment.service.JobService;
import com.recruitment.service.OfferService;
import com.recruitment.service.PlacementService;
import com.recruitment.service.ReportService;
import com.recruitment.util.DBConnection;
import com.recruitment.util.InputUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Main Console Application for the Recruitment & Placement Management System.
 * Coordinates user interaction with the underlying service layers.
 */
public class RecruitmentApp {

    private final CandidateService candidateService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final InterviewService interviewService;
    private final OfferService offerService;
    private final PlacementService placementService;
    private final ReportService reportService;

    public RecruitmentApp() {
        this.candidateService = new CandidateService();
        this.companyService = new CompanyService();
        this.jobService = new JobService();
        this.applicationService = new ApplicationService();
        this.interviewService = new InterviewService();
        this.offerService = new OfferService();
        this.placementService = new PlacementService();
        this.reportService = new ReportService();
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Starting Recruitment & Placement System...    ");
        System.out.println("=================================================");

        // Test database connectivity on boot
        System.out.print("[INFO] Testing database connectivity: ");
        if (DBConnection.testConnection()) {
            System.out.println("SUCCESS (Connected to " + DBConnection.getJdbcUrl() + ")");
        } else {
            System.out.println("FAILED!");
            System.out.println("[WARN] Please ensure MySQL is running and credentials in db.properties or environment are correct.");
            System.out.println("[WARN] You can still browse the menus, but DB operations will fail until MySQL is accessible.");
        }

        RecruitmentApp app = new RecruitmentApp();
        app.runMainMenu();
    }

    public void runMainMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n========================================");
            System.out.println("     RECRUITMENT & PLACEMENT SYSTEM     ");
            System.out.println("========================================");
            System.out.println("1. Candidates");
            System.out.println("2. Companies");
            System.out.println("3. Jobs");
            System.out.println("4. Applications");
            System.out.println("5. Interviews");
            System.out.println("6. Offers");
            System.out.println("7. Placements");
            System.out.println("8. Reports");
            System.out.println("9. Exit");

            int choice = InputUtil.readInt("Enter your choice", 1, 9);
            switch (choice) {
                case 1 -> handleCandidateMenu();
                case 2 -> handleCompanyMenu();
                case 3 -> handleJobMenu();
                case 4 -> handleApplicationMenu();
                case 5 -> handleInterviewMenu();
                case 6 -> handleOfferMenu();
                case 7 -> handlePlacementMenu();
                case 8 -> handleReportMenu();
                case 9 -> {
                    System.out.println("\nThank you for using Recruitment & Placement Management System. Goodbye!");
                    exit = true;
                }
            }
        }
    }

    // =========================================================================
    // 1. CANDIDATES MENU
    // =========================================================================
    private void handleCandidateMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- CANDIDATES MENU ---");
            System.out.println("1. Add Candidate");
            System.out.println("2. Update Candidate");
            System.out.println("3. Search Candidate");
            System.out.println("4. View All Candidates");
            System.out.println("5. View Candidate by ID");
            System.out.println("6. Deactivate Candidate");
            System.out.println("7. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 7);
            try {
                switch (choice) {
                    case 1 -> addCandidateFlow();
                    case 2 -> updateCandidateFlow();
                    case 3 -> searchCandidatesFlow();
                    case 4 -> viewAllCandidatesFlow();
                    case 5 -> viewCandidateByIdFlow();
                    case 6 -> deactivateCandidateFlow();
                    case 7 -> back = true;
                }
            } catch (Exception e) {
                printError("Candidate operation failed: " + e.getMessage());
            }
        }
    }

    private void addCandidateFlow() throws ValidationException, SQLException {
        System.out.println("\n>> Add New Candidate");
        Candidate c = new Candidate();
        c.setFullName(InputUtil.readString("Full Name"));
        c.setEmail(InputUtil.readString("Email Address"));
        c.setMobile(InputUtil.readString("Mobile (10 digits)"));
        c.setDateOfBirth(InputUtil.readDate("Date of Birth"));
        c.setGender(InputUtil.readString("Gender (Male/Female/Other)"));
        c.setQualification(InputUtil.readString("Qualification (e.g. B.Tech CSE, MCA)"));
        c.setCollege(InputUtil.readString("College Name"));
        c.setUniversity(InputUtil.readString("University Name"));
        c.setPassingYear(InputUtil.readInt("Passing Year (e.g. 2024)", 1990, 2040));
        c.setPercentage(InputUtil.readDouble("Aggregate Percentage", 0.0, 100.0));
        c.setBacklogCount(InputUtil.readInt("Backlog Count", 0, 50));
        c.setPrimaryTechnology(InputUtil.readString("Primary Technology (e.g. Java, Python, React, Testing)"));
        c.setSecondarySkills(InputUtil.readOptionalString("Secondary Skills (comma-separated)", "None"));
        c.setSkillLevel(InputUtil.readOptionalString("Skill Level (BEGINNER/INTERMEDIATE/EXPERT)", "INTERMEDIATE"));
        c.setCurrentLocation(InputUtil.readString("Current Location"));
        c.setPreferredLocation(InputUtil.readString("Preferred Location"));

        int id = candidateService.registerCandidate(c);
        printSuccess("Candidate successfully registered with ID: CAN" + id);
    }

    private void updateCandidateFlow() throws CandidateNotFoundException, ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Candidate ID to update");
        Candidate c = candidateService.getCandidateById(id);
        System.out.println("Found: " + c);

        c.setFullName(InputUtil.readOptionalString("Full Name", c.getFullName()));
        c.setEmail(InputUtil.readOptionalString("Email Address", c.getEmail()));
        c.setMobile(InputUtil.readOptionalString("Mobile", c.getMobile()));
        c.setQualification(InputUtil.readOptionalString("Qualification", c.getQualification()));
        c.setCollege(InputUtil.readOptionalString("College", c.getCollege()));
        c.setUniversity(InputUtil.readOptionalString("University", c.getUniversity()));
        c.setPassingYear(InputUtil.readInt("Passing Year", 1990, 2040));
        c.setPercentage(InputUtil.readDouble("Percentage", 0.0, 100.0));
        c.setBacklogCount(InputUtil.readInt("Backlogs", 0, 50));
        c.setPrimaryTechnology(InputUtil.readOptionalString("Primary Technology", c.getPrimaryTechnology()));
        c.setSecondarySkills(InputUtil.readOptionalString("Secondary Skills", c.getSecondarySkills()));
        c.setCurrentLocation(InputUtil.readOptionalString("Current Location", c.getCurrentLocation()));
        c.setPreferredLocation(InputUtil.readOptionalString("Preferred Location", c.getPreferredLocation()));

        boolean ok = candidateService.updateCandidate(c);
        if (ok) {
            printSuccess("Candidate profile updated successfully.");
        }
    }

    private void searchCandidatesFlow() throws SQLException {
        String keyword = InputUtil.readString("Enter search keyword (Name/Email/Tech/College)");
        List<Candidate> list = candidateService.searchCandidates(keyword);
        displayCandidatesTable(list);
    }

    private void viewAllCandidatesFlow() throws SQLException {
        List<Candidate> list = candidateService.getAllCandidates();
        displayCandidatesTable(list);
    }

    private void viewCandidateByIdFlow() throws CandidateNotFoundException, SQLException {
        int id = InputUtil.readInt("Enter Candidate ID");
        Candidate c = candidateService.getCandidateById(id);
        System.out.println("\n----------------- CANDIDATE DETAILS -----------------");
        System.out.println("Candidate ID       : CAN" + c.getCandidateId());
        System.out.println("Full Name          : " + c.getFullName());
        System.out.println("Email              : " + c.getEmail());
        System.out.println("Mobile             : " + c.getMobile());
        System.out.println("DOB                : " + c.getDateOfBirth());
        System.out.println("Gender             : " + c.getGender());
        System.out.println("Qualification      : " + c.getQualification());
        System.out.println("College / Univ     : " + c.getCollege() + " / " + c.getUniversity());
        System.out.println("Passing Year       : " + c.getPassingYear());
        System.out.printf("Percentage         : %.2f%%\n", c.getPercentage());
        System.out.println("Backlogs           : " + c.getBacklogCount());
        System.out.println("Primary Tech       : " + c.getPrimaryTechnology());
        System.out.println("Secondary Skills   : " + c.getSecondarySkills());
        System.out.println("Skill Level        : " + c.getSkillLevel());
        System.out.println("Location (Cur/Pref): " + c.getCurrentLocation() + " / " + c.getPreferredLocation());
        System.out.println("Status             : " + c.getStatus());
        System.out.println("-----------------------------------------------------");
    }

    private void deactivateCandidateFlow() throws CandidateNotFoundException, SQLException {
        int id = InputUtil.readInt("Enter Candidate ID to deactivate");
        boolean ok = candidateService.deactivateCandidate(id);
        if (ok) {
            printSuccess("Candidate CAN" + id + " has been marked INACTIVE.");
        }
    }

    private void displayCandidatesTable(List<Candidate> list) {
        if (list.isEmpty()) {
            System.out.println("No candidates found.");
            return;
        }
        System.out.printf("\n%-6s | %-20s | %-12s | %-6s | %-8s | %-5s | %-10s | %-8s\n",
                "ID", "Name", "Technology", "Year", "Pct(%)", "Backl", "Location", "Status");
        System.out.println("-".repeat(95));
        for (Candidate c : list) {
            System.out.printf("CAN%-3d | %-20.20s | %-12.12s | %-6d | %-8.2f | %-5d | %-10.10s | %-8s\n",
                    c.getCandidateId(), c.getFullName(), c.getPrimaryTechnology(), c.getPassingYear(),
                    c.getPercentage(), c.getBacklogCount(), c.getCurrentLocation(), c.getStatus());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 2. COMPANIES MENU
    // =========================================================================
    private void handleCompanyMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- COMPANIES MENU ---");
            System.out.println("1. Add Company");
            System.out.println("2. Update Company");
            System.out.println("3. Search Company");
            System.out.println("4. View All Companies");
            System.out.println("5. View Company Jobs");
            System.out.println("6. Deactivate Company");
            System.out.println("7. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 7);
            try {
                switch (choice) {
                    case 1 -> addCompanyFlow();
                    case 2 -> updateCompanyFlow();
                    case 3 -> searchCompanyFlow();
                    case 4 -> viewAllCompaniesFlow();
                    case 5 -> viewCompanyJobsFlow();
                    case 6 -> deactivateCompanyFlow();
                    case 7 -> back = true;
                }
            } catch (Exception e) {
                printError("Company operation failed: " + e.getMessage());
            }
        }
    }

    private void addCompanyFlow() throws ValidationException, SQLException {
        System.out.println("\n>> Add New Company");
        Company c = new Company();
        c.setCompanyName(InputUtil.readString("Company Name"));
        c.setIndustry(InputUtil.readString("Industry (e.g. IT Services, Consulting, SaaS)"));
        c.setWebsite(InputUtil.readOptionalString("Website URL", ""));
        c.setLocation(InputUtil.readString("HQ Location"));
        c.setHrName(InputUtil.readString("HR Contact Name"));
        c.setHrEmail(InputUtil.readString("HR Email"));
        c.setHrMobile(InputUtil.readString("HR Mobile"));
        c.setCompanyType(InputUtil.readOptionalString("Company Type (MNC/STARTUP/MID_SIZE/CONSULTANCY)", "MNC"));

        int id = companyService.addCompany(c);
        printSuccess("Company added successfully with ID: COM" + id);
    }

    private void updateCompanyFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Company ID to update");
        Company c = companyService.getCompanyById(id);
        System.out.println("Found: " + c);

        c.setCompanyName(InputUtil.readOptionalString("Company Name", c.getCompanyName()));
        c.setIndustry(InputUtil.readOptionalString("Industry", c.getIndustry()));
        c.setWebsite(InputUtil.readOptionalString("Website", c.getWebsite()));
        c.setLocation(InputUtil.readOptionalString("Location", c.getLocation()));
        c.setHrName(InputUtil.readOptionalString("HR Name", c.getHrName()));
        c.setHrEmail(InputUtil.readOptionalString("HR Email", c.getHrEmail()));
        c.setHrMobile(InputUtil.readOptionalString("HR Mobile", c.getHrMobile()));
        c.setCompanyType(InputUtil.readOptionalString("Company Type", c.getCompanyType()));

        boolean ok = companyService.updateCompany(c);
        if (ok) {
            printSuccess("Company details updated successfully.");
        }
    }

    private void searchCompanyFlow() throws SQLException {
        String keyword = InputUtil.readString("Enter search keyword (Name/Industry/Location)");
        List<Company> list = companyService.searchCompanies(keyword);
        displayCompaniesTable(list);
    }

    private void viewAllCompaniesFlow() throws SQLException {
        List<Company> list = companyService.getAllCompanies();
        displayCompaniesTable(list);
    }

    private void viewCompanyJobsFlow() throws SQLException {
        int id = InputUtil.readInt("Enter Company ID");
        List<Job> list = companyService.getJobsByCompany(id);
        displayJobsTable(list);
    }

    private void deactivateCompanyFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Company ID to deactivate");
        boolean ok = companyService.setCompanyStatus(id, "INACTIVE");
        if (ok) {
            printSuccess("Company COM" + id + " marked as INACTIVE.");
        }
    }

    private void displayCompaniesTable(List<Company> list) {
        if (list.isEmpty()) {
            System.out.println("No companies found.");
            return;
        }
        System.out.printf("\n%-6s | %-26s | %-20s | %-12s | %-10s | %-8s\n",
                "ID", "Company Name", "Industry", "Location", "Type", "Status");
        System.out.println("-".repeat(95));
        for (Company c : list) {
            System.out.printf("COM%-3d | %-26.26s | %-20.20s | %-12.12s | %-10.10s | %-8s\n",
                    c.getCompanyId(), c.getCompanyName(), c.getIndustry(), c.getLocation(), c.getCompanyType(), c.getStatus());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 3. JOBS MENU
    // =========================================================================
    private void handleJobMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- JOBS MENU ---");
            System.out.println("1. Create Job");
            System.out.println("2. Update Job");
            System.out.println("3. View All Jobs");
            System.out.println("4. View Open Jobs");
            System.out.println("5. Search Job");
            System.out.println("6. Close Job");
            System.out.println("7. View Jobs by Company");
            System.out.println("8. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 8);
            try {
                switch (choice) {
                    case 1 -> createJobFlow();
                    case 2 -> updateJobFlow();
                    case 3 -> viewAllJobsFlow();
                    case 4 -> viewOpenJobsFlow();
                    case 5 -> searchJobsFlow();
                    case 6 -> closeJobFlow();
                    case 7 -> viewJobsByCompanyFlow();
                    case 8 -> back = true;
                }
            } catch (Exception e) {
                printError("Job operation failed: " + e.getMessage());
            }
        }
    }

    private void createJobFlow() throws ValidationException, SQLException {
        System.out.println("\n>> Create Job Opening");
        Job j = new Job();
        j.setJobCode(InputUtil.readString("Job Code (e.g. JOB-TCS-03)"));
        j.setCompanyId(InputUtil.readInt("Company ID"));
        j.setJobTitle(InputUtil.readString("Job Title"));
        j.setTechnology(InputUtil.readString("Required Technology"));
        j.setOpenings(InputUtil.readInt("Openings Count", 1, 1000));
        j.setLocation(InputUtil.readString("Job Location"));
        j.setMinimumPackage(InputUtil.readDouble("Minimum Package (INR)", 0.0, 100000000.0));
        j.setMaximumPackage(InputUtil.readDouble("Maximum Package (INR)", 0.0, 100000000.0));
        j.setMinimumPercentage(InputUtil.readDouble("Minimum Required Percentage", 0.0, 100.0));
        j.setPassingYear(InputUtil.readInt("Eligible Passing Year", 1990, 2040));
        j.setMaximumBacklogs(InputUtil.readInt("Maximum Allowed Backlogs", 0, 20));
        j.setDeadline(InputUtil.readDate("Application Deadline"));
        j.setEmploymentType(InputUtil.readOptionalString("Employment Type (FULL_TIME/INTERNSHIP)", "FULL_TIME"));

        int id = jobService.createJob(j);
        printSuccess("Job opening created successfully with ID: JOB" + id);
    }

    private void updateJobFlow() throws JobNotFoundException, ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Job ID to update");
        Job j = jobService.getJobById(id);
        System.out.println("Found: " + j);

        j.setJobTitle(InputUtil.readOptionalString("Job Title", j.getJobTitle()));
        j.setTechnology(InputUtil.readOptionalString("Technology", j.getTechnology()));
        j.setOpenings(InputUtil.readInt("Openings", 1, 1000));
        j.setLocation(InputUtil.readOptionalString("Location", j.getLocation()));
        j.setMinimumPackage(InputUtil.readDouble("Min Package", 0.0, 100000000.0));
        j.setMaximumPackage(InputUtil.readDouble("Max Package", 0.0, 100000000.0));
        j.setMinimumPercentage(InputUtil.readDouble("Min Percentage", 0.0, 100.0));
        j.setPassingYear(InputUtil.readInt("Passing Year", 1990, 2040));
        j.setMaximumBacklogs(InputUtil.readInt("Max Backlogs", 0, 20));
        j.setDeadline(InputUtil.readDate("Deadline"));

        boolean ok = jobService.updateJob(j);
        if (ok) {
            printSuccess("Job details updated successfully.");
        }
    }

    private void viewAllJobsFlow() throws SQLException {
        List<Job> list = jobService.getAllJobs();
        displayJobsTable(list);
    }

    private void viewOpenJobsFlow() throws SQLException {
        List<Job> list = jobService.getOpenJobs();
        displayJobsTable(list);
    }

    private void searchJobsFlow() throws SQLException {
        String keyword = InputUtil.readString("Enter search keyword (Code/Title/Tech/Company)");
        List<Job> list = jobService.searchJobs(keyword);
        displayJobsTable(list);
    }

    private void closeJobFlow() throws JobNotFoundException, SQLException {
        int id = InputUtil.readInt("Enter Job ID to close");
        boolean ok = jobService.closeJob(id);
        if (ok) {
            printSuccess("Job JOB" + id + " has been CLOSED.");
        }
    }

    private void viewJobsByCompanyFlow() throws SQLException {
        int compId = InputUtil.readInt("Enter Company ID");
        List<Job> list = jobService.getJobsByCompany(compId);
        displayJobsTable(list);
    }

    private void displayJobsTable(List<Job> list) {
        if (list.isEmpty()) {
            System.out.println("No jobs found.");
            return;
        }
        System.out.printf("\n%-6s | %-12s | %-22s | %-10s | %-5s | %-8s | %-6s | %-12s | %-7s\n",
                "ID", "Code", "Title", "Tech", "Slots", "MinPct", "Year", "Deadline", "Status");
        System.out.println("-".repeat(105));
        for (Job j : list) {
            System.out.printf("JOB%-3d | %-12.12s | %-22.22s | %-10.10s | %-5d | %-8.1f | %-6d | %-12s | %-7s\n",
                    j.getJobId(), j.getJobCode(), j.getJobTitle(), j.getTechnology(),
                    j.getOpenings(), j.getMinimumPercentage(), j.getPassingYear(), j.getDeadline(), j.getStatus());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 4. APPLICATIONS MENU
    // =========================================================================
    private void handleApplicationMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- APPLICATIONS MENU ---");
            System.out.println("1. Apply for Job");
            System.out.println("2. Check Eligibility");
            System.out.println("3. View All Applications");
            System.out.println("4. Search Application");
            System.out.println("5. View Applications by Candidate");
            System.out.println("6. View Applications by Job");
            System.out.println("7. Shortlist Application");
            System.out.println("8. Reject Application");
            System.out.println("9. View Application History");
            System.out.println("10. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 10);
            try {
                switch (choice) {
                    case 1 -> applyForJobFlow();
                    case 2 -> checkEligibilityFlow();
                    case 3 -> viewAllApplicationsFlow();
                    case 4 -> searchApplicationsFlow();
                    case 5 -> viewApplicationsByCandidateFlow();
                    case 6 -> viewApplicationsByJobFlow();
                    case 7 -> shortlistApplicationFlow();
                    case 8 -> rejectApplicationFlow();
                    case 9 -> viewApplicationHistoryFlow();
                    case 10 -> back = true;
                }
            } catch (Exception e) {
                printError("Application operation failed: " + e.getMessage());
            }
        }
    }

    private void applyForJobFlow() throws CandidateNotFoundException, JobNotFoundException,
            DuplicateApplicationException, CandidateNotEligibleException, SQLException {
        int candidateId = InputUtil.readInt("Enter Candidate ID");
        int jobId = InputUtil.readInt("Enter Job ID");

        int appId = applicationService.applyForJob(candidateId, jobId);
        printSuccess("Application submitted successfully! Application ID: APP" + appId);
    }

    private void checkEligibilityFlow() throws CandidateNotFoundException, JobNotFoundException,
            DuplicateApplicationException, SQLException {
        int candidateId = InputUtil.readInt("Enter Candidate ID");
        int jobId = InputUtil.readInt("Enter Job ID");

        EligibilityResult result = applicationService.checkEligibility(candidateId, jobId);
        System.out.println("\n" + "=".repeat(45));
        System.out.println(result);
        System.out.println("=".repeat(45));
    }

    private void viewAllApplicationsFlow() throws SQLException {
        List<Application> list = applicationService.getAllApplications();
        displayApplicationsTable(list);
    }

    private void searchApplicationsFlow() throws SQLException {
        String keyword = InputUtil.readString("Enter search keyword (Candidate/Company/Title/Status)");
        List<Application> list = applicationService.searchApplications(keyword);
        displayApplicationsTable(list);
    }

    private void viewApplicationsByCandidateFlow() throws SQLException {
        int candidateId = InputUtil.readInt("Enter Candidate ID");
        List<Application> list = applicationService.getApplicationsByCandidate(candidateId);
        displayApplicationsTable(list);
    }

    private void viewApplicationsByJobFlow() throws SQLException {
        int jobId = InputUtil.readInt("Enter Job ID");
        List<Application> list = applicationService.getApplicationsByJob(jobId);
        displayApplicationsTable(list);
    }

    private void shortlistApplicationFlow() throws ValidationException, SQLException {
        int appId = InputUtil.readInt("Enter Application ID to shortlist");
        boolean ok = applicationService.shortlistCandidate(appId);
        if (ok) {
            printSuccess("Application APP" + appId + " marked as SHORTLISTED.");
        }
    }

    private void rejectApplicationFlow() throws ValidationException, SQLException {
        int appId = InputUtil.readInt("Enter Application ID to reject");
        boolean ok = applicationService.rejectApplication(appId);
        if (ok) {
            printSuccess("Application APP" + appId + " marked as REJECTED.");
        }
    }

    private void viewApplicationHistoryFlow() throws ValidationException, SQLException {
        int appId = InputUtil.readInt("Enter Application ID");
        Application a = applicationService.getApplicationById(appId);
        System.out.println("\n---------------- APPLICATION HISTORY ----------------");
        System.out.println("Application ID    : APP" + a.getApplicationId());
        System.out.println("Candidate         : " + a.getCandidateName() + " (CAN" + a.getCandidateId() + ")");
        System.out.println("Job Applied       : " + a.getJobTitle() + " (JOB" + a.getJobId() + ")");
        System.out.println("Company           : " + a.getCompanyName());
        System.out.println("Applied Date      : " + a.getAppliedDate());
        System.out.println("Eligibility       : " + a.getEligibilityStatus() + " - " + a.getEligibilityReason());
        System.out.println("Workflow Status   : " + a.getApplicationStatus());
        System.out.println("-----------------------------------------------------");

        // Display associated interviews
        List<Interview> interviews = interviewService.getInterviewsByApplication(appId);
        System.out.println("\nInterviews for this application (" + interviews.size() + "):");
        for (Interview i : interviews) {
            System.out.printf("  * Round: %-15s | Date: %s | Status: %-10s | Result: %s\n",
                    i.getRoundName(), i.getInterviewDate(), i.getInterviewStatus(), i.getResult());
        }
    }

    private void displayApplicationsTable(List<Application> list) {
        if (list.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        System.out.printf("\n%-6s | %-18s | %-22s | %-18s | %-11s | %-11s | %-12s\n",
                "AppID", "Candidate", "Job Title", "Company", "Applied", "Eligible", "Status");
        System.out.println("-".repeat(110));
        for (Application a : list) {
            System.out.printf("APP%-3d | %-18.18s | %-22.22s | %-18.18s | %-11s | %-11.11s | %-12s\n",
                    a.getApplicationId(),
                    (a.getCandidateName() != null ? a.getCandidateName() : "CAN" + a.getCandidateId()),
                    (a.getJobTitle() != null ? a.getJobTitle() : "JOB" + a.getJobId()),
                    (a.getCompanyName() != null ? a.getCompanyName() : "-"),
                    a.getAppliedDate(), a.getEligibilityStatus(), a.getApplicationStatus());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 5. INTERVIEWS MENU
    // =========================================================================
    private void handleInterviewMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- INTERVIEWS MENU ---");
            System.out.println("1. Schedule Interview");
            System.out.println("2. View Today's Interviews");
            System.out.println("3. View Upcoming Interviews");
            System.out.println("4. View Interview History");
            System.out.println("5. Reschedule Interview");
            System.out.println("6. Cancel Interview");
            System.out.println("7. Mark No Show");
            System.out.println("8. Record Interview Feedback");
            System.out.println("9. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 9);
            try {
                switch (choice) {
                    case 1 -> scheduleInterviewFlow();
                    case 2 -> viewTodayInterviewsFlow();
                    case 3 -> viewUpcomingInterviewsFlow();
                    case 4 -> viewInterviewHistoryFlow();
                    case 5 -> rescheduleInterviewFlow();
                    case 6 -> cancelInterviewFlow();
                    case 7 -> markNoShowFlow();
                    case 8 -> recordFeedbackFlow();
                    case 9 -> back = true;
                }
            } catch (Exception e) {
                printError("Interview operation failed: " + e.getMessage());
            }
        }
    }

    private void scheduleInterviewFlow() throws ValidationException, SQLException {
        System.out.println("\n>> Schedule Interview");
        Interview i = new Interview();
        i.setApplicationId(InputUtil.readInt("Application ID"));
        i.setCompanyId(InputUtil.readInt("Company ID"));
        i.setRoundName(InputUtil.readString("Round Name (e.g. Technical Round 1, HR Round)"));
        i.setInterviewDate(InputUtil.readDate("Interview Date"));
        i.setInterviewTime(InputUtil.readString("Interview Time (e.g. 10:30 AM)"));
        i.setInterviewMode(InputUtil.readOptionalString("Mode (ONLINE/OFFLINE/TELEPHONIC)", "ONLINE"));
        i.setInterviewerName(InputUtil.readString("Interviewer Name"));

        int id = interviewService.scheduleInterview(i);
        printSuccess("Interview scheduled successfully with ID: INT" + id);
    }

    private void viewTodayInterviewsFlow() throws SQLException {
        List<Interview> list = interviewService.getTodayInterviews();
        displayInterviewsTable(list);
    }

    private void viewUpcomingInterviewsFlow() throws SQLException {
        List<Interview> list = interviewService.getUpcomingInterviews();
        displayInterviewsTable(list);
    }

    private void viewInterviewHistoryFlow() throws SQLException {
        int appId = InputUtil.readInt("Enter Application ID");
        List<Interview> list = interviewService.getInterviewsByApplication(appId);
        displayInterviewsTable(list);
    }

    private void rescheduleInterviewFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Interview ID to reschedule");
        LocalDate newDate = InputUtil.readDate("New Interview Date");
        String newTime = InputUtil.readString("New Interview Time");

        boolean ok = interviewService.rescheduleInterview(id, newDate, newTime);
        if (ok) {
            printSuccess("Interview rescheduled successfully.");
        }
    }

    private void cancelInterviewFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Interview ID to cancel");
        boolean ok = interviewService.cancelInterview(id);
        if (ok) {
            printSuccess("Interview INT" + id + " has been CANCELLED.");
        }
    }

    private void markNoShowFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Interview ID");
        boolean ok = interviewService.markNoShow(id);
        if (ok) {
            printSuccess("Interview INT" + id + " marked as NO_SHOW.");
        }
    }

    private void recordFeedbackFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Interview ID to record feedback");
        int tech = InputUtil.readInt("Technical Rating (1 to 5)", 1, 5);
        int comm = InputUtil.readInt("Communication Rating (1 to 5)", 1, 5);
        String remarks = InputUtil.readString("Detailed Feedback Remarks");
        System.out.println("Select Result Outcome:");
        System.out.println(" 1. NEXT_ROUND");
        System.out.println(" 2. SELECTED (Candidate cleared drive)");
        System.out.println(" 3. REJECTED");
        System.out.println(" 4. ON_HOLD");
        int outcomeChoice = InputUtil.readInt("Choice", 1, 4);
        String result = switch (outcomeChoice) {
            case 1 -> "NEXT_ROUND";
            case 2 -> "SELECTED";
            case 3 -> "REJECTED";
            case 4 -> "ON_HOLD";
            default -> "ON_HOLD";
        };

        boolean advanceToSelected = false;
        if ("SELECTED".equals(result)) {
            advanceToSelected = InputUtil.readConfirmation("Advance application status to SELECTED?");
        }

        boolean ok = interviewService.recordFeedback(id, tech, comm, remarks, result, advanceToSelected);
        if (ok) {
            printSuccess("Feedback recorded successfully for Interview INT" + id);
        }
    }

    private void displayInterviewsTable(List<Interview> list) {
        if (list.isEmpty()) {
            System.out.println("No interviews found.");
            return;
        }
        System.out.printf("\n%-6s | %-6s | %-16s | %-16s | %-15s | %-11s | %-8s | %-10s | %-10s\n",
                "IntID", "AppID", "Candidate", "Company", "Round", "Date", "Time", "Status", "Result");
        System.out.println("-".repeat(115));
        for (Interview i : list) {
            System.out.printf("INT%-3d | APP%-3d | %-16.16s | %-16.16s | %-15.15s | %-11s | %-8.8s | %-10s | %-10s\n",
                    i.getInterviewId(), i.getApplicationId(),
                    (i.getCandidateName() != null ? i.getCandidateName() : "CAN" + i.getCandidateId()),
                    (i.getCompanyName() != null ? i.getCompanyName() : "COM" + i.getCompanyId()),
                    i.getRoundName(), i.getInterviewDate(), i.getInterviewTime(),
                    i.getInterviewStatus(), i.getResult());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 6. OFFERS MENU
    // =========================================================================
    private void handleOfferMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- OFFERS MENU ---");
            System.out.println("1. Create Offer");
            System.out.println("2. View All Offers");
            System.out.println("3. Search Offer");
            System.out.println("4. Accept Offer");
            System.out.println("5. Reject Offer");
            System.out.println("6. Expire Offer");
            System.out.println("7. Withdraw Offer");
            System.out.println("8. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 8);
            try {
                switch (choice) {
                    case 1 -> createOfferFlow();
                    case 2 -> viewAllOffersFlow();
                    case 3 -> searchOffersFlow();
                    case 4 -> acceptOfferFlow();
                    case 5 -> rejectOfferFlow();
                    case 6 -> expireOfferFlow();
                    case 7 -> withdrawOfferFlow();
                    case 8 -> back = true;
                }
            } catch (Exception e) {
                printError("Offer operation failed: " + e.getMessage());
            }
        }
    }

    private void createOfferFlow() throws ValidationException, SQLException {
        System.out.println("\n>> Create Job Offer");
        Offer o = new Offer();
        o.setApplicationId(InputUtil.readInt("Application ID (must be in SELECTED stage)"));
        o.setCompanyId(InputUtil.readInt("Company ID"));
        o.setDesignation(InputUtil.readString("Designation (e.g. Associate Software Engineer)"));
        o.setOfferedPackage(InputUtil.readDouble("Offered Annual CTC (INR)", 0.0, 100000000.0));
        o.setOfferDate(InputUtil.readOptionalDate("Offer Date", LocalDate.now()));
        o.setJoiningDate(InputUtil.readDate("Expected Joining Date"));

        int id = offerService.createOffer(o);
        printSuccess("Job offer created successfully with ID: OFF" + id);
    }

    private void viewAllOffersFlow() throws SQLException {
        List<Offer> list = offerService.getAllOffers();
        displayOffersTable(list);
    }

    private void searchOffersFlow() throws SQLException {
        String keyword = InputUtil.readString("Enter search keyword (Candidate/Company/Designation/Status)");
        List<Offer> list = offerService.searchOffers(keyword);
        displayOffersTable(list);
    }

    private void acceptOfferFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Offer ID to accept");
        boolean ok = offerService.acceptOffer(id);
        if (ok) {
            printSuccess("Offer OFF" + id + " marked as ACCEPTED by candidate.");
        }
    }

    private void rejectOfferFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Offer ID to reject");
        boolean ok = offerService.rejectOffer(id);
        if (ok) {
            printSuccess("Offer OFF" + id + " marked as REJECTED.");
        }
    }

    private void expireOfferFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Offer ID to expire");
        boolean ok = offerService.expireOffer(id);
        if (ok) {
            printSuccess("Offer OFF" + id + " marked as EXPIRED.");
        }
    }

    private void withdrawOfferFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Offer ID to withdraw");
        boolean ok = offerService.withdrawOffer(id);
        if (ok) {
            printSuccess("Offer OFF" + id + " marked as WITHDRAWN.");
        }
    }

    private void displayOffersTable(List<Offer> list) {
        if (list.isEmpty()) {
            System.out.println("No offers found.");
            return;
        }
        System.out.printf("\n%-6s | %-6s | %-16s | %-16s | %-20s | %-12s | %-11s | %-9s\n",
                "OffID", "AppID", "Candidate", "Company", "Designation", "Package (₹)", "Joining Date", "Status");
        System.out.println("-".repeat(110));
        for (Offer o : list) {
            System.out.printf("OFF%-3d | APP%-3d | %-16.16s | %-16.16s | %-20.20s | %,12.2f | %-12s | %-9s\n",
                    o.getOfferId(), o.getApplicationId(),
                    (o.getCandidateName() != null ? o.getCandidateName() : "CAN" + o.getCandidateId()),
                    (o.getCompanyName() != null ? o.getCompanyName() : "COM" + o.getCompanyId()),
                    o.getDesignation(), o.getOfferedPackage(), o.getJoiningDate(), o.getOfferStatus());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 7. PLACEMENTS MENU
    // =========================================================================
    private void handlePlacementMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- PLACEMENTS MENU ---");
            System.out.println("1. Confirm Joining");
            System.out.println("2. Update Joining Status");
            System.out.println("3. View All Placements");
            System.out.println("4. Search Placement");
            System.out.println("5. View Placed Candidates");
            System.out.println("6. View Unplaced Candidates");
            System.out.println("7. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 7);
            try {
                switch (choice) {
                    case 1 -> confirmJoiningFlow();
                    case 2 -> updateJoiningStatusFlow();
                    case 3 -> viewAllPlacementsFlow();
                    case 4 -> searchPlacementsFlow();
                    case 5 -> viewPlacedCandidatesFlow();
                    case 6 -> viewUnplacedCandidatesFlow();
                    case 7 -> back = true;
                }
            } catch (Exception e) {
                printError("Placement operation failed: " + e.getMessage());
            }
        }
    }

    private void confirmJoiningFlow() throws ValidationException, SQLException {
        System.out.println("\n>> Confirm Joining Process");
        int offerId = InputUtil.readInt("Enter Accepted Offer ID");
        LocalDate actualDate = InputUtil.readOptionalDate("Actual Joining Date", LocalDate.now());
        String status = InputUtil.readOptionalString("Joining Status (JOINED/DEFERRED/NO_SHOW)", "JOINED");

        int id = placementService.confirmJoining(offerId, actualDate, status);
        printSuccess("Placement confirmed successfully with ID: PLC" + id);
    }

    private void updateJoiningStatusFlow() throws ValidationException, SQLException {
        int id = InputUtil.readInt("Enter Placement ID to update");
        String newStatus = InputUtil.readString("New Joining Status (JOINED/NOT_JOINED/DEFERRED/NO_SHOW)");
        boolean ok = placementService.updateJoiningStatus(id, newStatus);
        if (ok) {
            printSuccess("Placement record PLC" + id + " updated to " + newStatus);
        }
    }

    private void viewAllPlacementsFlow() throws SQLException {
        List<Placement> list = placementService.getAllPlacements();
        displayPlacementsTable(list);
    }

    private void searchPlacementsFlow() throws SQLException {
        String keyword = InputUtil.readString("Enter search keyword (Candidate/Company/Designation/Status)");
        List<Placement> list = placementService.searchPlacements(keyword);
        displayPlacementsTable(list);
    }

    private void viewPlacedCandidatesFlow() throws SQLException {
        List<Placement> list = placementService.getPlacedCandidates();
        displayPlacementsTable(list);
    }

    private void viewUnplacedCandidatesFlow() throws SQLException {
        List<Candidate> list = placementService.getUnplacedCandidates();
        displayCandidatesTable(list);
    }

    private void displayPlacementsTable(List<Placement> list) {
        if (list.isEmpty()) {
            System.out.println("No placement records found.");
            return;
        }
        System.out.printf("\n%-6s | %-6s | %-18s | %-18s | %-20s | %-12s | %-11s | %-9s\n",
                "PlcID", "OffID", "Candidate", "Company", "Designation", "Final CTC(₹)", "Joined Date", "Status");
        System.out.println("-".repeat(115));
        for (Placement p : list) {
            System.out.printf("PLC%-3d | OFF%-3d | %-18.18s | %-18.18s | %-20.20s | %,12.2f | %-11s | %-9s\n",
                    p.getPlacementId(), p.getOfferId(),
                    (p.getCandidateName() != null ? p.getCandidateName() : "CAN" + p.getCandidateId()),
                    (p.getCompanyName() != null ? p.getCompanyName() : "COM" + p.getCompanyId()),
                    p.getDesignation(), p.getFinalPackage(), p.getActualJoiningDate(), p.getJoiningStatus());
        }
        System.out.println("Total: " + list.size() + " record(s).");
    }

    // =========================================================================
    // 8. REPORTS MENU
    // =========================================================================
    private void handleReportMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- REPORTS & ANALYTICS MENU ---");
            System.out.println("1. Candidates by Technology");
            System.out.println("2. Open Jobs");
            System.out.println("3. Applications by Status");
            System.out.println("4. Company-Wise Placements");
            System.out.println("5. Placed Candidates");
            System.out.println("6. Unplaced Candidates");
            System.out.println("7. Back to Main Menu");

            int choice = InputUtil.readInt("Enter your choice", 1, 7);
            try {
                switch (choice) {
                    case 1 -> reportCandidatesByTech();
                    case 2 -> reportOpenJobs();
                    case 3 -> reportApplicationsByStatus();
                    case 4 -> reportCompanyWisePlacements();
                    case 5 -> reportPlacedCandidates();
                    case 6 -> reportUnplacedCandidates();
                    case 7 -> back = true;
                }
            } catch (Exception e) {
                printError("Report generation failed: " + e.getMessage());
            }
        }
    }

    private void reportCandidatesByTech() throws SQLException {
        Map<String, Integer> map = reportService.getCandidatesByTechnology();
        System.out.println("\n========================================");
        System.out.println("     REPORT 1: CANDIDATES BY TECHNOLOGY ");
        System.out.println("========================================");
        int total = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.printf("%-18s : %4d\n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("----------------------------------------");
        System.out.printf("%-18s : %4d\n", "Total Candidates", total);
    }

    private void reportOpenJobs() throws SQLException {
        List<Job> jobs = reportService.getOpenJobs();
        System.out.println("\n=================================================================================");
        System.out.println("                      REPORT 2: CURRENTLY OPEN JOBS                              ");
        System.out.println("=================================================================================");
        displayJobsTable(jobs);
    }

    private void reportApplicationsByStatus() throws SQLException {
        Map<String, Integer> map = reportService.getApplicationsByStatus();
        System.out.println("\n========================================");
        System.out.println("   REPORT 3: APPLICATIONS BY STATUS     ");
        System.out.println("========================================");
        int total = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.printf("%-18s : %4d\n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("----------------------------------------");
        System.out.printf("%-18s : %4d\n", "Total Applications", total);
    }

    private void reportCompanyWisePlacements() throws SQLException {
        Map<String, Integer> map = reportService.getCompanyWisePlacements();
        System.out.println("\n========================================");
        System.out.println("  REPORT 4: COMPANY-WISE PLACEMENTS     ");
        System.out.println("========================================");
        int total = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.printf("%-30s : %3d\n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("----------------------------------------");
        System.out.printf("%-30s : %3d\n", "Total Placed Students", total);
    }

    private void reportPlacedCandidates() throws SQLException {
        List<Placement> placed = reportService.getPlacedCandidates();
        System.out.println("\n================================================================================================");
        System.out.println("                              REPORT 5: PLACED CANDIDATES                                       ");
        System.out.println("================================================================================================");
        displayPlacementsTable(placed);
    }

    private void reportUnplacedCandidates() throws SQLException {
        List<Candidate> unplaced = reportService.getUnplacedCandidates();
        System.out.println("\n================================================================================================");
        System.out.println("                             REPORT 6: UNPLACED CANDIDATES                                      ");
        System.out.println("================================================================================================");
        displayCandidatesTable(unplaced);
    }

    // =========================================================================
    // HELPER UI METHODS
    // =========================================================================
    private void printSuccess(String message) {
        System.out.println("\n[SUCCESS] " + message);
    }

    private void printError(String message) {
        System.out.println("\n[ERROR] " + message);
    }
}

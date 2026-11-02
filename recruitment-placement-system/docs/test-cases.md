# Test Cases Specification - RPMS (rpms_db)

This document details comprehensive test scenarios, input data, and expected outcomes across all modules of the **Recruitment & Placement Management System** configured for `rpms_db` (17 MySQL 8.x tables).

---

## 1. Candidate Management Test Cases

| TC ID | Test Scenario | Steps | Input Data | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-CAN-01** | Add a valid candidate | Menu 1 -> 1 (Add Candidate) | Code: 'CAN-2026-099', Name: 'Aditi Roy', Email: 'aditi.r@test.com', Mobile: '9811223344', DOB: '2002-06-15', Tech: 'Java', Pct: 82.5, Backlogs: 0, Year: 2024 | Candidate record created in `candidates` table with code, status `ACTIVE`, placement `NOT_PLACED`. Logged in `activity_logs` and `activity_log.txt`. |
| **TC-CAN-02** | Reject duplicate email | Attempt to register candidate with already existing email | Email: 'aditi.r@test.com' | Rejected with `ValidationException`: "Error: Email address already exists." |
| **TC-CAN-03** | Reject duplicate mobile | Attempt to register candidate with already existing mobile | Mobile: '9811223344' | Rejected with `ValidationException`: "Error: Mobile number already exists." |
| **TC-CAN-04** | Reject percentage below 0 | Register candidate with percentage < 0 | Percentage: -5.0 | Rejected with `ValidationException`: "Error: Percentage must be between 0 and 100." |
| **TC-CAN-05** | Reject percentage above 100 | Register candidate with percentage > 100 | Percentage: 104.5 | Rejected with `ValidationException`: "Error: Percentage must be between 0 and 100." |
| **TC-CAN-06** | Reject negative backlogs | Register candidate with negative backlogs | Backlog count: -1 | Rejected with `ValidationException`: "Error: Backlog count cannot be negative." |
| **TC-CAN-07** | Update existing candidate | Menu 1 -> 2 | Candidate ID: 1, New Location: 'Bangalore' | Profile updated in database and logged in `activity_logs` & `activity_log.txt`. |
| **TC-CAN-08** | Handle nonexistent candidate ID | Menu 1 -> 5 (View by ID) | Candidate ID: 99999 | Caught `CandidateNotFoundException`: "Candidate with ID 99999 not found". |
| **TC-CAN-09** | Deactivate candidate | Menu 1 -> 6 (Deactivate) | Candidate ID: 1 | Profile status set to `INACTIVE`. Logged in `activity_logs`. Placement and historical applications preserved. |

---

## 2. Company Management Test Cases

| TC ID | Test Scenario | Steps | Input Data | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-COM-01** | Create a valid company | Menu 2 -> 1 (Add Company) | Code: 'COM-NEX-01', Name: 'NexGen Infotech', HR: 'Sanjay Deshmukh', Email: 'hr@nexgen.com', Mobile: '9822334455', Type: 'MNC' | Company saved in `companies` table with unique code and status `ACTIVE`. Logged in `activity_logs`. |
| **TC-COM-02** | Reject duplicate company name | Attempt to add company with existing name | Name: 'NexGen Infotech' | Rejected with `ValidationException`: "Error: Company with name ... already exists." |
| **TC-COM-03** | Search company by keyword | Menu 2 -> 3 | Keyword: 'TCS' | Returns Tata Consultancy Services record with code `COM-TCS-01`. |
| **TC-COM-04** | Prevent job creation for inactive company | Menu 3 -> 1 with Inactive company | Inactive Company ID | Rejected: "Error: Cannot create job for inactive company." |

---

## 3. Job Management Test Cases

| TC ID | Test Scenario | Steps | Input Data | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-JOB-01** | Create a valid job | Menu 3 -> 1 | Code: 'JOB-NEX-01', CompID: 1, Title: 'Java Associate', Openings: 10, MinPkg: 4.50, MaxPkg: 7.00, MinPct: 65, MinYear: 2024, MaxYear: 2024, Deadline: '2026-12-31' | Job opening created in `jobs` with status `OPEN`. Logged in `activity_logs`. |
| **TC-JOB-02** | Reject min package > max package | Enter Min Pkg = 8.00 LPA, Max Pkg = 5.00 LPA | Min > Max | Rejected: "Error: Minimum package cannot exceed maximum package." |
| **TC-JOB-03** | Reject zero or negative openings | Enter Openings = 0 or -3 | Openings: 0 | Rejected: "Error: Openings must be greater than zero." |
| **TC-JOB-04** | Close job | Menu 3 -> 6 | Job ID: 1 | Status changed to `CLOSED`. Logged in `activity_logs`. |
| **TC-JOB-05** | Prevent application to closed job | Apply for a closed job | Job ID with `CLOSED` status | Eligibility check fails: "Job is not open for applications (status: CLOSED)." |

---

## 4. Eligibility Check & Application Test Cases

| TC ID | Test Scenario | Steps | Input Data | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-APP-01** | Apply with eligible candidate | Menu 4 -> 1 | Eligible candidate ID and open job ID | Returns ELIGIBLE, creates application in `applications` (`application_status = 'APPLIED'`), creates initial record in `application_history`, and logs event in `activity_logs`. |
| **TC-APP-02** | Reject inactive candidate | Menu 4 -> 1 | Candidate with `INACTIVE` profile status | Fails with `CandidateNotEligibleException`: "Candidate profile is not active (status: INACTIVE)." |
| **TC-APP-03** | Reject percentage below threshold | Candidate pct: 58%, Job min pct: 65% | Ineligible candidate ID | Fails: "Candidate percentage (58.00%) is below job requirement (65.00%)." |
| **TC-APP-04** | Reject passing year outside range | Candidate year: 2023, Job range: 2024-2025 | Candidate ID | Fails: "Candidate passing year (2023) is earlier than minimum allowed (2024)." |
| **TC-APP-05** | Reject excessive backlogs | Candidate backlogs: 2, Job max: 0 | Candidate ID | Fails: "Candidate backlogs (2) exceed maximum allowed (0)." |
| **TC-APP-06** | Reject expired deadline | Job deadline is in the past | Expired Job ID | Fails: "Job application deadline expired on ..." |
| **TC-APP-07** | Reject duplicate application | Candidate applies to same job twice | Same candidateId & jobId | Throws `DuplicateApplicationException`: "Candidate ... has already applied for this job." |
| **TC-APP-08** | Reject invalid workflow transition | Attempt to change APPLIED directly to JOINED | App in `APPLIED` status | Throws `InvalidWorkflowTransitionException`: "Invalid recruitment status transition: cannot move directly from 'APPLIED' to 'JOINED'." |

---

## 5. Interview Management Test Cases

| TC ID | Test Scenario | Steps | Input Data | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-INT-01** | Schedule interview for shortlisted application | Menu 5 -> 1 | App in `SHORTLISTED` status | Interview round inserted in `interview_rounds` table with auto round number. Application status moved to `INTERVIEW_SCHEDULED`. Logged in `activity_logs`. |
| **TC-INT-02** | Reject interview for unshortlisted candidate | Menu 5 -> 1 | App in `APPLIED` or `REJECTED` status | Rejected with `ValidationException`: "Application status must be SHORTLISTED or INTERVIEWED." |
| **TC-INT-03** | Reject rating outside permitted 0-5 range | Feedback ratings: Tech = 6.0, Comm = -1.0 | Tech = 6.0 | Rejected: "Error: Technical rating must be between 0 and 5." |
| **TC-INT-04** | Record interview feedback & select candidate | Menu 5 -> 8 | Recommendation: SELECTED, Advance: Yes | Record inserted in `interview_feedback`. Round updated to `COMPLETED`. Application status updated to `SELECTED`. Logged in `activity_logs`. |

---

## 6. Offer & Placement Test Cases

| TC ID | Test Scenario | Steps | Input Data | Expected Result |
| :--- | :--- | :--- | :--- | :--- |
| **TC-OFF-01** | Create offer for selected application | Menu 6 -> 1 | App in `SELECTED` status | Transaction succeeds: Offer record inserted in `offers`, application status atomically updated to `OFFERED`, history recorded. |
| **TC-OFF-02** | Reject offer for unselected application | Menu 6 -> 1 | App in `SHORTLISTED` or `INTERVIEW_SCHEDULED` status | Rejected: "Application must be in 'SELECTED' stage." |
| **TC-OFF-03** | Accept offer | Menu 6 -> 4 | Offer ID | Offer status updated to `ACCEPTED`. Logged in `activity_logs`. |
| **TC-PLC-01** | Confirm joining for accepted offer | Menu 7 -> 1 | Accepted Offer ID, Joining Date | Transaction succeeds: Record inserted in `placements`, application status updated to `JOINED`, candidate `placement_status` updated to `PLACED`, history recorded. |
| **TC-PLC-02** | Reject joining confirmation for unaccepted offer | Menu 7 -> 1 | Offer in `OFFERED` or `REJECTED` status | Rejected: "Offer must be in 'ACCEPTED' status before joining confirmation." |
| **TC-PLC-03** | Prevent duplicate placement record | Attempt to confirm joining twice for same offer | Same Offer ID | Rejected: "Placement record already exists for offer ID..." |

---

## 7. Reports Test Cases

| TC ID | Report | Expected Behavior |
| :--- | :--- | :--- |
| **TC-REP-01** | Report 1: Candidates by Technology | Groups candidates by `primary_technology` with aggregate counts from `candidates` table. |
| **TC-REP-02** | Report 2: Open Jobs | Lists all jobs from `jobs` where `status = 'OPEN'` and `application_deadline >= CURDATE()`. |
| **TC-REP-03** | Report 3: Applications by Status | Displays counts for all 10 RPMS application statuses (APPLIED, ELIGIBILITY_VERIFIED, SHORTLISTED, INTERVIEW_SCHEDULED, INTERVIEWED, SELECTED, OFFERED, JOINED, REJECTED, WITHDRAWN). |
| **TC-REP-04** | Report 4: Company-Wise Placements | Displays total number of candidates who have confirmed `JOINED` status grouped by company. |
| **TC-REP-05** | Report 5: Placed Candidates | Lists only candidates who have confirmed placement records with `joining_status = 'JOINED'` and `placement_status = 'ACTIVE'`. |
| **TC-REP-06** | Report 6: Unplaced Candidates | Lists all active candidates with `placement_status = 'NOT_PLACED'` or no confirmed joining record. |
| **TC-REP-07** | Additional Report: Skill Distribution | Aggregates counts across normalized `skills` and `candidate_skills` tables. |
| **TC-REP-08** | Additional Report: Jobs by Company | Summarizes job postings count per registered company. |
| **TC-REP-09** | Additional Report: Offers by Status | Displays counts of offers by OFFERED, ACCEPTED, REJECTED, EXPIRED, WITHDRAWN. |
| **TC-REP-10** | Additional Report: Joining Status Summary | Displays breakdown of placements across PENDING, JOINED, NOT_JOINED, DEFERRED, NO_SHOW. |

# Recruitment & Placement Management System (RPMS)

A robust, enterprise-grade backend developed in **Core Java 17, JDBC, and MySQL 8.x**, strictly built against the authoritative 17-table `rpms_db` database schema and sample data.

---

## Table of Contents
1. [Project Overview](#1-project-overview)
2. [Project Objective](#2-project-objective)
3. [Technology Stack](#3-technology-stack)
4. [Architecture & Design](#4-architecture--design)
5. [Project & Package Structure](#5-project--package-structure)
6. [Database Schema (17 Tables)](#6-database-schema-17-tables)
7. [Database Setup & Warnings](#7-database-setup--warnings)
8. [Database Credential Configuration](#8-database-credential-configuration)
9. [Build & Execution Instructions](#9-build--execution-instructions)
10. [Console Menu & Submenus](#10-console-menu--submenus)
11. [Candidate Eligibility Engine](#11-candidate-eligibility-engine)
12. [Recruitment Workflow Lifecycle](#12-recruitment-workflow-lifecycle)
13. [Transaction Management](#13-transaction-management)
14. [Activity Logging (Database & File)](#14-activity-logging-database--file)
15. [Analytical Reports](#15-analytical-reports)
16. [Custom Exceptions](#16-custom-exceptions)
17. [Testing Instructions](#17-testing-instructions)
18. [Troubleshooting](#18-troubleshooting)

---

## 1. Project Overview

The **Recruitment & Placement Management System (RPMS)** is an end-to-end recruitment lifecycle system designed for university placement departments, recruitment firms, and corporate hiring teams. It automates candidate profile management, corporate employer drives, opening criteria, multi-stage eligibility verification, interview round scheduling and scoring, formal compensation offers, and confirmed placement joinings.

---

## 2. Project Objective

- **Data Integrity**: Enforce foreign keys, unique constraints, and check constraints across all 17 tables in `rpms_db`.
- **Pure JDBC Backend**: Utilize standard Java `Connection`, `PreparedStatement`, `ResultSet`, and manual JDBC transaction management without ORM overhead.
- **Strict Eligibility Verification**: Eliminate manual errors by automating checks for academic cutoffs, passing year ranges, maximum backlogs, qualification rules, and mandatory skills.
- **Auditable Workflow**: Centralize application state progression across 10 defined lifecycle statuses and record all historical transitions in `application_history`.
- **Dual Audit Trail**: Persist critical system actions into both the `activity_logs` MySQL table and the append-only `activity_log.txt` flat file.
- **Management Intelligence**: Deliver real-time aggregated reports for technology distribution, active campus drives, company placement counts, and unplaced candidates.

---

## 3. Technology Stack

- **Platform**: Java SE 17 (OpenJDK 17 / Oracle JDK 17)
- **Database**: MySQL Server 8.0+ / 8.3+
- **Database Driver**: `com.mysql:mysql-connector-j:8.3.0`
- **Database Access**: Direct JDBC (`java.sql.*`) using `PreparedStatement` and parameterized queries
- **Design Architecture**: Layered Enterprise Architecture (App → Service → DAO → Model / Util)
- **Unit Testing**: JUnit 5 Jupiter (`org.junit.jupiter:junit-jupiter:5.10.2`)
- **Build Tool**: Apache Maven (`pom.xml`)

> **Architectural Constraint Compliance**:  
> In accordance with project requirements, this system strictly uses pure JDBC and Java SE. It does **not** employ Spring Boot, Spring Framework, Hibernate, JPA, Servlets, JSP, or REST APIs.

---

## 4. Architecture & Design

```text
┌─────────────────────────────────────────────────────────────┐
│                 Console Application (App)                   │
│          com.recruitment.app.RecruitmentApp                 │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer (12)                       │
│  CandidateService, JobService, ApplicationService,          │
│  EligibilityService, InterviewService, OfferService,         │
│  PlacementService, CompanyService, UserService, etc.        │
└──────────────────────────────┬──────────────────────────────┘
                               │ (ACID Transactions & Validations)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                      DAO Layer (18)                         │
│  CandidateDAO, JobDAO, ApplicationDAO, InterviewDAO,        │
│  OfferDAO, PlacementDAO, ActivityLogDAO, ReportDAO, etc.    │
└──────────────────────────────┬──────────────────────────────┘
                               │ (PreparedStatement / Try-with-resources)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 MySQL 8.x Database (rpms_db)                │
│                 17 Tables with Relational Constraints       │
└─────────────────────────────────────────────────────────────┘
```

1. **App Layer (`com.recruitment.app`)**: Handles console menus, user input reading, error display, and formatted tabular outputs.
2. **Service Layer (`com.recruitment.service`)**: Encapsulates business logic, rule evaluations, input validations, transaction demarcation (`commit()` / `rollback()`), and activity logging.
3. **DAO Layer (`com.recruitment.dao`)**: Manages SQL query execution, mapping `ResultSet` rows into domain models, and resource cleanup with `try-with-resources`.
4. **Model Layer (`com.recruitment.model`)**: Encapsulates database entity POJOs using appropriate Java types (`LocalDate`, `LocalDateTime`, `BigDecimal`, etc.).
5. **Util Layer (`com.recruitment.util`)**: Provides centralized database connection management (`DBConnection`), console input parsing (`InputUtil`), and file logging (`ActivityLogger`).
6. **Exception Layer (`com.recruitment.exception`)**: Provides domain-specific checked and runtime exceptions.

---

## 5. Project & Package Structure

```text
recruitment-placement-system/
│
├── pom.xml                                     # Maven build and dependency descriptor
├── README.md                                   # Complete system technical documentation
├── activity_log.txt                            # Append-only text audit trail
│
├── database/
│   ├── rpms_schema.sql                         # Authoritative 17-table DDL schema
│   ├── rpms_sample_data.sql                    # Realistic sample data for all 17 tables
│   ├── rpms_full_setup.sql                     # Combined DDL and DML setup script
│   └── er-diagram.md                           # Mermaid ER diagram & foreign-key dictionary
│
├── docs/
│   ├── test-cases.md                           # Comprehensive test matrix and edge cases
│   └── sample-output.md                        # Sample console execution transcripts
│
└── src/
    ├── main/
    │   ├── resources/
    │   │   └── db.properties                   # MySQL connection credentials configuration
    │   └── java/
    │       └── com/
    │           └── recruitment/
    │               ├── app/
    │               │   └── RecruitmentApp.java # Interactive 9-item console application
    │               ├── model/                  # 17 Entity Models + Helpers
    │               │   ├── Qualification.java
    │               │   ├── Location.java
    │               │   ├── Role.java
    │               │   ├── User.java
    │               │   ├── Candidate.java
    │               │   ├── Skill.java
    │               │   ├── CandidateSkill.java
    │               │   ├── Company.java
    │               │   ├── Job.java
    │               │   ├── JobSkill.java
    │               │   ├── Application.java
    │               │   ├── ApplicationHistory.java
    │               │   ├── InterviewRound.java
    │               │   ├── InterviewFeedback.java
    │               │   ├── Interview.java      # Combined compatibility model
    │               │   ├── Offer.java
    │               │   ├── Placement.java
    │               │   ├── ActivityLog.java
    │               │   └── EligibilityResult.java
    │               ├── dao/                    # 18 Data Access Objects
    │               │   ├── QualificationDAO.java
    │               │   ├── LocationDAO.java
    │               │   ├── RoleDAO.java
    │               │   ├── UserDAO.java
    │               │   ├── CandidateDAO.java
    │               │   ├── SkillDAO.java
    │               │   ├── CandidateSkillDAO.java
    │               │   ├── CompanyDAO.java
    │               │   ├── JobDAO.java
    │               │   ├── JobSkillDAO.java
    │               │   ├── ApplicationDAO.java
    │               │   ├── ApplicationHistoryDAO.java
    │               │   ├── InterviewDAO.java
    │               │   ├── InterviewFeedbackDAO.java
    │               │   ├── OfferDAO.java
    │               │   ├── PlacementDAO.java
    │               │   ├── ActivityLogDAO.java
    │               │   └── ReportDAO.java
    │               ├── service/                # 12 Business & Workflow Services
    │               │   ├── UserService.java
    │               │   ├── CandidateService.java
    │               │   ├── SkillService.java
    │               │   ├── CompanyService.java
    │               │   ├── JobService.java
    │               │   ├── EligibilityService.java
    │               │   ├── ApplicationService.java
    │               │   ├── InterviewService.java
    │               │   ├── OfferService.java
    │               │   ├── PlacementService.java
    │               │   ├── ActivityLogService.java
    │               │   └── ReportService.java
    │               ├── util/                   # Utilities
    │               │   ├── DBConnection.java
    │               │   ├── InputUtil.java
    │               │   └── ActivityLogger.java
    │               └── exception/              # Custom Exceptions
    │                   ├── CandidateNotFoundException.java
    │                   ├── JobNotFoundException.java
    │                   ├── DuplicateApplicationException.java
    │                   ├── CandidateNotEligibleException.java
    │                   ├── InvalidWorkflowTransitionException.java
    │                   └── ValidationException.java
    │
    └── test/
        └── java/
            └── com/
                └── recruitment/
                    └── service/
                        ├── CandidateServiceTest.java
                        ├── JobServiceTest.java
                        ├── ApplicationServiceTest.java
                        ├── InterviewServiceTest.java
                        └── OfferAndPlacementServiceTest.java
```

---

## 6. Database Schema (17 Tables)

The authoritative database is named **`rpms_db`** with the following 17 tables:

| # | Table Name | Purpose & Key Columns |
|---|---|---|
| 1 | `qualifications` | Master qualification list (`qualification_id`, `qualification_name`, `status`). |
| 2 | `locations` | Geographic master records (`location_id`, `city_name`, `state_name`, `country_name`). |
| 3 | `roles` | System authorization roles (`role_id`, `role_code`, `role_name`, `description`). |
| 4 | `users` | System user credentials (`user_id`, `username`, `password_hash`, `email`, `role_id`, `status`). |
| 5 | `candidates` | Candidate profiles (`candidate_id`, `candidate_code`, `email`, `mobile`, `percentage`, `cgpa`, `passing_year`, `backlog_count`, `primary_technology`, `profile_status`, `placement_status`). |
| 6 | `skills` | Master skill library (`skill_id`, `skill_name`, `category`, `status`). |
| 7 | `candidate_skills` | Candidate technical proficiencies (`candidate_id`, `skill_id`, `proficiency`, `years_of_experience`). |
| 8 | `companies` | Corporate recruitment partners (`company_id`, `company_code`, `company_name`, `industry`, `hr_email`, `status`). |
| 9 | `jobs` | Campus drive openings (`job_id`, `job_code`, `company_id`, `min_package_lpa`, `max_package_lpa`, `min_percentage`, `min_passing_year`, `max_passing_year`, `max_backlogs`, `deadline`, `status`). |
| 10 | `job_skills` | Mandatory & optional job skill requirements (`job_id`, `skill_id`, `is_mandatory`, `min_proficiency`). |
| 11 | `applications` | Candidate job applications (`application_id`, `application_code`, `candidate_id`, `job_id`, `eligibility_status`, `application_status`). Unique on `(candidate_id, job_id)`. |
| 12 | `application_history` | Audit log of application transitions (`history_id`, `application_id`, `old_status`, `new_status`, `remarks`, `changed_by`). |
| 13 | `interview_rounds` | Scheduled interview rounds (`interview_id`, `application_id`, `round_number`, `round_type`, `scheduled_date`, `mode`, `status`). |
| 14 | `interview_feedback` | Candidate scoring per round (`feedback_id`, `interview_id`, `technical_rating`, `communication_rating`, `problem_solving_rating`, `recommendation`). |
| 15 | `offers` | Official employment offers (`offer_id`, `offer_code`, `application_id`, `designation`, `offered_package_lpa`, `expected_joining_date`, `offer_status`). |
| 16 | `placements` | Confirmed candidate joinings (`placement_id`, `candidate_id`, `company_id`, `job_id`, `offer_id`, `final_package_lpa`, `actual_joining_date`, `joining_status`, `placement_status`). |
| 17 | `activity_logs` | System transaction audit trail (`log_id`, `user_id`, `module_name`, `action_name`, `record_id`, `description`, `logged_at`). |

---

## 7. Database Setup & Warnings

> [!WARNING]
> **DESTRUCTIVE OPERATION WARNING**:  
> Both `rpms_schema.sql` and `rpms_full_setup.sql` contain `DROP DATABASE IF EXISTS rpms_db;`. Executing either script will erase any existing `rpms_db` database and recreate all tables. Never execute these scripts against a production database without explicit authorization and a recent backup.

### Standard Setup Sequence
To initialize `rpms_db` on your local MySQL server:

```bash
# Option 1: Execute full setup file
mysql -u root -p < database/rpms_full_setup.sql

# Option 2: Execute schema followed by sample data
mysql -u root -p < database/rpms_schema.sql
mysql -u root -p < database/rpms_sample_data.sql
```

Alternatively, from the MySQL interactive shell:
```sql
SOURCE /home/pratik/Music/job requirment/recruitment-placement-system/database/rpms_full_setup.sql;
```

---

## 8. Database Credential Configuration

Database connection settings are loaded dynamically by `DBConnection.java`. Default properties are located in `src/main/resources/db.properties`:

```properties
db.host=localhost
db.port=3306
db.name=rpms_db
db.user=root
db.password=root
```

### Environment Variable Overrides
You can override any database property at runtime without altering source code:

| Environment Variable | Default Value | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL hostname or IP address |
| `DB_PORT` | `3306` | MySQL TCP port |
| `DB_NAME` | `rpms_db` | Authoritative database name |
| `DB_USER` | `root` | Database user account |
| `DB_PASSWORD` | `root` | Database user password |

Example:
```bash
export DB_NAME=rpms_db
export DB_USER=my_user
export DB_PASSWORD=my_secret_password
```

---

## 9. Build & Execution Instructions

### Prerequisites
- **JDK 17** (`javac -version`)
- **MySQL 8.0+** running on port 3306
- Optional: **Apache Maven 3.8+**

### Running the Application

#### Option 1: Web UI Mode (Browser Interface on http://localhost:8080/)
To run the full graphical web portal with live MySQL data:
```bash
cd "/home/pratik/Music/job requirment"
./run-web.sh
```
Then navigate to **`http://localhost:8080/`** in your browser.  
This uses Java 17's built-in `HttpServer` (zero external frameworks) to serve the UI and live REST endpoints connected directly to `rpms_db`.

#### Option 2: Console Application Mode (Terminal Menu)
To run the interactive terminal console application:
```bash
cd "/home/pratik/Music/job requirment"
./run.sh
```

#### Option 3: Running via Maven
```bash
cd "/home/pratik/Music/job requirment/recruitment-placement-system"
mvn exec:java
```

---

## 10. Console Menu & Submenus

The application renders a standard 9-item interactive main menu:

```text
========================================
     RECRUITMENT & PLACEMENT SYSTEM     
========================================
1. Candidates
2. Companies
3. Jobs
4. Applications
5. Interviews
6. Offers
7. Placements
8. Reports
9. Exit
```

### Module Submenus
- **1. Candidates**:
  - `Add Candidate`: Collects academic profile, generates `candidate_code` (e.g. `CAN-2026-0001`), validates email, mobile, and percentage.
  - `Update Candidate`: Updates contact details, qualifications, technology, and preferences.
  - `Search Candidate`: Searches by name, email, technology, or college.
  - `View All Candidates`: Displays formatted tabular candidate records.
  - `View Candidate by ID`: Displays comprehensive profile, backlogs, percentage, and status.
  - `Deactivate Candidate`: Soft-deactivates profile (`profile_status = 'INACTIVE'`).
- **2. Companies**:
  - `Add Company`: Registers corporate employer with auto-generated `company_code`.
  - `Update Company`: Modifies HR contact information and industry category.
  - `Search Company`: Searches by name, industry, or location.
  - `View All Companies`: Tabulates active and inactive employers.
  - `View Company Jobs`: Lists all openings posted by a specific employer.
  - `Deactivate Company`: Updates status to `INACTIVE`.
- **3. Jobs**:
  - `Create Job`: Posts job drive with salary package range (`min_package_lpa` to `max_package_lpa`), passing year range, percentage cutoff, and deadline.
  - `Update Job`: Updates openings, package, and cutoffs.
  - `View All Jobs` / `View Open Jobs`: Displays drive status and criteria.
  - `Search Job`: Filters by code, title, technology, or company.
  - `Close Job`: Updates job status to `CLOSED`.
- **4. Applications**:
  - `Apply for Job`: Runs automated eligibility checks, prevents duplicate applications, records initial application history, and logs activity within a JDBC transaction.
  - `Check Eligibility`: Pre-application dry-run evaluation detailing all passing/failing criteria.
  - `View All Applications`: Displays candidate, job, eligibility, and workflow statuses.
  - `Shortlist Application`: Moves application from `ELIGIBILITY_VERIFIED` to `SHORTLISTED`.
  - `Reject Application`: Marks application `REJECTED`.
  - `View Application History`: Displays complete timestamped audit trail of status changes.
- **5. Interviews**:
  - `Schedule Interview`: Schedules round in `interview_rounds` and advances application status to `INTERVIEW_SCHEDULED`.
  - `View Today's / Upcoming Interviews`: Filters scheduled drives by date.
  - `Reschedule / Cancel / Mark No-Show`: Updates round status.
  - `Record Feedback`: Records technical, communication, and overall ratings (0.0 to 5.0) and advances status to `SELECTED` when candidate clears the drive.
- **6. Offers**:
  - `Create Offer`: Generates formal job offer for `SELECTED` application and advances status to `OFFERED`.
  - `Accept / Reject / Expire / Withdraw Offer`: Updates offer status within transactions.
- **7. Placements**:
  - `Confirm Joining`: Verifies accepted offer, inserts into `placements`, updates `applications.application_status = 'JOINED'`, and sets `candidates.placement_status = 'PLACED'` inside an ACID transaction.
  - `Update Joining Status`: Handles `JOINED`, `NOT_JOINED`, `DEFERRED`, `NO_SHOW`.
  - `View Placed / Unplaced Candidates`: Distinguishes confirmed joinings from ongoing candidates.
- **8. Reports**:
  - Renders all 6 core reports plus additional analytical summaries.

---

## 11. Candidate Eligibility Engine

Implemented in `com.recruitment.service.EligibilityService`, the engine evaluates 11 distinct criteria before allowing an application:

1. **Candidate Existence**: Candidate ID must exist in `candidates`.
2. **Active Profile**: Candidate `profile_status` must be `ACTIVE` (rejects `INACTIVE`, `ON_HOLD`, `BLACKLISTED`).
3. **Job Existence**: Job ID must exist in `jobs`.
4. **Open Job Status**: Job `status` must be `OPEN` (rejects `DRAFT`, `CLOSED`, `CANCELLED`).
5. **Application Deadline**: Application deadline must not have passed (`application_deadline >= CURDATE()`).
6. **Duplicate Prevention**: Rejects duplicate application if candidate has already applied (`uq_candidate_job`).
7. **Percentage Cutoff**: Candidate `percentage` >= `jobs.min_percentage` (when specified).
8. **Passing Year Range**: Candidate `passing_year` must fall between `min_passing_year` and `max_passing_year` (when specified).
9. **Backlog Limit**: Candidate `backlog_count` <= `jobs.max_backlogs`.
10. **Qualification Rule**: Candidate qualification must satisfy `jobs.qualification_rule` when defined.
11. **Mandatory Skills**: Candidate must possess all mandatory skills required by `job_skills` with matching proficiency.

If any check fails, the engine returns `NOT_ELIGIBLE` with descriptive, actionable failure reasons.

---

## 12. Recruitment Workflow Lifecycle

The system strictly regulates transitions across the 10 defined application statuses:

```text
       APPLIED
          │
          ▼
ELIGIBILITY_VERIFIED
          │
          ▼
     SHORTLISTED
          │
          ▼
 INTERVIEW_SCHEDULED
          │
          ▼
     INTERVIEWED
          │
          ▼
      SELECTED
          │
          ▼
       OFFERED
          │
          ▼
       JOINED
```

- **Alternative Branching**:
  - `REJECTED`: Permitted from `APPLIED`, `ELIGIBILITY_VERIFIED`, `SHORTLISTED`, `INTERVIEWED`, or `SELECTED`.
  - `WITHDRAWN`: Permitted at candidate request prior to `JOINED`.
- **Enforcement**: Direct state jumps (e.g. `APPLIED` → `JOINED`) are strictly rejected by `InvalidWorkflowTransitionException`.

---

## 13. Transaction Management

All multi-table operations use atomic JDBC transactions (`connection.setAutoCommit(false)`, `commit()`, and `rollback()`):

1. **Application Submission**:
   - Inserts application record into `applications`.
   - Inserts initial status audit entry into `application_history`.
   - Records event into `activity_logs`.
   - Commits atomically or rolls back on any SQL exception.
2. **Application Status Progression**:
   - Updates `application_status` in `applications`.
   - Appends status change, user, and remarks to `application_history`.
   - Records event into `activity_logs`.
3. **Offer Generation**:
   - Inserts offer into `offers`.
   - Advances application status to `OFFERED`.
   - Appends audit entry into `application_history`.
   - Records event into `activity_logs`.
4. **Placement Joining Confirmation**:
   - Inserts placement record into `placements`.
   - Updates `applications.application_status = 'JOINED'`.
   - Updates `candidates.placement_status = 'PLACED'`.
   - Appends audit entry into `application_history`.
   - Records event into `activity_logs`.

---

## 14. Activity Logging (Database & File)

Every significant business event is logged to two targets:

1. **Database Table (`rpms_db.activity_logs`)**:
   - Captures `user_id`, `module_name`, `action_name`, `record_id`, `description`, and `logged_at`.
   - Uses user context and links to active users.
2. **Text Log (`activity_log.txt`)**:
   - Append-only format: `[YYYY-MM-DD HH:mm:ss] Action Description`.
   - Provides an immediate filesystem audit log without requiring database queries.
   - Sensitive data (passwords, auth tokens) is strictly masked/excluded.

---

## 15. Analytical Reports

The system provides 6 core analytical reports powered by SQL joins and aggregations in `ReportDAO.java`:

- **Report 1: Candidates by Technology**: Displays distribution of registered candidates grouped by primary technology.
- **Report 2: Open Jobs**: Details all currently active campus openings whose deadline has not expired.
- **Report 3: Applications by Status**: Breakdown of applications across all 10 workflow statuses.
- **Report 4: Company-Wise Placements**: Aggregates confirmed placements per employer company.
- **Report 5: Placed Candidates**: Tabulates student details, employer, designation, confirmed CTC, and actual joining date.
- **Report 6: Unplaced Candidates**: Lists all candidates without a confirmed `JOINED` placement status.
- **Additional Reports**: Skills distribution, company openings summary, interview results breakdown, offer status summary.

---

## 16. Custom Exceptions

Located in `com.recruitment.exception`:
- `CandidateNotFoundException`: Thrown when a specified candidate ID/code does not exist.
- `JobNotFoundException`: Thrown when a specified job opening does not exist.
- `DuplicateApplicationException`: Thrown when a candidate attempts to re-apply for a previously applied job.
- `CandidateNotEligibleException`: Thrown when a candidate fails eligibility criteria during application.
- `InvalidWorkflowTransitionException`: Thrown when an illegal lifecycle status transition is attempted.
- `ValidationException`: Base exception thrown for invalid input parameters (negative packages, invalid emails, etc.).

---

## 17. Testing Instructions

Automated unit tests are implemented using JUnit 5 under `src/test/java/com/recruitment/service/`:
- `CandidateServiceTest.java`: Verifies percentage bounds (0-100), backlog checks, and email validation.
- `JobServiceTest.java`: Validates positive openings, package range ordering, and negative backlog rejection.
- `ApplicationServiceTest.java`: Tests valid workflow transitions and verifies that direct jumps (e.g. `APPLIED` → `JOINED`) and transitions from terminal states are rejected.
- `InterviewServiceTest.java`: Validates interview scheduling and required parameters.
- `OfferAndPlacementServiceTest.java`: Verifies positive package requirements and chronological offer/joining date ordering.

*(Note: Per explicit development workflow constraints, tests are structured for automated execution in CI/CD environments without unprompted console disruption).*

---

## 18. Troubleshooting

| Symptom | Cause | Solution |
|---|---|---|
| `Communications link failure` | MySQL service is stopped or port 3306 is blocked. | Run `sudo systemctl start mysql` and verify connectivity on port 3306. |
| `Access denied for user 'root'@'localhost'` | MySQL password mismatch. | Update `db.password` in `src/main/resources/db.properties` or set `export DB_PASSWORD=your_pwd`. |
| `Unknown database 'rpms_db'` | Database has not been initialized. | Run `mysql -u root -p < database/rpms_full_setup.sql`. |
| `Duplicate entry ... for key 'uq_candidate_job'` | Candidate has already applied for the job. | Normal behavior. Handled gracefully by `DuplicateApplicationException`. |
| `Candidate percentage is below required minimum` | Candidate does not satisfy the job's academic cutoff. | Verify candidate percentage against `jobs.min_percentage`. |

---

## Preserving UI Prototype
The static HTML/CSS prototype located in `recruitment-placement-ui/` is preserved untouched. The Java JDBC backend operates as an independent console application as specified.

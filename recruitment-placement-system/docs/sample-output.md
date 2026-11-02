# Sample Console Execution Output - RPMS (rpms_db)

Representative console output captures demonstrating the menus, operations, validations, transaction executions, and reports in the **Recruitment & Placement Management System** configured for `rpms_db`.

---

## 1. Application Startup & Main Menu

```text
=================================================
 Starting Recruitment & Placement System...    
=================================================
[INFO] Testing database connectivity: SUCCESS (Connected to jdbc:mysql://localhost:3306/rpms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8)

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
Enter your choice (1 - 9): 1
```

---

## 2. Viewing Registered Candidates

```text
--- CANDIDATES MENU ---
1. Add Candidate
2. Update Candidate
3. Search Candidate
4. View All Candidates
5. View Candidate by ID
6. Deactivate Candidate
7. Back to Main Menu
Enter your choice (1 - 7): 4

ID     | Name                 | Technology   | Year   | Pct(%)   | Backl | Location   | Status  
-----------------------------------------------------------------------------------------------
CAN1   | Aarav Sharma         | Java         | 2024   | 82.50    | 0     | Pune       | ACTIVE  
CAN2   | Priya Patel          | Java         | 2024   | 78.00    | 0     | Mumbai     | ACTIVE  
CAN3   | Rohan Kulkarni       | Python       | 2024   | 88.40    | 0     | Pune       | ACTIVE  
CAN4   | Ananya Deshmukh      | React        | 2025   | 74.20    | 0     | Pune       | ACTIVE  
CAN5   | Vikram Joshi         | Testing      | 2024   | 69.50    | 1     | Pune       | ACTIVE  
CAN6   | Sneha Rao            | Java         | 2024   | 85.00    | 0     | Bangalore  | ACTIVE  
CAN7   | Aditya Verma         | Python       | 2024   | 62.00    | 2     | Satara     | ACTIVE  
CAN8   | Tanvi Mehra          | React        | 2025   | 81.30    | 0     | Mumbai     | ACTIVE  
CAN9   | Kunal Shinde         | Testing      | 2024   | 71.00    | 0     | Pune       | ACTIVE  
CAN10  | Neha Nambiar         | Java         | 2024   | 91.20    | 0     | Bangalore  | ACTIVE  
CAN11  | Sameer Khan          | Java         | 2023   | 58.00    | 3     | Bangalore  | INACTIVE
CAN12  | Pooja Hegde          | Python       | 2024   | 76.50    | 0     | Bangalore  | ON_HOLD 
Total: 12 record(s).
```

---

## 3. Eligibility Check Execution

```text
--- APPLICATIONS MENU ---
1. Apply for Job
2. Check Eligibility
...
Enter your choice (1 - 10): 2
Enter Candidate ID: 7
Enter Job ID: 2

=============================================
Eligibility Status for Candidate #7, Job #2: NOT_ELIGIBLE
Reason(s):
 - Candidate percentage (62.00%) is below job requirement (65.00%).
 - Candidate backlogs (2) exceed maximum allowed (0).
=============================================
```

Eligible Candidate Check:

```text
Enter Candidate ID: 2
Enter Job ID: 1

=============================================
Eligibility Status for Candidate #2, Job #1: ELIGIBLE
Reason(s):
 - Candidate satisfies all job eligibility criteria.
=============================================
```

---

## 4. Applying for a Job (Duplicate Check & Transaction)

```text
--- APPLICATIONS MENU ---
Enter your choice (1 - 10): 1
Enter Candidate ID: 1
Enter Job ID: 1

[ERROR] Application operation failed: Candidate 1 has already applied for job 1
```

---

## 5. Scheduling and Conducting an Interview

```text
--- INTERVIEWS MENU ---
1. Schedule Interview
2. View Today's Interviews
3. View Upcoming Interviews
4. View Interview History
5. Reschedule Interview
6. Cancel Interview
7. Mark No Show
8. Record Interview Feedback
9. Back to Main Menu
Enter your choice (1 - 9): 8

Enter Interview ID to record feedback: 1
Technical Rating (1 to 5) (1 - 5): 5
Communication Rating (1 to 5) (1 - 5): 4
Detailed Feedback Remarks: Strong core Java multithreading and JDBC principles.
Select Result Outcome:
 1. NEXT_ROUND
 2. SELECTED (Candidate cleared drive)
 3. REJECTED
 4. ON_HOLD
Choice (1 - 4): 2
Advance application status to SELECTED? (y/n): y

[SUCCESS] Feedback recorded successfully for Interview INT1
```

---

## 6. Extending an Offer (Transactional State Transition)

```text
--- OFFERS MENU ---
1. Create Offer
2. View All Offers
3. Search Offer
4. Accept Offer
5. Reject Offer
6. Expire Offer
7. Withdraw Offer
8. Back to Main Menu
Enter your choice (1 - 8): 1

>> Create Job Offer
Application ID (must be in SELECTED stage): 6
Company ID: 1
Designation: Associate Software Engineer
Offered Annual CTC (INR): 650000.00
Offer Date (YYYY-MM-DD) [2026-11-01]: 
Expected Joining Date (YYYY-MM-DD): 2026-12-01

[SUCCESS] Job offer created successfully with ID: OFF4
```

---

## 7. Confirming Joining & Final Placement

```text
--- PLACEMENTS MENU ---
1. Confirm Joining
2. Update Joining Status
3. View All Placements
4. Search Placement
5. View Placed Candidates
6. View Unplaced Candidates
7. Back to Main Menu
Enter your choice (1 - 7): 1

>> Confirm Joining Process
Enter Accepted Offer ID: 1
Actual Joining Date (YYYY-MM-DD) [2026-11-01]: 2026-10-15
Joining Status (JOINED/DEFERRED/NO_SHOW) [JOINED]: JOINED

[SUCCESS] Placement confirmed successfully with ID: PLC1
```

---

## 8. Management Analytics Reports

### Report 1: Candidates by Technology

```text
========================================
     REPORT 1: CANDIDATES BY TECHNOLOGY 
========================================
Java               :    5
Python             :    3
React              :    2
Testing            :    2
----------------------------------------
Total Candidates   :   12
```

### Report 3: Applications by Status

```text
========================================
   REPORT 3: APPLICATIONS BY STATUS     
========================================
APPLIED            :    4
ELIGIBILITY_VERIFIED:   1
SHORTLISTED        :    3
INTERVIEW_SCHEDULED:    2
INTERVIEWED        :    2
SELECTED           :    1
OFFERED            :    2
JOINED             :    3
REJECTED           :    2
WITHDRAWN          :    0
----------------------------------------
Total Applications :   20
```

### Report 4: Company-Wise Placements

```text
========================================
  REPORT 4: COMPANY-WISE PLACEMENTS     
========================================
Tata Consultancy Services      :   1
Capgemini Technology           :   1
CloudNova Software             :   1
Infosys Limited                :   0
Wipro Technologies             :   0
Accolite Digital               :   0
----------------------------------------
Total Placed Students          :   3
```

---

## 9. Activity Log Audit Sample (`activity_log.txt` & `activity_logs` Table)

```text
[2026-11-01 02:00:00] [CANDIDATE - CREATE] (Record: 1, User: 1) Candidate registered: Aarav Sharma (CAN-2024-001)
[2026-11-01 02:05:10] [COMPANY - CREATE] (Record: 1, User: 1) Company created: Tata Consultancy Services (COM-TCS-01)
[2026-11-01 02:07:30] [JOB - CREATE] (Record: 1, User: 1) Job opening posted: Java Software Engineer (JOB-TCS-01)
[2026-11-01 02:10:15] [APPLICATION - CREATE] (Record: 1, User: 1) Application submitted: APP-2024-001 (Candidate: 1, Job: 1)
[2026-11-01 02:12:00] [APPLICATION - STATUS_CHANGE] (Record: 1, User: 1) Application #1 status changed: APPLIED -> SHORTLISTED
[2026-11-01 02:15:30] [INTERVIEW - SCHEDULE] (Record: 1, User: 1) Interview Round #1 scheduled for Application #1
[2026-11-01 02:20:00] [INTERVIEW - FEEDBACK] (Record: 1, User: 1) Feedback submitted for Interview #1, Rec: SELECTED
[2026-11-01 02:22:15] [OFFER - CREATE] (Record: 1, User: 1) Offer issued: OFF-2024-001 for Application #1
[2026-11-01 02:25:00] [OFFER - ACCEPT] (Record: 1, User: 1) Offer accepted: OFF-2024-001
[2026-11-01 02:28:40] [PLACEMENT - CONFIRM_JOINING] (Record: 1, User: 1) Joining confirmed: Candidate #1 at Company #1 with status JOINED
```

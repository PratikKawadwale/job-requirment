# Recruitment & Placement Management System (RPMS) UI Prototype

## 1. Project Purpose & Objective
The **Recruitment & Placement Management System (RPMS)** is a modern, responsive, executive SaaS dashboard interface designed for training institutes, colleges, and placement cells to manage their end-to-end campus recruitment lifecycle.

The prototype visually demonstrates the complete recruitment lifecycle:
**Candidate Registration** → **Eligibility Verification** → **Job Application** → **Shortlisting** → **Interview Scheduling** → **Interview Feedback** → **Selection** → **Offer Management** → **Joining** → **Final Placement** → **Reports & Analytics**.

---

## 2. Technologies Used
- **HTML5**: Semantic tags, accessible forms, meta viewports, structured tables.
- **CSS3**: CSS Custom Properties (Variables), Flexbox, CSS Grid layouts, custom UI components, responsive media queries (`@media (max-width: 768px)`).
- **Icons**: Font Awesome 6 CDN.
- **Typography**: Google Fonts (*Plus Jakarta Sans* & *Inter*).
- **Design Paradigm**: Clean executive SaaS blue-and-white theme, subtle shadows, rounded cards, accessible status indicators, and CSS progress bars.

> **Important Architectural Note:**
> This project is **strictly a pure HTML5 and CSS3 front-end UI prototype**. 
> - No Java / Spring Boot backend.
> - No JDBC, MySQL, or SQL database.
> - No REST APIs or server execution.
> - No React, Angular, or Vue framework.
> - All sample data is static and pre-populated with realistic Indian candidate and company names.
> - Forms operate with simulated validations and direct HTML hyperlink transitions.

---

## 3. Project Directory Structure

```
recruitment-placement-ui/
│
├── login.html                 # Page 1: Portal authentication & role selector
├── dashboard.html             # Page 2: Executive overview & recruitment funnel
├── candidates.html            # Page 3: Candidate directory (10 sample records)
├── candidate-form.html        # Page 4: Candidate enrollment & academic form
├── candidate-details.html     # Page 5: Candidate dossier & placement history
├── companies.html             # Page 6: Corporate partner empanelment & jobs
├── jobs.html                  # Page 7: Job requisitions & openings dashboard
├── job-form.html              # Page 8: Job mandate creation & eligibility criteria
├── applications.html          # Page 9: Screening pipeline & 15 sample applications
├── interviews.html            # Page 10: Interview scheduler & live session tracker
├── placement-process.html     # Page 11: Feedback, offer letters, & joining tracker
├── reports.html               # Page 12: Placement analytics & recruiter metrics
│
├── css/
│   └── style.css              # Shared comprehensive stylesheet & design system
│
├── images/
│   ├── logo.svg               # Vector brand logo (Academic Cap + Briefcase)
│   └── logo.png               # Binary raster logo asset
│
└── README.md                  # Comprehensive documentation & running instructions
```

---

## 4. List of All 12 HTML Pages

1. **`login.html`**:
   - Secure login card, role selector (Admin, Recruiter, Placement Officer), demo credentials panel (`recruiter@demo.com` / `demo123`), remember me toggle, and direct navigation to `dashboard.html`.

2. **`dashboard.html`**:
   - 8 KPI cards: Total Candidates (2,450), Active (2,180), Companies (125), Open Jobs (28), Applications (1,200), Interviews (480), Selected (210), Placed (175).
   - Visual recruitment funnel progression, upcoming interviews table, recent job postings, and recent star placements.

3. **`candidates.html`**:
   - Search filter panel (ID, Name, Mobile, Email, Technology, Passing Year, Qualification, Status).
   - 10 realistic sample Indian candidate records (CAN1001 to CAN1010) with qualification pills, percentage scores, backlog counts, active status badges, and action buttons.

4. **`candidate-form.html`**:
   - 4 structured sections: Personal Information, Academic Credentials, Technical Skills, and Placement Preferences.
   - Validation helper hints, required attributes, and Cancel navigation back to `candidates.html`.

5. **`candidate-details.html`**:
   - Candidate hero badge (Aarav Sharma - CAN1001), verified academic credentials, verified skills badges, application history table, interview logs, and offer status.

6. **`companies.html`**:
   - Corporate partner empanelment form, company search filters, and table featuring 6 sample companies (TCS, Infosys, Wipro, Zoho, Cognizant, Tech Mahindra) with open jobs linked to `jobs.html`.

7. **`jobs.html`**:
   - 4 summary KPI cards (Total Jobs, Open Jobs, Closed Jobs, Total Openings).
   - Multi-criteria filter card, and 8 sample job mandates (JOB-2026-001 to JOB-2026-008) with salary CTC ranges and status indicators.

8. **`job-form.html`**:
   - 5 structured form sections: Basic Information, Compensation, Academic Eligibility Rules, Technical Stacks, and Important Drive Dates.

9. **`applications.html`**:
   - 6 KPI metric cards, visual recruitment pipeline banner, comprehensive filter panel, and a table of 15 sample applications with ELIGIBLE / NOT ELIGIBLE badges and APPLIED / SHORTLISTED / INTERVIEW / SELECTED statuses.

10. **`interviews.html`**:
    - 4 operational KPI cards, today's live interview lineup cards, panel interview scheduling form, and an 8-record interview history table linked to `placement-process.html`.

11. **`placement-process.html`**:
    - 5-step visual placement progress workflow.
    - 3 interactive tabs:
      - **Section A**: Interview Feedback & 1-5 rating scorecards.
      - **Section B**: Job Offer Letter generation & status updater.
      - **Section C**: Joining confirmation & institutional placement archiving.
    - Master offer and placement registry table with 6 sample student records.

12. **`reports.html`**:
    - Comprehensive placement intelligence dashboard with 5 KPI cards (Applications 1200, Interviews 480, Selected 210, Joined 175, Placement Rate 83.3%).
    - CSS-only progress bars (no external charting JS needed): Recruitment Funnel breakdown, Company-wise distribution, Technology breakdown, Recruiter performance scorecard, pending interviews, and job aging metrics.

---

## 5. Instructions to Run the Prototype

No web server or compilation step is required! You can open and navigate the prototype in any modern web browser:

### Option A: Direct Browser File Launch
1. Navigate to the `recruitment-placement-ui/` directory on your computer.
2. Double-click or right-click **`login.html`** and select **"Open with Google Chrome"** (or Edge, Firefox, Safari).
3. Click the **"Login to Dashboard"** button to enter the main dashboard.
4. Use the sidebar navigation on the left to move between all 12 modules seamlessly.

### Option B: From Command Line / Terminal
```bash
# Open directly in default web browser (Linux)
xdg-open recruitment-placement-ui/login.html

# On macOS:
# open recruitment-placement-ui/login.html

# On Windows:
# start recruitment-placement-ui/login.html
```

---

## 6. Static Data & Zero-Backend Architecture
- **Data Persistence**: Because this is a static client-side prototype, data submitted into forms is simulated and does not modify a database.
- **Navigation**: All hyperlinks use relative paths (`href="dashboard.html"`, `href="candidate-details.html"`, etc.), ensuring full portability whether run locally or hosted on GitHub Pages / static hosting.
- **Responsiveness**: The entire UI is built with a mobile breakpoint around `768px`, allowing seamless rendering across desktops, tablets, and smartphones.

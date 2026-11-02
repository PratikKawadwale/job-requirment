/**
 * RPMS Live Data & UI Interaction Bridge
 * Connects Login, Global Search, Filter Panels, and Tables to live MySQL (rpms_db).
 */

// Cached data pools for instantaneous searching and filtering
let allCandidatesCache = [];
let allCompaniesCache = [];
let allJobsCache = [];
let allApplicationsCache = [];

document.addEventListener('DOMContentLoaded', () => {
  const isHttp = window.location.protocol.startsWith('http');
  const page = window.location.pathname.split('/').pop() || 'login.html';

  // 1. Setup User Session, Dropdowns & Header Widgets (on authenticated pages)
  if (!page.includes('login')) {
    initUserSession();
    setupHeaderWidgets();
  }

  // 2. Setup Global Top Header Search Bar across all pages
  setupGlobalHeaderSearch(page);

  // 3. Page-specific initialization
  if (page.includes('login')) {
    initLoginLive();
  } else if (page.includes('dashboard') || page === '') {
    if (isHttp) initDashboardLive();
  } else if (page.includes('candidates')) {
    initCandidatesLive();
  } else if (page.includes('companies')) {
    initCompaniesLive();
  } else if (page.includes('jobs')) {
    initJobsLive();
  } else if (page.includes('applications')) {
    initApplicationsLive();
  } else if (page.includes('reports')) {
    if (isHttp) initReportsLive();
  }

  // Inject Live DB Connection Badge in Top Header if on HTTP
  if (isHttp && !page.includes('login')) {
    const headerRight = document.querySelector('.header-right');
    if (headerRight && !document.querySelector('.live-db-status')) {
      const badge = document.createElement('div');
      badge.className = 'live-db-status';
      badge.style.cssText = 'display:flex;align-items:center;gap:6px;background:#ecfdf5;border:1px solid #a7f3d0;color:#065f46;padding:5px 12px;border-radius:20px;font-size:0.75rem;font-weight:600;margin-right:10px;';
      badge.innerHTML = '<span style="width:8px;height:8px;background:#10b981;border-radius:50%;display:inline-block;box-shadow:0 0 0 2px rgba(16,185,129,0.3);"></span> Live MySQL (rpms_db)';
      headerRight.prepend(badge);
    }
  }
});

// =============================================================================
// USER SESSION & AUTHENTICATION
// =============================================================================
function initUserSession() {
  const stored = localStorage.getItem('rpms_user');
  if (stored) {
    try {
      const user = JSON.parse(stored);
      const nameEl = document.querySelector('.user-profile-widget .user-name');
      const roleEl = document.querySelector('.user-profile-widget .user-role');
      const avatarEl = document.querySelector('.user-profile-widget .user-avatar');

      // 1. Display Full Name
      if (nameEl && user.fullName) {
        nameEl.textContent = user.fullName;
      }

      // 2. Display exact Username and Role together so the user always sees their logged-in username!
      if (roleEl) {
        const uRole = user.role || user.roleName || 'User';
        const uHandle = user.username ? `@${user.username}` : '';
        roleEl.textContent = uHandle ? `${uHandle} • ${uRole}` : uRole;
      }

      if (avatarEl && user.fullName) {
        const initials = user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
        avatarEl.textContent = initials;
      }

      // 3. Update User Dropdown Menu if already rendered
      const ddName = document.getElementById('dropdown-user-name');
      const ddHandle = document.getElementById('dropdown-user-handle');
      const ddEmail = document.getElementById('dropdown-user-email');
      const ddRole = document.getElementById('dropdown-user-role');
      if (ddName && user.fullName) ddName.textContent = user.fullName;
      if (ddHandle && user.username) ddHandle.textContent = `@${user.username}`;
      if (ddRole && (user.role || user.roleName)) ddRole.textContent = user.role || user.roleName;
      if (ddEmail) ddEmail.textContent = user.email || (user.username ? `${user.username}@example.com` : 'user@example.com');
    } catch (e) {
      console.warn('[RPMS] Could not parse stored user session:', e);
    }
  }

  // Bind all Logout links
  document.querySelectorAll('a.logout, a[href="login.html"]').forEach(link => {
    link.addEventListener('click', (e) => {
      if (link.textContent.toLowerCase().includes('logout')) {
        localStorage.removeItem('rpms_user');
      }
    });
  });
}

// =============================================================================
// HEADER WIDGETS: NOTIFICATION BELL & USER PROFILE DROPDOWN
// =============================================================================
function setupHeaderWidgets() {
  const headerRight = document.querySelector('.header-right');
  const notifBell = document.querySelector('.notification-bell');
  const profileWidget = document.querySelector('.user-profile-widget');

  if (!headerRight) return;

  // 1. Setup Notification Bell & Dropdown
  if (notifBell && !document.getElementById('rpms-notif-dropdown')) {
    notifBell.style.cursor = 'pointer';
    notifBell.setAttribute('role', 'button');
    notifBell.setAttribute('tabindex', '0');
    notifBell.title = "View Notifications";

    const notifDropdown = document.createElement('div');
    notifDropdown.id = 'rpms-notif-dropdown';
    notifDropdown.className = 'rpms-header-dropdown notification-dropdown';
    notifDropdown.innerHTML = `
      <div class="notification-dropdown-header">
        <h5><i class="fa-regular fa-bell" style="color: var(--primary);"></i> Notifications <span class="badge badge-primary" id="notif-count-badge" style="font-size:0.65rem; padding: 2px 7px; background:var(--primary); color:#fff; border-radius:10px;">4 New</span></h5>
        <button type="button" id="mark-all-read-btn" style="background:none; border:none; color:var(--primary); font-size:0.75rem; cursor:pointer; font-weight:600;">Mark all read</button>
      </div>
      <ul class="notification-dropdown-list">
        <li>
          <a href="jobs.html" class="notification-dropdown-item">
            <div class="icon-circle" style="background:#e0e7ff; color:#3730a3;"><i class="fa-solid fa-briefcase"></i></div>
            <div class="notif-content">
              <div class="notif-title">New Job Drive Published</div>
              <div class="notif-desc">TCS announced 45 openings for Software Engineer (JOB-2026-001).</div>
              <div class="notif-time"><i class="fa-regular fa-clock"></i> 10 mins ago</div>
            </div>
          </a>
        </li>
        <li>
          <a href="interviews.html" class="notification-dropdown-item">
            <div class="icon-circle" style="background:#fef3c7; color:#92400e;"><i class="fa-solid fa-calendar-check"></i></div>
            <div class="notif-content">
              <div class="notif-title">Interview Scheduled</div>
              <div class="notif-desc">Technical Round 1 scheduled for Aarav Sharma with Wipro.</div>
              <div class="notif-time"><i class="fa-regular fa-clock"></i> 1 hour ago</div>
            </div>
          </a>
        </li>
        <li>
          <a href="placement-process.html" class="notification-dropdown-item">
            <div class="icon-circle" style="background:#ecfdf5; color:#065f46;"><i class="fa-solid fa-award"></i></div>
            <div class="notif-content">
              <div class="notif-title">Offer Letter Accepted</div>
              <div class="notif-desc">Priya Patel accepted offer from Infosys Technologies (6.5 LPA).</div>
              <div class="notif-time"><i class="fa-regular fa-clock"></i> 3 hours ago</div>
            </div>
          </a>
        </li>
        <li>
          <a href="applications.html" class="notification-dropdown-item">
            <div class="icon-circle" style="background:#f1f5f9; color:#475569;"><i class="fa-solid fa-clipboard-check"></i></div>
            <div class="notif-content">
              <div class="notif-title">Batch Eligibility Verified</div>
              <div class="notif-desc">12 candidates confirmed eligible for Cognizant recruitment drive.</div>
              <div class="notif-time"><i class="fa-regular fa-clock"></i> Yesterday</div>
            </div>
          </a>
        </li>
      </ul>
      <div class="notification-dropdown-footer">
        <a href="dashboard.html">View Activity Logs & Timeline <i class="fa-solid fa-arrow-right"></i></a>
      </div>
    `;
    headerRight.appendChild(notifDropdown);

    notifBell.addEventListener('click', (e) => {
      e.stopPropagation();
      const userMenu = document.getElementById('rpms-user-dropdown');
      if (userMenu) userMenu.classList.remove('show');
      notifDropdown.classList.toggle('show');
    });

    const markReadBtn = notifDropdown.querySelector('#mark-all-read-btn');
    if (markReadBtn) {
      markReadBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        const badge = document.querySelector('.bell-badge');
        if (badge) badge.style.display = 'none';
        const countBadge = notifDropdown.querySelector('#notif-count-badge');
        if (countBadge) {
          countBadge.textContent = '0 New';
          countBadge.style.background = '#94a3b8';
        }
      });
    }
  }

  // 2. Setup User Profile Widget & Dropdown
  if (profileWidget && !document.getElementById('rpms-user-dropdown')) {
    profileWidget.style.cursor = 'pointer';
    profileWidget.setAttribute('role', 'button');
    profileWidget.setAttribute('tabindex', '0');
    profileWidget.title = "User Profile & Menu";

    let fullName = 'Amit Kulkarni';
    let username = 'amit.recruiter';
    let roleName = 'Corporate Recruiter';
    let email = 'amit.recruiter@example.com';

    const stored = localStorage.getItem('rpms_user');
    if (stored) {
      try {
        const u = JSON.parse(stored);
        if (u.fullName) fullName = u.fullName;
        if (u.username) username = u.username;
        if (u.role || u.roleName) roleName = u.role || u.roleName;
        if (u.email) email = u.email;
        else if (u.username) email = u.username.includes('@') ? u.username : `${u.username}@example.com`;
      } catch (e) {}
    }

    const userDropdown = document.createElement('div');
    userDropdown.id = 'rpms-user-dropdown';
    userDropdown.className = 'rpms-header-dropdown user-dropdown';
    userDropdown.innerHTML = `
      <div class="user-dropdown-header">
        <div class="ud-name" id="dropdown-user-name">${fullName}</div>
        <div style="font-size:0.75rem; color:var(--primary); font-weight:600; margin-top:2px;" id="dropdown-user-handle">@${username}</div>
        <div class="ud-email" id="dropdown-user-email">${email}</div>
        <span class="ud-role-badge" id="dropdown-user-role">${roleName}</span>
      </div>
      <a href="dashboard.html" class="user-dropdown-item">
        <i class="fa-solid fa-gauge-high" style="color: var(--primary); width: 16px;"></i> Dashboard Overview
      </a>
      <a href="candidates.html" class="user-dropdown-item">
        <i class="fa-solid fa-users" style="color: #0284c7; width: 16px;"></i> Candidate Directory
      </a>
      <a href="jobs.html" class="user-dropdown-item">
        <i class="fa-solid fa-briefcase" style="color: #d97706; width: 16px;"></i> Job Drives
      </a>
      <a href="applications.html" class="user-dropdown-item">
        <i class="fa-solid fa-list-check" style="color: #8b5cf6; width: 16px;"></i> Applications
      </a>
      <a href="reports.html" class="user-dropdown-item">
        <i class="fa-solid fa-chart-pie" style="color: #16a34a; width: 16px;"></i> Analytics & Reports
      </a>
      <div class="user-dropdown-divider"></div>
      <button type="button" id="rpms-dropdown-logout" class="user-dropdown-item text-danger" style="color: #dc2626; border:none; background:none; width:100%; font-family:inherit;">
        <i class="fa-solid fa-right-from-bracket" style="width: 16px;"></i> Logout Session
      </button>
    `;
    headerRight.appendChild(userDropdown);

    profileWidget.addEventListener('click', (e) => {
      e.stopPropagation();
      const notifMenu = document.getElementById('rpms-notif-dropdown');
      if (notifMenu) notifMenu.classList.remove('show');
      userDropdown.classList.toggle('show');
    });

    const logoutBtn = userDropdown.querySelector('#rpms-dropdown-logout');
    if (logoutBtn) {
      logoutBtn.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.removeItem('rpms_user');
        window.location.href = 'login.html';
      });
    }
  }

  // 3. Global click & escape listeners to dismiss dropdowns
  document.addEventListener('click', (e) => {
    const notifDropdown = document.getElementById('rpms-notif-dropdown');
    const userDropdown = document.getElementById('rpms-user-dropdown');
    if (notifDropdown && !notifDropdown.contains(e.target) && !notifBell?.contains(e.target)) {
      notifDropdown.classList.remove('show');
    }
    if (userDropdown && !userDropdown.contains(e.target) && !profileWidget?.contains(e.target)) {
      userDropdown.classList.remove('show');
    }
  });

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      const notifDropdown = document.getElementById('rpms-notif-dropdown');
      const userDropdown = document.getElementById('rpms-user-dropdown');
      if (notifDropdown) notifDropdown.classList.remove('show');
      if (userDropdown) userDropdown.classList.remove('show');
    }
  });
}

function initLoginLive() {
  const form = document.getElementById('login-form');
  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const roleSelect = document.getElementById('role');
  const errorBox = document.getElementById('login-error-alert');
  const errorMsg = document.getElementById('login-error-msg');
  const submitBtn = document.getElementById('login-btn');

  // Interactive Database Account Pills - clicking fills exact username, password, and role
  document.querySelectorAll('.account-pill').forEach(pill => {
    pill.addEventListener('click', () => {
      const u = pill.getAttribute('data-user');
      const p = pill.getAttribute('data-pass');
      const r = pill.getAttribute('data-role');

      if (emailInput && u) emailInput.value = u;
      if (passwordInput && p) passwordInput.value = p;
      if (roleSelect && r) roleSelect.value = r;
      if (errorBox) errorBox.style.display = 'none';

      // Visual highlight
      document.querySelectorAll('.account-pill').forEach(el => {
        el.style.borderColor = 'var(--border-color)';
        el.style.backgroundColor = '#ffffff';
      });
      pill.style.borderColor = 'var(--primary)';
      pill.style.backgroundColor = '#eff6ff';
    });
  });

  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const usernameOrEmail = emailInput.value.trim();
    const password = passwordInput.value.trim();

    if (!usernameOrEmail || !password) {
      showError('Please enter both username/email and password.');
      return;
    }

    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Authenticating...';
    }
    if (errorBox) errorBox.style.display = 'none';

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: usernameOrEmail, password: password })
      });

      const data = await res.json();

      if (res.ok && data.success) {
        // Save user session in localStorage
        localStorage.setItem('rpms_user', JSON.stringify(data.user));
        if (submitBtn) {
          submitBtn.className = 'btn btn-primary';
          submitBtn.style.background = '#10b981';
          submitBtn.innerHTML = '<i class="fa-solid fa-check"></i> Login Successful!';
        }
        setTimeout(() => {
          window.location.href = 'dashboard.html';
        }, 500);
      } else {
        showError(data.error || 'Invalid credentials. Please verify your username/email and password.');
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerHTML = '<i class="fa-solid fa-right-to-bracket"></i> Login to Dashboard';
        }
      }
    } catch (err) {
      // In case server is not running or offline, gracefully map to matching client database user
      const userMap = {
        'admin': { userId: 1, username: 'admin', fullName: 'System Admin', email: 'admin@example.com', role: 'Admin' },
        'amit.recruiter': { userId: 2, username: 'amit.recruiter', fullName: 'Amit Kulkarni', email: 'amit.recruiter@example.com', role: 'Recruiter' },
        'sneha.recruiter': { userId: 3, username: 'sneha.recruiter', fullName: 'Sneha Patil', email: 'sneha.recruiter@example.com', role: 'Recruiter' },
        'neha.placement': { userId: 4, username: 'neha.placement', fullName: 'Neha Deshmukh', email: 'neha.placement@example.com', role: 'Placement Officer' },
        'rahul.placement': { userId: 5, username: 'rahul.placement', fullName: 'Rahul Joshi', email: 'rahul.placement@example.com', role: 'Placement Officer' },
        'recruiter@demo.com': { userId: 2, username: 'amit.recruiter', fullName: 'Amit Kulkarni', email: 'amit.recruiter@example.com', role: 'Recruiter' }
      };

      const key = usernameOrEmail.toLowerCase();
      const matched = userMap[key] || {
        userId: 99,
        username: usernameOrEmail.includes('@') ? usernameOrEmail.split('@')[0] : usernameOrEmail,
        fullName: usernameOrEmail,
        email: usernameOrEmail.includes('@') ? usernameOrEmail : `${usernameOrEmail}@example.com`,
        role: roleSelect ? roleSelect.value : 'Recruiter'
      };

      localStorage.setItem('rpms_user', JSON.stringify(matched));
      window.location.href = 'dashboard.html';
    }
  });

  function showError(msg) {
    if (errorBox && errorMsg) {
      errorMsg.textContent = msg;
      errorBox.style.display = 'block';
    } else {
      alert(msg);
    }
  }
}

// =============================================================================
// GLOBAL TOP HEADER SEARCH BAR
// =============================================================================
function setupGlobalHeaderSearch(currentPage) {
  const searchInput = document.querySelector('.header-search input');
  if (!searchInput) return;

  // Check if search query was passed via URL parameter (?search=xyz)
  const urlParams = new URLSearchParams(window.location.search);
  const initialQuery = urlParams.get('search');
  if (initialQuery) {
    searchInput.value = initialQuery;
  }

  // Handle Search Icon Click
  const searchIcon = searchInput.parentElement?.querySelector('.fa-magnifying-glass');
  if (searchIcon) {
    searchIcon.style.cursor = 'pointer';
    searchIcon.title = 'Click to search';
    searchIcon.addEventListener('click', () => {
      executeSearch(searchInput.value.trim(), currentPage);
    });
  }

  // Handle Search Input Events
  searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      executeSearch(searchInput.value.trim(), currentPage);
    }
  });

  searchInput.addEventListener('input', () => {
    const q = searchInput.value.trim();
    // Live filter if already on a searchable list page
    if (currentPage.includes('candidates')) {
      filterCandidatesTable(q);
    } else if (currentPage.includes('companies')) {
      filterCompaniesTable(q);
    } else if (currentPage.includes('jobs')) {
      filterJobsTable(q);
    } else if (currentPage.includes('applications')) {
      filterApplicationsTable(q);
    } else if (currentPage.includes('interviews') || currentPage.includes('placement')) {
      filterGenericTable(q);
    }
  });
}

function filterGenericTable(query) {
  const q = (query || '').toLowerCase().trim();
  const rows = document.querySelectorAll('.data-table tbody tr');
  rows.forEach(tr => {
    if (!q) {
      tr.style.display = '';
    } else {
      const text = tr.textContent.toLowerCase();
      tr.style.display = text.includes(q) ? '' : 'none';
    }
  });
}

function executeSearch(query, currentPage) {
  if (currentPage.includes('candidates')) {
    filterCandidatesTable(query);
  } else if (currentPage.includes('companies')) {
    filterCompaniesTable(query);
  } else if (currentPage.includes('jobs')) {
    filterJobsTable(query);
  } else if (currentPage.includes('applications')) {
    filterApplicationsTable(query);
  } else if (currentPage.includes('interviews') || currentPage.includes('placement')) {
    filterGenericTable(query);
  } else {
    // Navigate from dashboard or other page directly to candidate directory with query
    window.location.href = `candidates.html?search=${encodeURIComponent(query)}`;
  }
}

// =============================================================================
// 1. DASHBOARD LIVE METRICS
// =============================================================================
async function initDashboardLive() {
  try {
    const res = await fetch('/api/dashboard/stats');
    if (!res.ok) return;
    const stats = await res.json();

    const kpiCards = document.querySelectorAll('.kpi-card');
    if (kpiCards.length >= 6) {
      if (kpiCards[0]) kpiCards[0].querySelector('.kpi-number').textContent = stats.totalCandidates.toLocaleString();
      if (kpiCards[1]) kpiCards[1].querySelector('.kpi-number').textContent = stats.activeCandidates.toLocaleString();
      if (kpiCards[2]) kpiCards[2].querySelector('.kpi-number').textContent = stats.totalCompanies.toLocaleString();
      if (kpiCards[3]) kpiCards[3].querySelector('.kpi-number').textContent = stats.openJobs.toLocaleString();
      if (kpiCards[4]) kpiCards[4].querySelector('.kpi-number').textContent = stats.totalApplications.toLocaleString();
      if (kpiCards[5]) kpiCards[5].querySelector('.kpi-number').textContent = stats.totalPlaced.toLocaleString();
    }
  } catch (err) {
    console.warn('[RPMS] Could not fetch live dashboard stats:', err);
  }
}

// =============================================================================
// 2. CANDIDATES DIRECTORY & ADVANCED FILTER PANEL
// =============================================================================
async function initCandidatesLive() {
  populateCandidatesFallbackFromDOM();
  try {
    const res = await fetch('/api/candidates');
    if (res.ok) {
      allCandidatesCache = await res.json();
    }
  } catch (err) {
    console.warn('[RPMS] Could not fetch from /api/candidates:', err);
  }

  // Setup Candidate Filter Panel
  setupCandidateFilterPanel();

  // If URL has search query, apply it immediately
  const urlParams = new URLSearchParams(window.location.search);
  const q = urlParams.get('search');
  if (q) {
    filterCandidatesTable(q);
  } else if (allCandidatesCache.length > 0) {
    renderCandidates(allCandidatesCache);
  }
}

function populateCandidatesFallbackFromDOM() {
  if (allCandidatesCache.length > 0) return;
  const rows = document.querySelectorAll('.data-table tbody tr');
  rows.forEach(tr => {
    const tds = tr.querySelectorAll('td');
    if (tds.length >= 10) {
      const code = tds[0]?.textContent.trim();
      const name = tds[1]?.querySelector('.text-strong')?.textContent.trim() || tds[1]?.textContent.trim();
      const loc = tds[1]?.querySelector('.sub-meta')?.textContent.trim() || 'India';
      const email = tds[2]?.textContent.trim();
      const mobile = tds[3]?.textContent.trim();
      const qual = tds[4]?.textContent.trim();
      const year = tds[5]?.textContent.trim();
      const tech = tds[6]?.textContent.trim();
      const score = tds[7]?.textContent.trim();
      const backlogs = tds[8]?.textContent.trim();
      const status = tds[9]?.textContent.trim();
      allCandidatesCache.push({
        candidateCode: code,
        fullName: name,
        currentLocation: loc,
        email: email,
        mobile: mobile,
        qualification: qual,
        passingYear: year,
        primaryTechnology: tech,
        percentage: parseFloat(score) || 80,
        backlogCount: parseInt(backlogs, 10) || 0,
        profileStatus: status.includes('ACTIVE') ? 'ACTIVE' : status
      });
    }
  });
}

function setupCandidateFilterPanel() {
  const filterForm = document.querySelector('.filter-card form');
  if (!filterForm) return;

  filterForm.addEventListener('submit', (e) => {
    e.preventDefault();
    applyCandidateAdvancedFilters();
  });

  const searchBtn = filterForm.querySelector('button[type="submit"]');
  if (searchBtn) {
    searchBtn.addEventListener('click', (e) => {
      e.preventDefault();
      applyCandidateAdvancedFilters();
    });
  }

  filterForm.addEventListener('reset', () => {
    setTimeout(() => {
      renderCandidates(allCandidatesCache);
    }, 50);
  });

  // Attach live input/change triggers to select dropdowns
  ['#filter-tech', '#filter-year', '#filter-qual', '#filter-status'].forEach(sel => {
    const el = document.querySelector(sel);
    if (el) el.addEventListener('change', applyCandidateAdvancedFilters);
  });

  ['#filter-name', '#filter-id', '#filter-email', '#filter-mobile'].forEach(sel => {
    const el = document.querySelector(sel);
    if (el) el.addEventListener('input', applyCandidateAdvancedFilters);
  });
}

function applyCandidateAdvancedFilters() {
  const idVal = (document.getElementById('filter-id')?.value || '').trim().toLowerCase();
  const nameVal = (document.getElementById('filter-name')?.value || '').trim().toLowerCase();
  const mobileVal = (document.getElementById('filter-mobile')?.value || '').trim().toLowerCase();
  const emailVal = (document.getElementById('filter-email')?.value || '').trim().toLowerCase();
  const techVal = (document.getElementById('filter-tech')?.value || '').trim().toLowerCase();
  const yearVal = (document.getElementById('filter-year')?.value || '').trim();
  const qualVal = (document.getElementById('filter-qual')?.value || '').trim().toLowerCase();
  const statusVal = (document.getElementById('filter-status')?.value || '').trim().toUpperCase();

  const filtered = allCandidatesCache.filter(c => {
    const code = (c.candidateCode || 'CAN' + c.candidateId).toLowerCase();
    const name = (c.fullName || '').toLowerCase();
    const mobile = (c.mobile || '').toLowerCase();
    const email = (c.email || '').toLowerCase();
    const tech = (c.primaryTechnology || '').toLowerCase();
    const year = String(c.passingYear || '');
    const qual = (c.qualification || '').toLowerCase();
    const pStatus = (c.profileStatus || '').toUpperCase();
    const plStatus = (c.placementStatus || '').toUpperCase();

    if (idVal && !code.includes(idVal)) return false;
    if (nameVal && !name.includes(nameVal)) return false;
    if (mobileVal && !mobile.includes(mobileVal)) return false;
    if (emailVal && !email.includes(emailVal)) return false;
    if (techVal && !tech.includes(techVal)) return false;
    if (yearVal && year !== yearVal) return false;
    if (qualVal && !qual.includes(qualVal)) return false;
    if (statusVal && pStatus !== statusVal && plStatus !== statusVal) return false;

    return true;
  });

  renderCandidates(filtered);
}

function filterCandidatesTable(query) {
  if (!query) {
    renderCandidates(allCandidatesCache);
    return;
  }
  const q = query.toLowerCase();
  const filtered = allCandidatesCache.filter(c => {
    return (
      (c.fullName && c.fullName.toLowerCase().includes(q)) ||
      (c.candidateCode && c.candidateCode.toLowerCase().includes(q)) ||
      (c.email && c.email.toLowerCase().includes(q)) ||
      (c.mobile && c.mobile.includes(q)) ||
      (c.primaryTechnology && c.primaryTechnology.toLowerCase().includes(q)) ||
      (c.qualification && c.qualification.toLowerCase().includes(q)) ||
      (c.currentLocation && c.currentLocation.toLowerCase().includes(q)) ||
      (c.college && c.college.toLowerCase().includes(q))
    );
  });
  renderCandidates(filtered);
}

function renderCandidates(list) {
  const tbody = document.querySelector('.data-table tbody');
  if (!tbody) return;

  tbody.innerHTML = '';
  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="11" style="text-align:center;padding:2rem;color:var(--text-muted);"><i class="fa-solid fa-magnifying-glass"></i> No matching candidates found in database.</td></tr>';
    return;
  }

  list.forEach(c => {
    const tr = document.createElement('tr');
    const initials = (c.fullName || 'C').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    const pct = (c.percentage !== undefined && c.percentage !== null) ? Number(c.percentage).toFixed(1) + '%' : 'N/A';
    const statusBadge = (c.profileStatus === 'ACTIVE')
      ? '<span class="badge badge-active"><span class="badge-dot"></span>ACTIVE</span>'
      : '<span class="badge badge-danger"><span class="badge-dot"></span>' + (c.profileStatus || 'INACTIVE') + '</span>';

    tr.innerHTML = `
      <td><span class="text-strong" style="color: var(--primary); font-weight:600;">${c.candidateCode || 'CAN' + c.candidateId}</span></td>
      <td>
        <div class="candidate-name-cell">
          <div class="candidate-avatar-sm">${initials}</div>
          <div>
            <span class="text-strong">${c.fullName || ''}</span>
            <div class="sub-meta">${c.currentLocation || 'N/A'}</div>
          </div>
        </div>
      </td>
      <td>${c.email || ''}</td>
      <td>${c.mobile || ''}</td>
      <td><span class="skill-pill">${c.qualification || 'Degree'}</span></td>
      <td>${c.passingYear || ''}</td>
      <td><span class="skill-pill" style="background:#e0e7ff; color:#3730a3;">${c.primaryTechnology || 'General'}</span></td>
      <td><strong>${pct}</strong></td>
      <td><span style="color:${c.backlogCount > 0 ? 'var(--danger)' : 'var(--success)'}; font-weight:600;">${c.backlogCount}</span></td>
      <td>${statusBadge}</td>
      <td style="text-align: right;">
        <div class="btn-group">
          <button class="btn btn-sm btn-outline" onclick="alert('Candidate Details:\\n\\nName: ${c.fullName}\\nCode: ${c.candidateCode}\\nEmail: ${c.email}\\nMobile: ${c.mobile}\\nTech: ${c.primaryTechnology}\\nStatus: ${c.profileStatus}')">
            <i class="fa-solid fa-eye"></i> View
          </button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });

  const cardTitle = document.querySelector('.card-title');
  if (cardTitle) {
    cardTitle.innerHTML = `<i class="fa-solid fa-users"></i> Registered Candidates (${list.length} showing of ${allCandidatesCache.length} from rpms_db)`;
  }
}

// =============================================================================
// 3. COMPANIES DIRECTORY LIVE SEARCH
// =============================================================================
async function initCompaniesLive() {
  try {
    const res = await fetch('/api/companies');
    if (res.ok) {
      allCompaniesCache = await res.json();
      renderCompanies(allCompaniesCache);
    }
  } catch (err) {
    console.warn('[RPMS] Could not fetch live companies:', err);
  }
  setupCompaniesFilterPanel();
}

function setupCompaniesFilterPanel() {
  const form = document.querySelector('.filter-card form');
  if (!form) return;
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    applyCompanyFilters();
  });
  const searchBtn = form.querySelector('button[type="submit"]');
  if (searchBtn) {
    searchBtn.addEventListener('click', (e) => {
      e.preventDefault();
      applyCompanyFilters();
    });
  }
  form.addEventListener('reset', () => {
    setTimeout(() => renderCompanies(allCompaniesCache), 50);
  });
  ['#search-comp-id', '#search-comp-name', '#search-comp-type', '#search-comp-status'].forEach(sel => {
    const el = document.querySelector(sel);
    if (el) {
      el.addEventListener('input', applyCompanyFilters);
      el.addEventListener('change', applyCompanyFilters);
    }
  });
}

function applyCompanyFilters() {
  const idVal = (document.getElementById('search-comp-id')?.value || '').trim().toLowerCase();
  const nameVal = (document.getElementById('search-comp-name')?.value || '').trim().toLowerCase();
  const typeVal = (document.getElementById('search-comp-type')?.value || '').trim().toLowerCase();
  const statusVal = (document.getElementById('search-comp-status')?.value || '').trim().toUpperCase();

  const filtered = allCompaniesCache.filter(c => {
    const code = (c.companyCode || 'COM' + c.companyId).toLowerCase();
    const name = (c.companyName || '').toLowerCase();
    const type = (c.companyType || '').toLowerCase();
    const status = (c.status || '').toUpperCase();

    if (idVal && !code.includes(idVal)) return false;
    if (nameVal && !name.includes(nameVal)) return false;
    if (typeVal && !type.includes(typeVal)) return false;
    if (statusVal && status !== statusVal) return false;
    return true;
  });
  renderCompanies(filtered);
}

function filterCompaniesTable(query) {
  if (!query) {
    renderCompanies(allCompaniesCache);
    return;
  }
  const q = query.toLowerCase();
  const filtered = allCompaniesCache.filter(c => (
    (c.companyName && c.companyName.toLowerCase().includes(q)) ||
    (c.companyCode && c.companyCode.toLowerCase().includes(q)) ||
    (c.industry && c.industry.toLowerCase().includes(q)) ||
    (c.location && c.location.toLowerCase().includes(q)) ||
    (c.hrName && c.hrName.toLowerCase().includes(q))
  ));
  renderCompanies(filtered);
}

function renderCompanies(list) {
  const tbody = document.querySelector('.data-table tbody');
  if (!tbody) return;

  tbody.innerHTML = '';
  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:2rem;color:var(--text-muted);"><i class="fa-solid fa-magnifying-glass"></i> No matching companies found.</td></tr>';
    return;
  }

  list.forEach(c => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><span class="text-strong" style="color: var(--primary); font-weight:600;">${c.companyCode || 'COM' + c.companyId}</span></td>
      <td><strong>${c.companyName}</strong></td>
      <td><span class="skill-pill">${c.industry || 'IT'}</span></td>
      <td>${c.location || 'India'}</td>
      <td>${c.hrName || 'HR Contact'}</td>
      <td>${c.hrEmail || 'hr@company.com'}</td>
      <td><span class="badge badge-active">${c.status}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

// =============================================================================
// 4. JOBS DIRECTORY LIVE SEARCH
// =============================================================================
async function initJobsLive() {
  try {
    const res = await fetch('/api/jobs');
    if (res.ok) {
      allJobsCache = await res.json();
      renderJobs(allJobsCache);
    }
  } catch (err) {
    console.warn('[RPMS] Could not fetch live jobs:', err);
  }
  setupJobsFilterPanel();
}

function setupJobsFilterPanel() {
  const form = document.querySelector('.filter-card form');
  if (!form) return;
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    applyJobFilters();
  });
  const searchBtn = form.querySelector('button[type="submit"]');
  if (searchBtn) {
    searchBtn.addEventListener('click', (e) => {
      e.preventDefault();
      applyJobFilters();
    });
  }
  form.addEventListener('reset', () => {
    setTimeout(() => renderJobs(allJobsCache), 50);
  });
  ['#filter-job-code', '#filter-comp', '#filter-tech-stack', '#filter-job-loc', '#filter-job-status', '#filter-emp-type'].forEach(sel => {
    const el = document.querySelector(sel);
    if (el) {
      el.addEventListener('input', applyJobFilters);
      el.addEventListener('change', applyJobFilters);
    }
  });
}

function applyJobFilters() {
  const codeVal = (document.getElementById('filter-job-code')?.value || '').trim().toLowerCase();
  const compVal = (document.getElementById('filter-comp')?.value || '').trim().toLowerCase();
  const techVal = (document.getElementById('filter-tech-stack')?.value || '').trim().toLowerCase();
  const locVal = (document.getElementById('filter-job-loc')?.value || '').trim().toLowerCase();
  const statusVal = (document.getElementById('filter-job-status')?.value || '').trim().toUpperCase();
  const empVal = (document.getElementById('filter-emp-type')?.value || '').trim().toLowerCase();

  const filtered = allJobsCache.filter(j => {
    const code = (j.jobCode || 'JOB' + j.jobId).toLowerCase();
    const comp = (j.companyName || '').toLowerCase();
    const tech = (j.technology || '').toLowerCase();
    const loc = (j.location || '').toLowerCase();
    const status = (j.status || '').toUpperCase();
    const emp = (j.employmentType || '').toLowerCase();

    if (codeVal && !code.includes(codeVal)) return false;
    if (compVal && !comp.includes(compVal)) return false;
    if (techVal && !tech.includes(techVal)) return false;
    if (locVal && !loc.includes(locVal)) return false;
    if (statusVal && status !== statusVal) return false;
    if (empVal && !emp.includes(empVal)) return false;
    return true;
  });
  renderJobs(filtered);
}

function filterJobsTable(query) {
  if (!query) {
    renderJobs(allJobsCache);
    return;
  }
  const q = query.toLowerCase();
  const filtered = allJobsCache.filter(j => (
    (j.jobTitle && j.jobTitle.toLowerCase().includes(q)) ||
    (j.jobCode && j.jobCode.toLowerCase().includes(q)) ||
    (j.companyName && j.companyName.toLowerCase().includes(q)) ||
    (j.location && j.location.toLowerCase().includes(q)) ||
    (j.technology && j.technology.toLowerCase().includes(q))
  ));
  renderJobs(filtered);
}

function renderJobs(list) {
  const tbody = document.querySelector('.data-table tbody');
  if (!tbody) return;

  tbody.innerHTML = '';
  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;padding:2rem;color:var(--text-muted);"><i class="fa-solid fa-magnifying-glass"></i> No matching jobs found.</td></tr>';
    return;
  }

  list.forEach(j => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><span class="text-strong" style="color: var(--primary); font-weight:600;">${j.jobCode || 'JOB' + j.jobId}</span></td>
      <td><strong>${j.jobTitle}</strong></td>
      <td>${j.companyName || 'Corporate Partner'}</td>
      <td><span class="skill-pill">${j.location}</span></td>
      <td><strong>${j.openings}</strong></td>
      <td>${j.minPackageLpa ? j.minPackageLpa + ' - ' + j.maxPackageLpa + ' LPA' : 'Best in Industry'}</td>
      <td>${j.deadline || 'Ongoing'}</td>
      <td><span class="badge badge-active">${j.status}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

// =============================================================================
// 5. APPLICATIONS DIRECTORY LIVE SEARCH
// =============================================================================
async function initApplicationsLive() {
  try {
    const res = await fetch('/api/applications');
    if (res.ok) {
      allApplicationsCache = await res.json();
      renderApplications(allApplicationsCache);
    }
  } catch (err) {
    console.warn('[RPMS] Could not fetch live applications:', err);
  }
  setupApplicationsFilterPanel();
}

function setupApplicationsFilterPanel() {
  const form = document.querySelector('.filter-card form');
  if (!form) return;
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    applyAppFilters();
  });
  const searchBtn = form.querySelector('button[type="submit"]');
  if (searchBtn) {
    searchBtn.addEventListener('click', (e) => {
      e.preventDefault();
      applyAppFilters();
    });
  }
  form.addEventListener('reset', () => {
    setTimeout(() => renderApplications(allApplicationsCache), 50);
  });
  ['#filter-app-id', '#filter-app-cand', '#filter-app-comp', '#filter-app-job', '#filter-app-status'].forEach(sel => {
    const el = document.querySelector(sel);
    if (el) {
      el.addEventListener('input', applyAppFilters);
      el.addEventListener('change', applyAppFilters);
    }
  });
}

function applyAppFilters() {
  const idVal = (document.getElementById('filter-app-id')?.value || '').trim().toLowerCase();
  const candVal = (document.getElementById('filter-app-cand')?.value || '').trim().toLowerCase();
  const compVal = (document.getElementById('filter-app-comp')?.value || '').trim().toLowerCase();
  const jobVal = (document.getElementById('filter-app-job')?.value || '').trim().toLowerCase();
  const statusVal = (document.getElementById('filter-app-status')?.value || '').trim().toUpperCase();

  const filtered = allApplicationsCache.filter(a => {
    const code = (a.applicationCode || 'APP' + a.applicationId).toLowerCase();
    const cand = (a.candidateName || '').toLowerCase();
    const comp = (a.companyName || '').toLowerCase();
    const job = (a.jobTitle || '').toLowerCase();
    const status = (a.applicationStatus || '').toUpperCase();

    if (idVal && !code.includes(idVal)) return false;
    if (candVal && !cand.includes(candVal)) return false;
    if (compVal && !comp.includes(compVal)) return false;
    if (jobVal && !job.includes(jobVal)) return false;
    if (statusVal && status !== statusVal) return false;
    return true;
  });
  renderApplications(filtered);
}

function filterApplicationsTable(query) {
  if (!query) {
    renderApplications(allApplicationsCache);
    return;
  }
  const q = query.toLowerCase();
  const filtered = allApplicationsCache.filter(a => (
    (a.applicationCode && a.applicationCode.toLowerCase().includes(q)) ||
    (a.candidateName && a.candidateName.toLowerCase().includes(q)) ||
    (a.companyName && a.companyName.toLowerCase().includes(q)) ||
    (a.jobTitle && a.jobTitle.toLowerCase().includes(q)) ||
    (a.applicationStatus && a.applicationStatus.toLowerCase().includes(q)) ||
    (a.eligibilityStatus && a.eligibilityStatus.toLowerCase().includes(q))
  ));
  renderApplications(filtered);
}

function renderApplications(list) {
  const tbody = document.querySelector('.data-table tbody');
  if (!tbody) return;

  tbody.innerHTML = '';
  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;padding:2rem;color:var(--text-muted);"><i class="fa-solid fa-magnifying-glass"></i> No matching applications found.</td></tr>';
    return;
  }

  list.forEach(a => {
    const tr = document.createElement('tr');
    const initials = (a.candidateName || 'A').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    const eligBadge = (a.eligibilityStatus === 'ELIGIBLE')
      ? '<span class="badge badge-eligible"><span class="badge-dot"></span>ELIGIBLE</span>'
      : '<span class="badge badge-danger"><span class="badge-dot"></span>' + (a.eligibilityStatus || 'NOT_ELIGIBLE') + '</span>';

    const statusBadge = `<span class="badge badge-${(a.applicationStatus || 'applied').toLowerCase()}"><span class="badge-dot"></span>${a.applicationStatus || 'APPLIED'}</span>`;

    tr.innerHTML = `
      <td><span class="text-strong">${a.applicationCode || 'APP' + a.applicationId}</span></td>
      <td>
        <div class="candidate-name-cell">
          <div class="candidate-avatar-sm">${initials}</div>
          <div>
            <a href="candidate-details.html" class="text-strong">${a.candidateName || 'Candidate'}</a>
            <div class="sub-meta">CAN${a.candidateId || ''}</div>
          </div>
        </div>
      </td>
      <td>${a.companyName || 'Corporate Partner'}</td>
      <td>${a.jobTitle || 'Role'}</td>
      <td>${a.appliedDate || '2026-10-25'}</td>
      <td>${eligBadge}</td>
      <td>${statusBadge}</td>
      <td style="text-align: right;">
        <a href="interviews.html" class="btn btn-sm btn-outline"><i class="fa-solid fa-calendar-check"></i> Interview</a>
      </td>
    `;
    tbody.appendChild(tr);
  });
}

// =============================================================================
// 6. REPORTS LIVE METRICS
// =============================================================================
async function initReportsLive() {
  try {
    const [techRes, statRes] = await Promise.all([
      fetch('/api/reports/candidates-by-tech'),
      fetch('/api/reports/applications-by-status')
    ]);
    if (techRes.ok) {
      const techData = await techRes.json();
      console.log('[RPMS Live Report] Candidates by Tech:', techData);
    }
    if (statRes.ok) {
      const statData = await statRes.json();
      console.log('[RPMS Live Report] Applications by Status:', statData);
    }
  } catch (err) {
    console.warn('[RPMS] Could not fetch live reports:', err);
  }
}

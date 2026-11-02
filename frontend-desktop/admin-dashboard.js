// =====================================================
// ADMIN DASHBOARD
// =====================================================

let adminCrimeMap = null;

let adminCrimeMarkers = [];
let adminCrimeHeatLayer = null;


// =====================================================
// LOAD DASHBOARD
// =====================================================

async function loadAdminDashboard() {

    console.log(
        "ADMIN DASHBOARD LOADING..."
    );

    await loadAdminUsers();

    await loadIncidentAnalytics();
    await loadSOSStatistics();

    await loadCrimeHotspots();

    await loadResponseMonitoring();

    await loadEmergencyContactStatistics();

    await checkApplicationHealth();

    console.log(
        "ADMIN DASHBOARD LOADED"
    );
}


// =====================================================
// USER MANAGEMENT
// =====================================================

async function loadAdminUsers() {

    const token = localStorage.getItem("token");

    const tableBody =
        document.getElementById("usersTableBody");

    if (!tableBody) {
        console.error("usersTableBody not found");
        return;
    }

    if (!token) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6">
                    Admin authentication token missing
                </td>
            </tr>
        `;
        return;
    }

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/users`,
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        // ==========================================
        // ACCESS DENIED
        // ==========================================

        if (response.status === 403) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="6">
                        ❌ Admin access denied
                    </td>
                </tr>
            `;

            return;
        }

        if (response.status === 401) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="6">
                        ❌ Session expired. Please login again.
                    </td>
                </tr>
            `;

            return;
        }

        if (!response.ok) {
            throw new Error(
                "Failed to load users"
            );
        }

        const users = await response.json();

const totalUsers =
    document.getElementById("totalUsers");

if (totalUsers) {
    totalUsers.textContent =
        users ? users.length : 0;
}

tableBody.innerHTML = "";

if (!users || users.length === 0) {
    tableBody.innerHTML = `
        <tr>
            <td colspan="6">
                No users found
            </td>
        </tr>
    `;

    return;
}

        // ==========================================
        // NO USERS
        // ==========================================

        if (!users || users.length === 0) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="6">
                        No users found
                    </td>
                </tr>
            `;

            return;
        }

        // ==========================================
        // DISPLAY USERS
        // ==========================================

        users.forEach(user => {

            const row =
                document.createElement("tr");

            let statusClass = "";

            if (user.accountStatus === "ACTIVE") {
                statusClass = "status-active";
            }
            else if (user.accountStatus === "BLOCKED") {
                statusClass = "status-blocked";
            }
            else if (user.accountStatus === "BANNED") {
                statusClass = "status-banned";
            }

            row.innerHTML = `
                <td>${user.id}</td>

                <td>${escapeHtml(
                    user.fullName || "-"
                )}</td>

                <td>${escapeHtml(
                    user.email || "-"
                )}</td>
                 <td>
        <span class="user-role">
            ${escapeHtml(
                user.role || "-"
            )}
        </span>
    </td>

                <td>
                    <span class="${statusClass}">
                        ${user.accountStatus || "ACTIVE"}
                    </span>
                </td>

                <td>

    ${
        user.role === "SUPER_ADMIN"
        ?
        `
        <span class="admin-protected">
            🔒 Protected Admin
        </span>
        `
        :
        user.accountStatus === "ACTIVE"
        ?
        `
        <button
            onclick="blockAdminUser(${user.id})">
            Block
        </button>

        <button
            onclick="banAdminUser(${user.id})">
            Ban
        </button>

        <button
            onclick="deleteAdminUser(${user.id})">
            Delete
        </button>
        `
        :
        `
        <button
            onclick="unblockAdminUser(${user.id})">
            Unblock
        </button>

        <button
            onclick="deleteAdminUser(${user.id})">
            Delete
        </button>
        `
    }

</td>
            `;

            tableBody.appendChild(row);
        });

    } catch (error) {

        console.error(
            "Admin users loading failed:",
            error
        );

        tableBody.innerHTML = `
            <tr>
                <td colspan="6">
                    ❌ Unable to load users
                </td>
            </tr>
        `;
    }
}
function escapeHtml(value) {

    const div = document.createElement("div");

    div.textContent = value;

    return div.innerHTML;
}
async function blockAdminUser(userId) {

    if (!confirm(
        "Are you sure you want to block this user?"
    )) {
        return;
    }

    await updateAdminUserStatus(
        userId,
        "block"
    );
}
async function unblockAdminUser(userId) {

    await updateAdminUserStatus(
        userId,
        "unblock"
    );
}
async function banAdminUser(userId) {

    if (!confirm(
        "Are you sure you want to permanently ban this user?"
    )) {
        return;
    }

    await updateAdminUserStatus(
        userId,
        "ban"
    );
}
async function updateAdminUserStatus(
    userId,
    action
) {

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/users/${userId}/${action}`,
            {
                method: "PUT",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            if (response.status === 403) {
                alert("Admin access denied.");
                return;
            }

            throw new Error(
                "Unable to update user"
            );
        }

        alert(
            `User ${action} successful.`
        );

        await loadAdminUsers();

    } catch (error) {

        console.error(error);

        alert(
            "Unable to update user."
        );
    }
}
async function deleteAdminUser(userId) {

    if (!confirm(
        "Are you sure you want to delete this user?"
    )) {
        return;
    }

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/users/${userId}`,
            {
                method: "DELETE",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            if (response.status === 403) {
                alert("Admin access denied.");
                return;
            }

            throw new Error(
                "Unable to delete user"
            );
        }

        alert("User deleted successfully.");

        await loadAdminUsers();

    } catch (error) {

        console.error(error);

        alert(
            "Unable to delete user."
        );
    }
}
function viewAdminUser(userId) {

    console.log(
        "Viewing user:",
        userId
    );

    alert(
        "User ID: " + userId
    );
}


// =====================================================
// INCIDENT ANALYTICS
// =====================================================

async function loadIncidentAnalytics() {

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(
            `${BASE_URL}/api/incident-reports`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (response.status === 403) {

            console.error(
                "Admin access denied for incidents."
            );

            return;
        }

        if (!response.ok) {
            throw new Error(
                "Failed to load incidents"
            );
        }

        const incidents =
            await response.json();

        // ==========================================
        // TOTAL INCIDENTS
        // ==========================================

        const totalIncidents =
            document.getElementById(
                "totalIncidents"
            );

        if (totalIncidents) {
            totalIncidents.textContent =
                incidents.length;
        }

        // ==========================================
        // INCIDENT TYPES
        // ==========================================

        const typeCounts = {};

        incidents.forEach(incident => {

            const type =
                incident.incidentType ||
                "UNKNOWN";

            typeCounts[type] =
                (typeCounts[type] || 0) + 1;
        });

        displayIncidentTypes(typeCounts);

        // ==========================================
        // INCIDENT STATUS
        // ==========================================

        const statusCounts = {};

        incidents.forEach(incident => {

            const status =
                incident.status ||
                "UNKNOWN";

            statusCounts[status] =
                (statusCounts[status] || 0) + 1;
        });

        displayIncidentStatuses(statusCounts);

    } catch (error) {

        console.error(
            "Incident analytics error:",
            error
        );

        const typeContainer =
            document.getElementById(
                "incidentTypeStats"
            );

        const statusContainer =
            document.getElementById(
                "incidentStatusStats"
            );

        if (typeContainer) {
            typeContainer.innerHTML =
                "Unable to load incident data.";
        }

        if (statusContainer) {
            statusContainer.innerHTML =
                "Unable to load incident data.";
        }
    }
}
function displayIncidentTypes(typeCounts) {

    const container =
        document.getElementById(
            "incidentTypeStats"
        );

    if (!container) {
        return;
    }

    container.innerHTML = "";

    const types =
        Object.keys(typeCounts);

    if (types.length === 0) {

        container.innerHTML =
            "<p>No incidents reported.</p>";

        return;
    }

    types.forEach(type => {

        const row =
            document.createElement("div");

        row.className =
            "admin-stat-row";

        row.innerHTML = `
            <span>${escapeHtml(type)}</span>
            <strong>${typeCounts[type]}</strong>
        `;

        container.appendChild(row);
    });
}
function displayIncidentStatuses(statusCounts) {

    const container =
        document.getElementById(
            "incidentStatusStats"
        );

    if (!container) {
        return;
    }

    container.innerHTML = "";

    const statuses =
        Object.keys(statusCounts);

    if (statuses.length === 0) {

        container.innerHTML =
            "<p>No incident status data.</p>";

        return;
    }

    statuses.forEach(status => {

        const row =
            document.createElement("div");

        row.className =
            "admin-stat-row";

        row.innerHTML = `
            <span>${escapeHtml(status)}</span>
            <strong>${statusCounts[status]}</strong>
        `;

        container.appendChild(row);
    });
}
let incidentTrendChart = null;

function displayIncidentTrend(incidents) {

    const canvas =
        document.getElementById("incidentTrendChart");

    if (!canvas) {
        console.error("incidentTrendChart not found");
        return;
    }

    const dailyCounts = {};

    incidents.forEach(incident => {

        if (!incident.reportedAt) {
            console.warn(
                "Incident has no reportedAt:",
                incident
            );
            return;
        }

        // Convert backend LocalDateTime to date
        const date =
            incident.reportedAt.substring(0, 10);

        dailyCounts[date] =
            (dailyCounts[date] || 0) + 1;
    });

    const dates =
        Object.keys(dailyCounts).sort();

    const counts =
        dates.map(date => dailyCounts[date]);

    console.log("Incident trend dates:", dates);
    console.log("Incident trend counts:", counts);

    if (incidentTrendChart) {
        incidentTrendChart.destroy();
    }

    incidentTrendChart =
        new Chart(canvas, {

            type: "line",

            data: {
                labels: dates,

                datasets: [{
                    label: "Incidents",
                    data: counts,
                    tension: 0.3,
                    fill: false
                }]
            },

            options: {
                responsive: true,

                plugins: {
                    legend: {
                        display: true
                    }
                },

                scales: {

                    x: {
                        title: {
                            display: true,
                            text: "Date"
                        }
                    },

                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        },
                        title: {
                            display: true,
                            text: "Number of Incidents"
                        }
                    }
                }
            }
        });
}
// =====================================================
// SOS STATISTICS
// =====================================================

async function loadSOSStatistics() {

    const token = localStorage.getItem("token");

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/incidents`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            console.error(
                "Failed to load SOS incidents:",
                response.status
            );

            return;
        }

        const incidents =
            await response.json();
            displayIncidentTrend(incidents);

        const totalSOS =
            document.getElementById("totalSOS");

        if (totalSOS) {

            totalSOS.textContent =
                incidents.length;
        }

    } catch (error) {

        console.error(
            "SOS statistics error:",
            error
        );
    }
}


// =====================================================
// CRIME HOTSPOTS
// =====================================================

async function loadCrimeHotspots() {

    console.log("Loading crime hotspots...");

    const mapElement =
        document.getElementById("adminCrimeMap");

    if (!mapElement) {
        return;
    }

    if (typeof L === "undefined") {

        console.error("Leaflet is not loaded.");
        return;
    }

    if (typeof L.heatLayer === "undefined") {

        console.error(
            "Leaflet Heat plugin is not loaded."
        );

        return;
    }

    const token =
        localStorage.getItem("token");

    try {

        // ==========================================
        // GET SOS LOCATIONS
        // ==========================================

        const sosResponse = await fetch(
            `${BASE_URL}/api/admin/locations`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!sosResponse.ok) {

            console.error(
                "Failed to load SOS locations:",
                sosResponse.status
            );

            return;
        }

        const sosLocations =
            await sosResponse.json();


        // ==========================================
        // GET INCIDENT REPORTS
        // ==========================================

        const incidentResponse = await fetch(
            `${BASE_URL}/api/incident-reports`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!incidentResponse.ok) {

            console.error(
                "Failed to load incident reports:",
                incidentResponse.status
            );

            return;
        }

        const incidentReports =
            await incidentResponse.json();


        // ==========================================
        // CREATE MAP
        // ==========================================

        if (!adminCrimeMap) {

            adminCrimeMap =
                L.map("adminCrimeMap")
                    .setView(
                        [16.3067, 80.4365],
                        7
                    );

            L.tileLayer(
                "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
                {
                    maxZoom: 19,
                    attribution:
                        "&copy; OpenStreetMap contributors"
                }
            ).addTo(adminCrimeMap);
        }


        // ==========================================
        // REMOVE OLD HEAT LAYER
        // ==========================================

        if (adminCrimeHeatLayer) {

            adminCrimeMap.removeLayer(
                adminCrimeHeatLayer
            );

            adminCrimeHeatLayer = null;
        }


        // ==========================================
        // PREPARE SOS LOCATIONS
        // ==========================================

        const heatPoints = [];

        sosLocations.forEach(location => {

            if (
                location.latitude == null ||
                location.longitude == null
            ) {
                return;
            }

            heatPoints.push([
                Number(location.latitude),
                Number(location.longitude),

                // SOS gets strong weight
                1.0
            ]);
        });


        // ==========================================
        // PREPARE INCIDENT REPORT LOCATIONS
        // ==========================================

        incidentReports.forEach(report => {

            if (
                report.latitude == null ||
                report.longitude == null
            ) {
                return;
            }

            heatPoints.push([
                Number(report.latitude),
                Number(report.longitude),

                // Incident report weight
                0.8
            ]);
        });


        // ==========================================
        // CREATE COMBINED HEAT MAP
        // ==========================================

        if (heatPoints.length > 0) {

            adminCrimeHeatLayer =
                L.heatLayer(
                    heatPoints,
                    {
                        radius: 30,
                        blur: 20,
                        maxZoom: 17,
                        minOpacity: 0.35
                    }
                ).addTo(adminCrimeMap);
        }


        console.log(
            "SOS locations:",
            sosLocations.length
        );

        console.log(
            "Incident reports:",
            incidentReports.length
        );

        console.log(
            "Total hotspot points:",
            heatPoints.length
        );

    } catch (error) {

        console.error(
            "Crime hotspot error:",
            error
        );
    }
}


// =====================================================
// RESPONSE MONITORING
// =====================================================

async function loadResponseMonitoring() {

    const token = localStorage.getItem("token");

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/incidents`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            console.error(
                "Failed to load response monitoring:",
                response.status
            );

            return;
        }

        const incidents =
            await response.json();

        let activeCount = 0;
        let pendingCount = 0;
        let resolvedCount = 0;

        incidents.forEach(incident => {

            const status =
                (incident.emergencyResponseStatus || "")
                    .toUpperCase();

            if (status === "RESOLVED") {

                resolvedCount++;

            } else if (
                status === "PENDING_RESPONSE"
            ) {

                pendingCount++;

            } else {

                activeCount++;
            }
        });

        const activeElement =
            document.getElementById(
                "activeIncidents"
            );

        const pendingElement =
            document.getElementById(
                "pendingResponses"
            );

        const resolvedElement =
            document.getElementById(
                "resolvedIncidents"
            );

        if (activeElement) {
            activeElement.textContent =
                activeCount;
        }

        if (pendingElement) {
            pendingElement.textContent =
                pendingCount;
        }

        if (resolvedElement) {
            resolvedElement.textContent =
                resolvedCount;
        }

    } catch (error) {

        console.error(
            "Response monitoring error:",
            error
        );
    }
}


// =====================================================
// EMERGENCY CONTACT STATISTICS
// =====================================================

async function loadEmergencyContactStatistics() {

    console.log(
        "Loading emergency contact statistics..."
    );

    const token =
        localStorage.getItem("token");

    try {

        const response = await fetch(
            `${BASE_URL}/api/admin/contact-statistics`,
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            console.error(
                "Failed to load emergency contact statistics:",
                response.status
            );

            return;
        }

        const data =
            await response.json();

        const totalContacts =
            document.getElementById(
                "totalEmergencyContacts"
            );

        const calls =
            document.getElementById(
                "emergencyCalls"
            );

        const messages =
            document.getElementById(
                "emergencyMessages"
            );
            const emails =
            document.getElementById(
                "emergencyEmails"
            );

        if (totalContacts) {

            totalContacts.textContent =
                data.totalContacts;
        }

        if (calls) {

            calls.textContent =
                data.totalCalls;
        }

        if (messages) {

            messages.textContent =
                data.totalMessages;
        }
        if (emails) {

            emails.textContent =
                data.totalEmails;
        }


    } catch (error) {

        console.error(
            "Emergency contact statistics error:",
            error
        );
    }
}


// =====================================================
// APPLICATION HEALTH
// =====================================================

async function checkApplicationHealth() {

    const backendHealth =
        document.getElementById("backendHealth");

    const backendHealthIcon =
        document.getElementById("backendHealthIcon");

    const databaseHealth =
        document.getElementById("databaseHealth");

    const databaseHealthIcon =
        document.getElementById("databaseHealthIcon");

    const applicationHealth =
        document.getElementById("applicationHealth");

    const applicationHealthIcon =
        document.getElementById("applicationHealthIcon");


    // Initial state

    if (backendHealth) {
        backendHealth.textContent = "Checking...";
    }

    if (databaseHealth) {
        databaseHealth.textContent = "Checking...";
    }

    if (applicationHealth) {
        applicationHealth.textContent = "Checking...";
    }


    try {

        const response =
            await fetch(
                `${BASE_URL}/actuator/health`
            );


        // ==========================================
        // BACKEND API
        // ==========================================

        if (response.ok) {

            const data =
                await response.json();


            if (backendHealth) {
                backendHealth.textContent =
                    "Online";
            }

            if (backendHealthIcon) {
                backendHealthIcon.textContent =
                    "🟢";
            }


            // ======================================
            // DATABASE
            // ======================================

            if (
                data.components &&
                data.components.db
            ) {

                const dbStatus =
                    data.components.db.status;


                if (
                    dbStatus === "UP"
                ) {

                    if (databaseHealth) {
                        databaseHealth.textContent =
                            "Connected";
                    }

                    if (databaseHealthIcon) {
                        databaseHealthIcon.textContent =
                            "🟢";
                    }

                } else {

                    if (databaseHealth) {
                        databaseHealth.textContent =
                            "Unavailable";
                    }

                    if (databaseHealthIcon) {
                        databaseHealthIcon.textContent =
                            "🔴";
                    }
                }

            } else {

                if (databaseHealth) {
                    databaseHealth.textContent =
                        "Not Reported";
                }

                if (databaseHealthIcon) {
                    databaseHealthIcon.textContent =
                        "🟡";
                }
            }


            // ======================================
            // APPLICATION
            // ======================================

            if (data.status === "UP") {

                if (applicationHealth) {
                    applicationHealth.textContent =
                        "System Healthy";
                }

                if (applicationHealthIcon) {
                    applicationHealthIcon.textContent =
                        "🟢";
                }

            } else {

                if (applicationHealth) {
                    applicationHealth.textContent =
                        "System Warning";
                }

                if (applicationHealthIcon) {
                    applicationHealthIcon.textContent =
                        "🟠";
                }
            }


        } else {

            if (backendHealth) {
                backendHealth.textContent =
                    "Unavailable";
            }

            if (backendHealthIcon) {
                backendHealthIcon.textContent =
                    "🔴";
            }

            if (databaseHealth) {
                databaseHealth.textContent =
                    "Unavailable";
            }

            if (databaseHealthIcon) {
                databaseHealthIcon.textContent =
                    "🔴";
            }

            if (applicationHealth) {
                applicationHealth.textContent =
                    "System Unavailable";
            }

            if (applicationHealthIcon) {
                applicationHealthIcon.textContent =
                    "🔴";
            }
        }


    } catch (error) {

        console.error(
            "Application health error:",
            error
        );


        if (backendHealth) {
            backendHealth.textContent =
                "Backend Unavailable";
        }

        if (backendHealthIcon) {
            backendHealthIcon.textContent =
                "🔴";
        }


        if (databaseHealth) {
            databaseHealth.textContent =
                "Database Unavailable";
        }

        if (databaseHealthIcon) {
            databaseHealthIcon.textContent =
                "🔴";
        }


        if (applicationHealth) {
            applicationHealth.textContent =
                "System Unavailable";
        }

        if (applicationHealthIcon) {
            applicationHealthIcon.textContent =
                "🔴";
        }
    }
}

// =====================================================
// INITIALIZE
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    function() {

        loadAdminDashboard();

    }
);

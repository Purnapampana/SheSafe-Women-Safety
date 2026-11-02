// ============================================================
// SHESAFE - SAFE ROUTE
// Clean single implementation
// ============================================================

"use strict";

// ============================================================
// GLOBAL STATE
// ============================================================

window.safeRouteMap = null;

window.safeRouteData = null;
window.safeRouteSelectedRoute = null;

window.safestRouteLayer = null;
window.alternativeRouteLayer = null;

window.safeRouteStartMarker = null;
window.safeRouteDestinationMarker = null;

window.safeRouteFeatureMarkers = [];

window.safeRouteLatitude = null;
window.safeRouteLongitude = null;

// Live navigation
window.safeRouteNavigationActive = false;
window.safeRouteWatchId = null;

window.safeRouteCurrentMarker = null;
window.safeRouteAccuracyCircle = null;

window.safeRouteProgressLayer = null;
window.safeRouteRemainingLayer = null;


// ============================================================
// GET CURRENT LOCATION
// ============================================================

function getSafeRouteLocation() {

    const status =
        document.getElementById("routeLocationStatus");

    if (!navigator.geolocation) {

        if (status) {
            status.innerText =
                "Geolocation is not supported by this browser.";
        }

        return;
    }

    if (status) {
        status.innerText =
            "Getting your current location...";
    }

    navigator.geolocation.getCurrentPosition(

        function (position) {

            window.safeRouteLatitude =
                position.coords.latitude;

            window.safeRouteLongitude =
                position.coords.longitude;

            const input =
                document.getElementById("startAddress");

            if (input) {
                input.value = "Current Location";
            }

            if (status) {
                status.innerText =
                    "📍 Current location detected.";
            }

            console.log(
                "Safe Route Current Location:",
                window.safeRouteLatitude,
                window.safeRouteLongitude
            );
        },

        function (error) {

            console.error(
                "Safe Route Geolocation Error:",
                error
            );

            if (!status) {
                return;
            }

            switch (error.code) {

                case 1:
                    status.innerText =
                        "Location permission was denied.";
                    break;

                case 2:
                    status.innerText =
                        "Current location is unavailable.";
                    break;

                case 3:
                    status.innerText =
                        "Location request timed out.";
                    break;

                default:
                    status.innerText =
                        "Unable to get current location.";
            }
        },

        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
    );
}


// ============================================================
// CLEAR CURRENT LOCATION WHEN USER TYPES
// ============================================================

function clearCurrentLocationWhenTyping() {

    const input =
        document.getElementById("startAddress");

    if (!input) {
        return;
    }

    if (
        input.value.trim() !==
        "Current Location"
    ) {

        window.safeRouteLatitude = null;
        window.safeRouteLongitude = null;
    }
}


// ============================================================
// FIND SAFE ROUTE
// ============================================================

async function findSafeRoute() {

    const status =
        document.getElementById("safeRouteStatus");

    const result =
        document.getElementById("safeRouteResult");

    const startInput =
        document.getElementById("startAddress");

    const destinationInput =
        document.getElementById("destination");


    if (!destinationInput) {

        console.error(
            "Destination input not found."
        );

        return;
    }


    const startAddress =
        startInput
            ? startInput.value.trim()
            : "";

    const destination =
        destinationInput.value.trim();


    // --------------------------------------------------------
    // VALIDATE DESTINATION
    // --------------------------------------------------------

    if (!destination) {

        if (status) {
            status.innerText =
                "Please enter a destination.";
        }

        return;
    }


    // --------------------------------------------------------
    // VALIDATE START LOCATION
    // --------------------------------------------------------

    if (
        window.safeRouteLatitude == null ||
        window.safeRouteLongitude == null
    ) {

        if (!startAddress) {

            if (status) {
                status.innerText =
                    "Please select your current location or enter a starting location.";
            }

            return;
        }
    }


    // --------------------------------------------------------
    // STOP PREVIOUS NAVIGATION
    // --------------------------------------------------------

    stopLiveNavigation();


    // --------------------------------------------------------
    // STATUS
    // --------------------------------------------------------

    if (status) {
        status.innerText =
            "Calculating safe routes...";
    }

    if (result) {
        result.innerHTML = "";
    }


    try {

        // ----------------------------------------------------
        // REQUEST BODY
        // ----------------------------------------------------

        const requestBody = {

            startLatitude:
                window.safeRouteLatitude,

            startLongitude:
                window.safeRouteLongitude,

            startAddress:
                startAddress,

            destinationLatitude:
                null,

            destinationLongitude:
                null,

            destinationAddress:
                destination
        };


        console.log(
            "Safe Route Request:",
            requestBody
        );


        // ----------------------------------------------------
        // JWT
        // ----------------------------------------------------

        const token =
            localStorage.getItem("token");

        if (!token) {

            if (status) {
                status.innerText =
                    "Please login again before using Safe Route.";
            }

            return;
        }


        // ----------------------------------------------------
        // BACKEND REQUEST
        // ----------------------------------------------------

        const response =
            await fetch(
                `${BASE_URL}/api/safe-route`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Authorization":
                            `Bearer ${token}`
                    },

                    body:
                        JSON.stringify(
                            requestBody
                        )
                }
            );


        // ----------------------------------------------------
        // READ RESPONSE
        // ----------------------------------------------------

        const responseText =
            await response.text();

        console.log(
            "Safe Route HTTP Status:",
            response.status
        );

        console.log(
            "Safe Route Raw Response:",
            responseText
        );


        let data;

        try {

            data =
                JSON.parse(
                    responseText
                );

        } catch (error) {

            throw new Error(
                `Server returned HTTP ${response.status}: ${
                    responseText ||
                    "No response body"
                }`
            );
        }


        // ----------------------------------------------------
        // HTTP ERROR
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                data.message ||
                data.error ||
                `Safe route request failed. HTTP ${response.status}`
            );
        }


        // ----------------------------------------------------
        // VALIDATE ROUTES
        // ----------------------------------------------------

        if (
            !data ||
            !Array.isArray(data.routes) ||
            data.routes.length === 0
        ) {

            throw new Error(
                "No routes were returned by the server."
            );
        }


        // ----------------------------------------------------
        // SAVE DATA
        // ----------------------------------------------------

        window.safeRouteData =
            data;


        // ----------------------------------------------------
        // SELECT RECOMMENDED ROUTE
        // ----------------------------------------------------

        window.safeRouteSelectedRoute =
            data.routes.find(
                function (route) {

                    return (
                        route.routeName ===
                        data.recommendedRoute
                    );
                }
            ) ||
            data.routes[0];


        console.log(
            "Safe Route Response:",
            data
        );


        // ----------------------------------------------------
        // RENDER MAP
        // ----------------------------------------------------

        displaySafeRouteMap();


        // ----------------------------------------------------
        // RENDER ROUTE CARDS
        // ----------------------------------------------------

        renderSafeRouteCards(
            data
        );


        // ----------------------------------------------------
        // SUCCESS
        // ----------------------------------------------------

        if (status) {

            status.innerText =
                "Routes calculated successfully.";
        }


        // ----------------------------------------------------
        // SCROLL TO MAP
        // ----------------------------------------------------

        setTimeout(
            function () {

                const mapSection =
                    document.getElementById(
                        "safeRouteMapSection"
                    );

                if (mapSection) {

                    mapSection.scrollIntoView({
                        behavior: "smooth",
                        block: "start"
                    });
                }

            },
            300
        );


    } catch (error) {

        console.error(
            "SAFE ROUTE ERROR:",
            error
        );


        if (status) {

            status.innerText =
                " ❌ Unable to calculate safe route.";
        }


        if (result) {

            result.innerHTML = `

                <div
                    class="error-message"
                    style="
                        padding:15px;
                        margin-top:15px;
                        border-radius:10px;
                        background:#fee2e2;
                        color:#991b1b;
                        border:1px solid #fecaca;
                    "
                >

                    ❌ ${escapeHtml(
                        error.message ||
                        "Unknown error"
                    )}

                </div>

            `;
        }
    }
}


// ============================================================
// DISPLAY SAFE ROUTE MAP
// ============================================================

function displaySafeRouteMap() {

    console.log(
        "================================="
    );

    console.log(
        "DISPLAYING SAFE ROUTE MAP"
    );

    console.log(
        "================================="
    );


    const mapContainer =
        document.getElementById(
            "safeRouteMap"
        );

    const mapSection =
        document.getElementById(
            "safeRouteMapSection"
        );


    // --------------------------------------------------------
    // CHECK CONTAINER
    // --------------------------------------------------------

    if (!mapContainer) {

        console.error(
            "❌ #safeRouteMap does not exist in DOM."
        );

        return;
    }


    if (!mapSection) {

        console.error(
            "❌ #safeRouteMapSection does not exist."
        );

        return;
    }


    const data =
        window.safeRouteData;


    if (!data) {

        console.error(
            "❌ Safe route data not available."
        );

        return;
    }


    // --------------------------------------------------------
    // REMOVE OLD LEAFLET MAP
    // --------------------------------------------------------

    if (window.safeRouteMap) {

        try {

            window.safeRouteMap.remove();

        } catch (error) {

            console.warn(
                "Old Leaflet map removal failed:",
                error
            );
        }

        window.safeRouteMap = null;
    }


    // --------------------------------------------------------
    // RESET LAYERS
    // --------------------------------------------------------

    window.safestRouteLayer = null;
    window.alternativeRouteLayer = null;

    window.safeRouteStartMarker = null;
    window.safeRouteDestinationMarker = null;

    window.safeRouteFeatureMarkers = [];


    // --------------------------------------------------------
    // NEVER REMOVE THE MAP ELEMENT
    // --------------------------------------------------------

    mapContainer.innerHTML = "";


    // --------------------------------------------------------
    // MAKE MAP VISIBLE
    // --------------------------------------------------------

    mapSection.style.display = "block";
    mapSection.style.visibility = "visible";
    mapSection.style.opacity = "1";

    mapContainer.style.display = "block";
    mapContainer.style.visibility = "visible";
    mapContainer.style.opacity = "1";

    mapContainer.style.width = "100%";
    mapContainer.style.height = "510px";
    mapContainer.style.minHeight = "510px";

    mapContainer.style.position = "relative";
    mapContainer.style.overflow = "hidden";
    mapContainer.style.boxSizing = "border-box";


    console.log(
        "MAP BEFORE LEAFLET:",
        mapContainer.getBoundingClientRect()
    );

    console.log(
        "MAP WIDTH:",
        mapContainer.offsetWidth
    );

    console.log(
        "MAP HEIGHT:",
        mapContainer.offsetHeight
    );


    // --------------------------------------------------------
    // CHECK LEAFLET
    // --------------------------------------------------------

    if (
        typeof L === "undefined"
    ) {

        console.error(
            "❌ Leaflet is not loaded."
        );

        mapContainer.innerHTML = `

            <div
                style="
                    padding:20px;
                    color:#991b1b;
                    background:#fee2e2;
                    border:1px solid #fecaca;
                    border-radius:10px;
                "
            >
                ❌ Map library failed to load.
                Please check the Leaflet CDN.
            </div>

        `;

        return;
    }


    // --------------------------------------------------------
    // CREATE LEAFLET MAP
    // --------------------------------------------------------

    window.safeRouteMap =
        L.map(
            mapContainer,
            {
                zoomControl: true,
                attributionControl: true
            }
        );


    console.log(
        "✅ Leaflet map created."
    );


    // --------------------------------------------------------
    // OPEN STREET MAP
    // --------------------------------------------------------

    L.tileLayer(
        "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        {
            maxZoom: 19,
            minZoom: 3,

            attribution:
                "&copy; OpenStreetMap contributors"
        }
    ).addTo(
        window.safeRouteMap
    );


    // --------------------------------------------------------
    // START COORDINATES
    // --------------------------------------------------------

    const startLat =
        Number(
            data.startLatitude
        );

    const startLon =
        Number(
            data.startLongitude
        );


    // --------------------------------------------------------
    // DESTINATION COORDINATES
    // --------------------------------------------------------

    const destinationLat =
        Number(
            data.destinationLatitude
        );

    const destinationLon =
        Number(
            data.destinationLongitude
        );


    // --------------------------------------------------------
    // INITIAL MAP CENTER
    // --------------------------------------------------------

    if (
        Number.isFinite(startLat) &&
        Number.isFinite(startLon)
    ) {

        window.safeRouteMap.setView(
            [
                startLat,
                startLon
            ],
            14
        );

    } else {

        // India fallback if backend does not return start
        // coordinates.

        window.safeRouteMap.setView(
            [
                20.5937,
                78.9629
            ],
            5
        );
    }


    // --------------------------------------------------------
    // FIND SAFEST ROUTE
    // --------------------------------------------------------

    const safestRoute =
        data.routes.find(
            function (route) {

                return (
                    route.routeName ===
                    "Safest Route"
                );
            }
        );


    // --------------------------------------------------------
    // FIND ALTERNATIVE ROUTE
    // --------------------------------------------------------

    const alternativeRoute =
        data.routes.find(
            function (route) {

                return (
                    route.routeName ===
                    "Alternative Route"
                );
            }
        );


    // --------------------------------------------------------
    // DRAW SAFEST ROUTE
    // --------------------------------------------------------

    if (
        safestRoute &&
        Array.isArray(
            safestRoute.coordinates
        )
    ) {

        const coordinates =
            normalizeRouteCoordinates(
                safestRoute.coordinates
            );


        console.log(
            "Safest route coordinates:",
            coordinates.length
        );


        if (
            coordinates.length >= 2
        ) {

            window.safestRouteLayer =
                L.polyline(
                    coordinates,
                    {
                        color: "#2563eb",

                        weight: 8,

                        opacity: 1,

                        lineCap: "round",

                        lineJoin: "round"
                    }
                ).addTo(
                    window.safeRouteMap
                );
        }
    }


    // --------------------------------------------------------
    // DRAW ALTERNATIVE ROUTE
    // --------------------------------------------------------

    if (
        alternativeRoute &&
        Array.isArray(
            alternativeRoute.coordinates
        )
    ) {

        const coordinates =
            normalizeRouteCoordinates(
                alternativeRoute.coordinates
            );


        console.log(
            "Alternative route coordinates:",
            coordinates.length
        );


        if (
            coordinates.length >= 2
        ) {

            window.alternativeRouteLayer =
                L.polyline(
                    coordinates,
                    {
                        color: "#64748b",

                        weight: 6,

                        opacity: 0.75,

                        dashArray: "10,10",

                        lineCap: "round",

                        lineJoin: "round"
                    }
                ).addTo(
                    window.safeRouteMap
                );
        }
    }


    // --------------------------------------------------------
    // START MARKER
    // --------------------------------------------------------

    if (
        Number.isFinite(startLat) &&
        Number.isFinite(startLon)
    ) {

        window.safeRouteStartMarker =
            L.marker(
                [
                    startLat,
                    startLon
                ]
            )
            .addTo(
                window.safeRouteMap
            )
            .bindPopup(
                "<b>📍 Starting Location</b>"
            );
    }


    // --------------------------------------------------------
    // DESTINATION MARKER
    // --------------------------------------------------------

    if (
        Number.isFinite(destinationLat) &&
        Number.isFinite(destinationLon)
    ) {

        window.safeRouteDestinationMarker =
            L.marker(
                [
                    destinationLat,
                    destinationLon
                ]
            )
            .addTo(
                window.safeRouteMap
            )
            .bindPopup(
                "<b>🎯 Destination</b>"
            );
    }


    // --------------------------------------------------------
    // SAFETY / OSM FEATURES
    // --------------------------------------------------------

    if (
        Array.isArray(data.routes)
    ) {

        window.safeRouteFeatureMarkers = [];

if (window.safeRouteSelectedRoute) {

    addSafetyFeatureMarkers(
        window.safeRouteMap,
        window.safeRouteSelectedRoute
    );

}
    }


    // --------------------------------------------------------
    // FIT ROUTES TO MAP
    // --------------------------------------------------------

    const boundsLayers = [];


    if (
        window.safestRouteLayer
    ) {

        boundsLayers.push(
            window.safestRouteLayer
        );
    }


    if (
        window.alternativeRouteLayer
    ) {

        boundsLayers.push(
            window.alternativeRouteLayer
        );
    }


    if (
        window.safeRouteStartMarker
    ) {

        boundsLayers.push(
            window.safeRouteStartMarker
        );
    }


    if (
        window.safeRouteDestinationMarker
    ) {

        boundsLayers.push(
            window.safeRouteDestinationMarker
        );
    }


    if (
        boundsLayers.length > 0
    ) {

        const group =
            L.featureGroup(
                boundsLayers
            );


        const bounds =
            group.getBounds();


        if (
            bounds.isValid()
        ) {

            window.safeRouteMap.fitBounds(
                bounds,
                {
                    padding: [
                        40,
                        40
                    ]
                }
            );
        }
    }


    // --------------------------------------------------------
    // HIGHLIGHT SELECTED ROUTE
    // --------------------------------------------------------

    highlightSelectedRoute();


    // --------------------------------------------------------
    // IMPORTANT LEAFLET FIX
    // --------------------------------------------------------

    setTimeout(
        function () {

            if (
                window.safeRouteMap
            ) {

                window.safeRouteMap.invalidateSize(
                    true
                );

                console.log(
                    "MAP FINAL RECT:",
                    mapContainer.getBoundingClientRect()
                );

                console.log(
                    "MAP FINAL WIDTH:",
                    mapContainer.offsetWidth
                );

                console.log(
                    "MAP FINAL HEIGHT:",
                    mapContainer.offsetHeight
                );
            }

        },
        300
    );


    console.log(
        "✅ Safe Route map displayed successfully."
    );
    // =====================================================
// RENDER BACKEND SAFETY FEATURES
// =====================================================
const allRoutes = window.safeRouteData?.routes || [];

allRoutes.forEach((route, routeIndex) => {

    const features = route.mapFeatures || [];

    console.log(
        `🛡️ Route ${routeIndex + 1} mapFeatures:`,
        features
    );

    features.forEach((feature, featureIndex) => {

        // Support common backend coordinate names
        const lat = Number(
            feature.latitude ??
            feature.lat ??
            feature.location?.latitude ??
            feature.location?.lat
        );

        const lng = Number(
            feature.longitude ??
            feature.lng ??
            feature.lon ??
            feature.location?.longitude ??
            feature.location?.lng ??
            feature.location?.lon
        );

        if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
            console.warn(
                "⚠️ Invalid mapFeature coordinates:",
                feature
            );
            return;
        }

        // Support different backend naming conventions
        const type = String(
            feature.type ??
            feature.category ??
            feature.featureType ??
            feature.placeType ??
            "OTHER"
        ).toUpperCase();

        const name =
            feature.name ??
            feature.title ??
            feature.placeName ??
            "Safety Location";

        // Choose icon
        let icon = "📍";
        let cssClass = "other";

        if (
            type.includes("POLICE") ||
            type.includes("STATION")
        ) {
            icon = "👮";
            cssClass = "police";
        }
        else if (
            type.includes("HOSPITAL") ||
            type.includes("MEDICAL")
        ) {
            icon = "🏥";
            cssClass = "hospital";
        }
        else if (
            type.includes("HOTEL")
        ) {
            icon = "🏨";
            cssClass = "hotel";
        }
        else if (
            type.includes("PHARMACY") ||
            type.includes("MEDICINE")
        ) {
            icon = "💊";
            cssClass = "pharmacy";
        }
        else if (
            type.includes("FIRE")
        ) {
            icon = "🚒";
            cssClass = "fire";
        }
        else if (
            type.includes("SCHOOL")
        ) {
            icon = "🏫";
            cssClass = "school";
        }

        const safetyIcon = L.divIcon({
            className: "shesafe-backend-marker",
            html: `
                <div class="shesafe-poi-marker ${cssClass}">
                    <span>${icon}</span>
                </div>
            `,
            iconSize: [42, 42],
            iconAnchor: [21, 21],
            popupAnchor: [0, -21]
        });

        const marker = L.marker(
            [lat, lng],
            {
                icon: safetyIcon,
                zIndexOffset: 1000
            }
        ).addTo(window.safeRouteMap);

        marker.bindPopup(`
            <div class="shesafe-feature-popup">
                <strong>${escapeHtml(name)}</strong>
                <br>
                <span>${escapeHtml(type)}</span>
            </div>
        `);

        console.log(
            `📍 Rendered ${type}: ${name} (${lat}, ${lng})`
        );
    });
});
function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
}


// ============================================================
// NORMALIZE ROUTE COORDINATES
// ============================================================

function normalizeRouteCoordinates(
    coordinates
) {

    if (
        !Array.isArray(
            coordinates
        )
    ) {

        return [];
    }


    return coordinates

        .map(
            function (point) {

                // --------------------------------------------
                // [latitude, longitude]
                // --------------------------------------------

                if (
                    Array.isArray(point) &&
                    point.length >= 2
                ) {

                    const lat =
                        Number(point[0]);

                    const lon =
                        Number(point[1]);


                    if (
                        Number.isFinite(lat) &&
                        Number.isFinite(lon)
                    ) {

                        return [
                            lat,
                            lon
                        ];
                    }
                }


                // --------------------------------------------
                // { latitude, longitude }
                // --------------------------------------------

                if (
                    point &&
                    point.latitude != null &&
                    point.longitude != null
                ) {

                    const lat =
                        Number(
                            point.latitude
                        );

                    const lon =
                        Number(
                            point.longitude
                        );


                    if (
                        Number.isFinite(lat) &&
                        Number.isFinite(lon)
                    ) {

                        return [
                            lat,
                            lon
                        ];
                    }
                }


                // --------------------------------------------
                // { lat, lng }
                // --------------------------------------------

                if (
                    point &&
                    point.lat != null &&
                    point.lng != null
                ) {

                    const lat =
                        Number(
                            point.lat
                        );

                    const lon =
                        Number(
                            point.lng
                        );


                    if (
                        Number.isFinite(lat) &&
                        Number.isFinite(lon)
                    ) {

                        return [
                            lat,
                            lon
                        ];
                    }
                }


                return null;
            }
        )

        .filter(
            function (point) {

                return (
                    point &&
                    Number.isFinite(point[0]) &&
                    Number.isFinite(point[1])
                );
            }
        );
}


// ============================================================
// RENDER ROUTE CARDS
// ============================================================

function renderSafeRouteCards(
    data
) {

    const result =
        document.getElementById(
            "safeRouteResult"
        );


    if (!result) {
        return;
    }


    result.innerHTML = "";


    if (
        !data ||
        !Array.isArray(data.routes)
    ) {

        return;
    }


    const recommendedName =
        data.recommendedRoute;


    data.routes.forEach(
        function (route, index) {

            const isRecommended =
                route.routeName ===
                recommendedName;


            const isSelected =
                window.safeRouteSelectedRoute ===
                route;


            const card =
                document.createElement(
                    "div"
                );


            card.className =
                "route-card";


            if (
                isRecommended ||
                isSelected
            ) {

                card.classList.add(
                    "selected"
                );
            }


            // ------------------------------------------------
            // TRAFFIC
            // ------------------------------------------------

            const trafficLevel =
                route.trafficLevel ||
                getTrafficLevel(
                    route.trafficScore
                );


            const trafficClass =
                trafficLevel === "LOW"

                    ? "traffic-low"

                    : trafficLevel === "MEDIUM"

                        ? "traffic-medium"

                        : "traffic-high";


            // ------------------------------------------------
            // ICON
            // ------------------------------------------------

            const routeIcon =
                route.routeName ===
                "Alternative Route"

                    ? "🔵"

                    : "🛡️";


            // ------------------------------------------------
            // RECOMMENDED BADGE
            // ------------------------------------------------

            const recommendedBadge =
                isRecommended

                    ? `
                        <span
                            style="
                                display:inline-block;
                                margin-left:8px;
                                padding:4px 8px;
                                border-radius:20px;
                                background:#dcfce7;
                                color:#166534;
                                font-size:12px;
                                font-weight:bold;
                            "
                        >
                            RECOMMENDED
                        </span>
                    `

                    : "";


            // ------------------------------------------------
            // CARD
            // ------------------------------------------------

            card.innerHTML = `

                <div class="route-title">

                    ${routeIcon}

                    ${escapeHtml(
                        route.routeName ||
                        "Route"
                    )}

                    ${recommendedBadge}

                </div>


                <div class="route-details">

                    <div class="route-detail">

                        📏 Distance

                        <br>

                        <strong>
                            ${formatNumber(
                                route.distanceKm
                            )} km
                        </strong>

                    </div>


                    <div class="route-detail">

                        ⏱️ Duration

                        <br>

                        <strong>
                            ${formatNumber(
                                route.durationMinutes
                            )} min
                        </strong>

                    </div>


                    <div class="route-detail">

                        🤖 AI Risk

                        <br>

                        <strong>
                            ${formatNumber(
                                route.riskScore
                            )}
                        </strong>

                    </div>


                    <div class="route-detail">

                        ⚠️ Risk Level

                        <br>

                        <strong>
                            ${formatRiskZone(
                                route.riskZone,
                                route.riskScore
                            )}
                        </strong>

                    </div>


                    <div class="route-detail">

                        🚦 Traffic

                        <br>

                        <strong
                            class="${trafficClass}"
                        >
                            ${escapeHtml(
                                trafficLevel
                            )}
                        </strong>

                    </div>

                </div>


                <div class="route-actions">

                    <button
                        type="button"
                        class="route-button select-route-button"
                        onclick="selectSafeRoute(${index})"
                    >
                        ${
                            isSelected
                                ? "✓ Selected"
                                : "Select Route"
                        }
                    </button>


                    <button
                        type="button"
                        class="route-button navigation-button"
                        onclick="startRouteNavigation(${index})"
                    >
                        ▶ Start Navigation
                    </button>

                </div>

            `;


            result.appendChild(
                card
            );
        }
    );
}


// ============================================================
// SELECT ROUTE
// ============================================================

function selectSafeRoute(
    index
) {

    if (
        !window.safeRouteData ||
        !Array.isArray(
            window.safeRouteData.routes
        ) ||
        !window.safeRouteData.routes[index]
    ) {

        return;
    }


    window.safeRouteSelectedRoute =
        window.safeRouteData.routes[
            index
        ];


    console.log(
        "Selected route:",
        window.safeRouteSelectedRoute.routeName
    );


    renderSafeRouteCards(
        window.safeRouteData
    );


    highlightSelectedRoute();


    focusSelectedSafeRoute();
}


// ============================================================
// FOCUS SELECTED ROUTE
// ============================================================

function focusSelectedSafeRoute() {

    const route =
        window.safeRouteSelectedRoute;


    if (
        !window.safeRouteMap ||
        !route ||
        !Array.isArray(
            route.coordinates
        )
    ) {

        return;
    }


    const coordinates =
        normalizeRouteCoordinates(
            route.coordinates
        );


    if (
        coordinates.length < 2
    ) {

        return;
    }


    window.safeRouteMap.fitBounds(

        L.latLngBounds(
            coordinates
        ),

        {
            padding: [
                50,
                50
            ]
        }
    );
}


// ============================================================
// HIGHLIGHT SELECTED ROUTE
// ============================================================

function highlightSelectedRoute() {

    const selectedRoute =
        window.safeRouteSelectedRoute;


    if (!selectedRoute) {
        return;
    }


    // Safest route

    if (
        window.safestRouteLayer
    ) {

        const selected =
            selectedRoute.routeName ===
            "Safest Route";


        window.safestRouteLayer.setStyle({

            weight:
                selected
                    ? 8
                    : 4,

            opacity:
                selected
                    ? 1
                    : 0.45
        });
    }


    // Alternative route

    if (
        window.alternativeRouteLayer
    ) {

        const selected =
            selectedRoute.routeName ===
            "Alternative Route";


        window.alternativeRouteLayer.setStyle({

            weight:
                selected
                    ? 8
                    : 4,

            opacity:
                selected
                    ? 1
                    : 0.45
        });
    }
}


// ============================================================
// ADD SAFETY FEATURE MARKERS
// ============================================================

// =====================================================
// BACKEND SAFETY MAP FEATURES
// =====================================================

function addSafetyFeatureMarkers(map, route) {

    if (
        !map ||
        !route ||
        !Array.isArray(route.mapFeatures)
    ) {
        return;
    }

    route.mapFeatures.forEach(function(feature) {

        const latitude = Number(feature.latitude);
        const longitude = Number(feature.longitude);

        if (
            !Number.isFinite(latitude) ||
            !Number.isFinite(longitude)
        ) {
            console.warn(
                "Invalid safety feature coordinates:",
                feature
            );
            return;
        }

        const category = String(
            feature.category || "OTHER"
        ).toUpperCase();

        const icon = getMapFeatureIcon(category);

        const featureName =
            feature.name || "Safety Location";

        console.log(
            "📍 Safety feature:",
            category,
            featureName,
            "LAT:",
            latitude,
            "LNG:",
            longitude
        );

        // ---------------------------------------------
        // CREATE ICON
        // ---------------------------------------------

        const featureIcon = L.divIcon({
            className: "shesafe-feature-marker",

            html: `
                <div class="shesafe-feature-pin">
                    <div class="shesafe-feature-circle">
                        ${icon}
                    </div>
                    <div class="shesafe-feature-tip"></div>
                </div>
            `,

            // Actual geographic coordinate is at
            // the bottom-center of the pin.
            iconSize: [38, 46],

            iconAnchor: [19, 46],

            popupAnchor: [0, -46]
        });

        // ---------------------------------------------
        // CREATE MARKER
        // ---------------------------------------------

        const marker = L.marker(
            [latitude, longitude],
            {
                icon: featureIcon,
                zIndexOffset: 500
            }
        ).addTo(map);

        // ---------------------------------------------
        // POPUP
        // ---------------------------------------------

        marker.bindPopup(`
            <div class="map-feature-popup">
                <strong>
                    ${icon}
                    ${escapeHtml(featureName)}
                </strong>

                <br>

                <span>
                    ${escapeHtml(
                        formatFeatureCategory(category)
                    )}
                </span>
            </div>
        `);

        window.safeRouteFeatureMarkers.push(marker);
    });
}


// ============================================================
// MAP FEATURE ICON
// ============================================================

function getMapFeatureIcon(
    category
) {

    switch (
        String(
            category ||
            "OTHER"
        ).toUpperCase()
    ) {

        case "STREETLIGHT":
            return "💡";

        case "POLICE":
            return "👮";

        case "HOSPITAL":
            return "🏥";

        case "SCHOOL":
            return "🏫";

        case "MARKET":
            return "🛒";

        case "BUS_STATION":
            return "🚌";

        case "PUBLIC_TRANSPORT":
            return "🚏";

        case "RESTAURANT":
            return "🍽️";

        case "HOTEL":
            return "🏨";

        case "CCTV":
            return "📹";

        default:
            return "📍";
    }
}


// ============================================================
// FORMAT FEATURE CATEGORY
// ============================================================

function formatFeatureCategory(
    category
) {

    return String(
        category ||
        "OTHER"
    )
        .toLowerCase()
        .replaceAll(
            "_",
            " "
        )
        .replace(
            /\b\w/g,
            function (letter) {

                return letter.toUpperCase();
            }
        );
}


// ============================================================
// TRAFFIC LEVEL
// ============================================================

function getTrafficLevel(
    score
) {

    score =
        Number(
            score || 0
        );


    if (
        score < 30
    ) {

        return "LOW";
    }


    if (
        score < 60
    ) {

        return "MEDIUM";
    }


    return "HIGH";
}


// ============================================================
// RISK LEVEL
// ============================================================

function formatRiskZone(
    zone,
    riskScore
) {

    // Backend value first

    if (
        zone &&
        String(zone).toUpperCase() !==
        "UNKNOWN"
    ) {

        return String(zone)
            .replaceAll(
                "_",
                " "
            );
    }


    const score =
        Number(
            riskScore
        );


    if (
        !Number.isFinite(score)
    ) {

        return "UNKNOWN";
    }


    if (
        score >= 75
    ) {

        return "CRIME HOTSPOT";
    }


    if (
        score >= 55
    ) {

        return "HIGH RISK";
    }


    if (
        score >= 30
    ) {

        return "MODERATE RISK";
    }


    return "SAFE";
}


// ============================================================
// START ROUTE NAVIGATION
// ============================================================

function startRouteNavigation(
    index
) {

    if (
        !window.safeRouteData ||
        !Array.isArray(
            window.safeRouteData.routes
        ) ||
        !window.safeRouteData.routes[index]
    ) {

        console.error(
            "Navigation route not found."
        );

        return;
    }


    window.safeRouteSelectedRoute =
        window.safeRouteData.routes[
            index
        ];


    renderSafeRouteCards(
        window.safeRouteData
    );


    highlightSelectedRoute();


    focusSelectedSafeRoute();


    startLiveNavigation();
}


// ============================================================
// START LIVE NAVIGATION
// ============================================================

function startLiveNavigation() {

    if (
        !navigator.geolocation
    ) {

        alert(
            "Geolocation is not supported by this browser."
        );

        return;
    }


    if (
        !window.safeRouteSelectedRoute ||
        !Array.isArray(
            window.safeRouteSelectedRoute.coordinates
        )
    ) {

        alert(
            "Please select a route first."
        );

        return;
    }


    // --------------------------------------------------------
    // STOP OLD WATCHER
    // --------------------------------------------------------

    if (
        window.safeRouteWatchId !== null
    ) {

        navigator.geolocation.clearWatch(
            window.safeRouteWatchId
        );

        window.safeRouteWatchId = null;
    }


    window.safeRouteNavigationActive =
        true;


    const status =
        document.getElementById(
            "safeRouteStatus"
        );


    if (status) {

        status.innerText =
            `🧭 Navigation started on ${window.safeRouteSelectedRoute.routeName}.`;
    }


    focusSelectedSafeRoute();


    // --------------------------------------------------------
    // WATCH GPS
    // --------------------------------------------------------

    window.safeRouteWatchId =
        navigator.geolocation.watchPosition(

            function (position) {

                updateNavigationPosition(
                    position
                );
            },

            function (error) {

                console.error(
                    "Navigation GPS error:",
                    error
                );


                const status =
                    document.getElementById(
                        "safeRouteStatus"
                    );


                if (!status) {
                    return;
                }


                switch (error.code) {

                    case 1:

                        status.innerText =
                            "⚠️ Location permission denied.";

                        break;

                    case 2:

                        status.innerText =
                            "⚠️ Current location unavailable.";

                        break;

                    case 3:

                        status.innerText =
                            "⚠️ Location request timed out.";

                        break;

                    default:

                        status.innerText =
                            "⚠️ Unable to update your location.";
                }
            },

            {
                enableHighAccuracy: true,
                maximumAge: 1000,
                timeout: 10000
            }
        );


    console.log(
        "🧭 Live navigation started."
    );
}


// ============================================================
// UPDATE LIVE NAVIGATION POSITION
// ============================================================

function updateNavigationPosition(
    position
) {

    if (
        !window.safeRouteNavigationActive ||
        !window.safeRouteMap
    ) {

        return;
    }


    const latitude =
        position.coords.latitude;

    const longitude =
        position.coords.longitude;

    const accuracy =
        position.coords.accuracy || 20;


    window.safeRouteLatitude =
        latitude;

    window.safeRouteLongitude =
        longitude;


    console.log(
        "📍 LIVE LOCATION:",
        latitude,
        longitude
    );


    // --------------------------------------------------------
    // CURRENT LOCATION MARKER
    // --------------------------------------------------------

    if (
        !window.safeRouteCurrentMarker
    ) {

        window.safeRouteCurrentMarker =
            L.marker(
                [
                    latitude,
                    longitude
                ],
                {
                    icon:
                        L.divIcon({

                            className:
                                "current-location-icon",

                            html:
                                `
                                <div
                                    style="
                                        width:24px;
                                        height:24px;
                                        background:#2563eb;
                                        border:4px solid white;
                                        border-radius:50%;
                                        box-shadow:0 2px 8px rgba(0,0,0,.4);
                                    "
                                ></div>
                                `,

                            iconSize:
                                [
                                    32,
                                    32
                                ],

                            iconAnchor:
                                [
                                    16,
                                    16
                                ]
                        })
                }
            )
            .addTo(
                window.safeRouteMap
            )
            .bindPopup(
                "<b>📍 You are here</b>"
            );

    } else {

        window.safeRouteCurrentMarker.setLatLng(
            [
                latitude,
                longitude
            ]
        );
    }


    // --------------------------------------------------------
    // GPS ACCURACY CIRCLE
    // --------------------------------------------------------

    if (
        !window.safeRouteAccuracyCircle
    ) {

        window.safeRouteAccuracyCircle =
            L.circle(
                [
                    latitude,
                    longitude
                ],
                {
                    radius:
                        accuracy,

                    weight:
                        1,

                    fillOpacity:
                        0.08
                }
            )
            .addTo(
                window.safeRouteMap
            );

    } else {

        window.safeRouteAccuracyCircle.setLatLng(
            [
                latitude,
                longitude
            ]
        );

        window.safeRouteAccuracyCircle.setRadius(
            accuracy
        );
    }


    // --------------------------------------------------------
    // FOLLOW USER
    // --------------------------------------------------------

    window.safeRouteMap.setView(
        [
            latitude,
            longitude
        ],
        17,
        {
            animate: true
        }
    );


    // --------------------------------------------------------
    // UPDATE ROUTE PROGRESS
    // --------------------------------------------------------

    updateRouteProgress(
        latitude,
        longitude
    );
}


// ============================================================
// UPDATE ROUTE PROGRESS
// ============================================================

function updateRouteProgress(
    latitude,
    longitude
) {

    const route =
        window.safeRouteSelectedRoute;


    if (
        !route ||
        !Array.isArray(
            route.coordinates
        )
    ) {

        return;
    }


    const coordinates =
        normalizeRouteCoordinates(
            route.coordinates
        );


    if (
        coordinates.length < 2
    ) {

        return;
    }


    const userPoint =
        L.latLng(
            latitude,
            longitude
        );


    let closestIndex = 0;

    let closestDistance =
        Infinity;


    // --------------------------------------------------------
    // FIND CLOSEST ROUTE POINT
    // --------------------------------------------------------

    coordinates.forEach(
        function (point, index) {

            const routePoint =
                L.latLng(
                    point[0],
                    point[1]
                );


            const distance =
                userPoint.distanceTo(
                    routePoint
                );


            if (
                distance <
                closestDistance
            ) {

                closestDistance =
                    distance;

                closestIndex =
                    index;
            }
        }
    );


    // --------------------------------------------------------
    // TRAVELLED
    // --------------------------------------------------------

    const travelled =
        coordinates.slice(
            0,
            closestIndex + 1
        );


    // --------------------------------------------------------
    // REMAINING
    // --------------------------------------------------------

    const remaining =
        coordinates.slice(
            closestIndex
        );


    // --------------------------------------------------------
    // REMOVE OLD PROGRESS
    // --------------------------------------------------------

    if (
        window.safeRouteProgressLayer
    ) {

        window.safeRouteProgressLayer.remove();

        window.safeRouteProgressLayer =
            null;
    }


    if (
        window.safeRouteRemainingLayer
    ) {

        window.safeRouteRemainingLayer.remove();

        window.safeRouteRemainingLayer =
            null;
    }


    // --------------------------------------------------------
    // DRAW TRAVELLED
    // --------------------------------------------------------

    if (
        travelled.length >= 2
    ) {

        window.safeRouteProgressLayer =
            L.polyline(
                travelled,
                {
                    weight: 9,

                    opacity: 0.9,

                    color: "#16a34a",

                    lineCap: "round"
                }
            )
            .addTo(
                window.safeRouteMap
            );
    }


    // --------------------------------------------------------
    // DRAW REMAINING
    // --------------------------------------------------------

    if (
        remaining.length >= 2
    ) {

        window.safeRouteRemainingLayer =
            L.polyline(
                remaining,
                {
                    weight: 7,

                    opacity: 0.9,

                    color: "#2563eb",

                    lineCap: "round"
                }
            )
            .addTo(
                window.safeRouteMap
            );
    }


    // --------------------------------------------------------
    // DESTINATION DISTANCE
    // --------------------------------------------------------

    const destination =
        coordinates[
            coordinates.length - 1
        ];


    const destinationPoint =
        L.latLng(
            destination[0],
            destination[1]
        );


    const distanceToDestination =
        userPoint.distanceTo(
            destinationPoint
        );


    console.log(
        "Distance to destination:",
        Math.round(
            distanceToDestination
        ),
        "meters"
    );


    // --------------------------------------------------------
    // ARRIVAL
    // --------------------------------------------------------

    if (
        distanceToDestination <= 30
    ) {

        navigationArrived();
    }
}


// ============================================================
// NAVIGATION ARRIVED
// ============================================================

function navigationArrived() {

    if (
        !window.safeRouteNavigationActive
    ) {

        return;
    }


    stopLiveNavigation();


    const status =
        document.getElementById(
            "safeRouteStatus"
        );


    if (status) {

        status.innerText =
            "🎯 You have arrived at your destination.";
    }


    console.log(
        "🎯 Destination reached."
    );
}


// ============================================================
// STOP LIVE NAVIGATION
// ============================================================

function stopLiveNavigation() {

    window.safeRouteNavigationActive =
        false;


    // --------------------------------------------------------
    // STOP GPS WATCH
    // --------------------------------------------------------

    if (
        window.safeRouteWatchId !== null
    ) {

        navigator.geolocation.clearWatch(
            window.safeRouteWatchId
        );

        window.safeRouteWatchId =
            null;
    }


    // --------------------------------------------------------
    // CURRENT LOCATION
    // --------------------------------------------------------

    if (
        window.safeRouteCurrentMarker
    ) {

        window.safeRouteCurrentMarker.remove();

        window.safeRouteCurrentMarker =
            null;
    }


    // --------------------------------------------------------
    // ACCURACY CIRCLE
    // --------------------------------------------------------

    if (
        window.safeRouteAccuracyCircle
    ) {

        window.safeRouteAccuracyCircle.remove();

        window.safeRouteAccuracyCircle =
            null;
    }


    // --------------------------------------------------------
    // TRAVELLED ROUTE
    // --------------------------------------------------------

    if (
        window.safeRouteProgressLayer
    ) {

        window.safeRouteProgressLayer.remove();

        window.safeRouteProgressLayer =
            null;
    }


    // --------------------------------------------------------
    // REMAINING ROUTE
    // --------------------------------------------------------

    if (
        window.safeRouteRemainingLayer
    ) {

        window.safeRouteRemainingLayer.remove();

        window.safeRouteRemainingLayer =
            null;
    }


    console.log(
        "🛑 Live navigation stopped."
    );
}


// ============================================================
// FORMAT NUMBER
// ============================================================

function formatNumber(
    value
) {

    const number =
        Number(
            value
        );


    if (
        Number.isNaN(number)
    ) {

        return "0";
    }


    return number
        .toFixed(2)
        .replace(
            /\.00$/,
            ""
        );
}


// ============================================================
// ESCAPE HTML
// ============================================================

function escapeHtml(
    value
) {

    if (
        value == null
    ) {

        return "";
    }


    return String(
        value
    )
        .replaceAll(
            "&",
            "&amp;"
        )
        .replaceAll(
            "<",
            "&lt;"
        )
        .replaceAll(
            ">",
            "&gt;"
        )
        .replaceAll(
            '"',
            "&quot;"
        )
        .replaceAll(
            "'",
            "&#039;"
        );
}


// ============================================================
// PAGE INITIALIZATION
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "✅ SheSafe Safe Route JS loaded."
        );


        const map =
            document.getElementById(
                "safeRouteMap"
            );


        if (map) {

            console.log(
                "✅ #safeRouteMap exists on page load."
            );

        } else {

            console.error(
                "❌ #safeRouteMap is missing on page load."
            );
        }


        const section =
            document.getElementById(
                "safeRouteMapSection"
            );


        if (section) {

            console.log(
                "✅ #safeRouteMapSection exists."
            );
        }
    }
);
function refreshSafetyFeatureMarkers() {

    if (!window.safeRouteMap) {
        return;
    }

    // Remove old feature markers
    if (Array.isArray(window.safeRouteFeatureMarkers)) {

        window.safeRouteFeatureMarkers.forEach(function(marker) {

            try {
                window.safeRouteMap.removeLayer(marker);
            } catch (error) {
                console.warn(
                    "Unable to remove old safety marker:",
                    error
                );
            }

        });
    }

    window.safeRouteFeatureMarkers = [];

    // Add markers for selected route
    if (window.safeRouteSelectedRoute) {

        addSafetyFeatureMarkers(
            window.safeRouteMap,
            window.safeRouteSelectedRoute
        );
    }
}
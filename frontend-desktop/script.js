// =====================================================
// API URLS
// =====================================================

const BASE_URL =
    "https://shesafe-backend-api.onrender.com";

const AUTH_URL =
    BASE_URL + "/api/auth";

const EMERGENCY_URL =
    BASE_URL + "/api/emergency";

const LOCATION_URL =
    BASE_URL + "/api/location";

const LIVE_LOCATION_URL =
    BASE_URL + "/api/live-location";

const NOTIFICATION_URL =
    BASE_URL + "/api/notifications";

const CALLS_URL =
    BASE_URL + "/api/calls";

const MESSAGES_URL =
    BASE_URL + "/api/messages";
let currentUserLocation = null;
let locationWatchId = null;


// =====================================================
// LOGIN
// =====================================================

// =====================================================
// LOGIN
// =====================================================

async function login() {

    const emailElement =
        document.getElementById("loginEmail");

    const passwordElement =
        document.getElementById("loginPassword");

    if (!emailElement || !passwordElement) {
        return;
    }

    const email =
        emailElement.value.trim();

    const password =
        passwordElement.value;

    if (email === "" || password === "") {

        alert("Please enter email and password.");

        return;
    }
    const loginButton =
    document.getElementById("loginButton");

if (loginButton) {
    loginButton.disabled = true;
    loginButton.innerText = "Connecting...";
}

    try {

        const response =
            await fetch(
                AUTH_URL + "/login",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                }
            );


        // ==========================================
        // READ RESPONSE
        // ==========================================

        const data =
            await response.json();

        console.log(
            "LOGIN RESPONSE:",
            data
        );


        // ==========================================
        // LOGIN FAILED
        // ==========================================

        if (!response.ok) {

            alert(
                data.message ||
                "Invalid email or password."
            );

            return;
        }


        // ==========================================
        // SUPER ADMIN OTP
        // ==========================================

        if (
            data.role &&
            (
                data.role.toString().trim().toUpperCase() ===
                    "SUPER_ADMIN" ||

                data.role.toString().trim().toUpperCase() ===
                    "ROLE_SUPER_ADMIN"
            )
        ) {

            console.log(
                "SUPER ADMIN LOGIN - OTP REQUIRED"
            );

            localStorage.setItem(
                "pendingSuperAdminEmail",
                email
            );

            localStorage.setItem(
                "pendingSuperAdminUserId",
                String(data.userId)
            );

            window.location.href =
                "admin-otp.html";

            return;
        }


        // ==========================================
        // CHECK TOKEN FOR NORMAL USERS
        // ==========================================

        if (!data.token) {

            alert(
                data.message ||
                "Login failed."
            );

            console.error(
                "Backend did not return JWT token:",
                data
            );

            return;
        }


        // ==========================================
        // CHECK USER ID
        // ==========================================

        if (
            data.userId === null ||
            data.userId === undefined ||
            data.userId === "" ||
            Number(data.userId) <= 0
        ) {

            console.error(
                "Backend returned invalid userId:",
                data.userId
            );

            alert(
                "Login failed: User ID was not returned by the server."
            );

            return;
        }
        
const language = String(data.preferredLanguage || "")
    .trim()
    .toLowerCase();

console.log("Language returned by backend:", language);

if (["en", "te", "hi", "ur"].includes(language)) {
    localStorage.setItem("preferredLanguage", language);
} else {
    console.error(
        "Backend did not return a valid preferredLanguage:",
        data.preferredLanguage
    );
}



        // ==========================================
        // SAVE LOGIN DATA
        // ==========================================

        localStorage.setItem(
            "token",
            data.token
        );

        localStorage.setItem(
            "userId",
            String(data.userId)
        );

        localStorage.setItem(
            "role",
            data.role || "USER"
        );

        localStorage.setItem(
            "profileCompleted",
            String(data.profileCompleted)
        );


        // ==========================================
        // VERIFY LOCAL STORAGE
        // ==========================================

        console.log(
            "LOGIN SUCCESS"
        );

        console.log(
            "User ID:",
            localStorage.getItem("userId")
        );

        console.log(
            "Role:",
            localStorage.getItem("role")
        );

        console.log(
            "Profile Completed:",
            localStorage.getItem(
                "profileCompleted"
            )
        );


       
        // ==========================================
        // REQUEST PERMISSIONS AFTER LOGIN
        // ==========================================

        console.log("Requesting SheSafe permissions...");

        try {
            const permissionsGranted =
                await requestEmergencyPermissions();

            console.log(
                "Permissions granted:",
                permissionsGranted
            );
        } catch (permissionError) {
            console.error(
                "Permission request failed:",
                permissionError
            );
        }

        // ==========================================
        // CHECK PROFILE COMPLETION
        // ==========================================

        const profileCompleted =
            data.profileCompleted === true ||
            String(data.profileCompleted).toLowerCase() === "true";

        if (!profileCompleted) {
            window.location.href = "complete-profile.html";
            return;
        }

        // ==========================================
        // GO TO HOME
        // ==========================================

        window.location.href = "home.html";
        return;

        


    } catch (error) {
        if (loginButton) {
    loginButton.disabled = false;
    loginButton.innerText = "Sign in";
}

        console.error(
            "Login error:",
            error
        );

        alert(
            "Cannot connect to backend!"
        );
      
    }
}
// =====================================================
// REGISTER
// =====================================================

async function register() {

    const user = {
        fullName: document.getElementById("fullName").value.trim(),
        email: document.getElementById("email").value.trim(),
        phoneNumber: document.getElementById("phoneNumber").value.trim(),
        password: document.getElementById("password").value,
        preferredLanguage: document.getElementById("preferredLanguage").value
    };

    if (
        user.fullName === "" ||
        user.email === "" ||
        user.phoneNumber === "" ||
        user.password === "" ||
        !["en", "te", "hi", "ur"].includes(user.preferredLanguage)
    ) {
        alert("Please fill all fields and select a valid language.");
        return;
    }


    try {

        const response =
            await fetch(
                AUTH_URL + "/register",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(user)
                }
            );


        const message =
            await response.text();


        if (!response.ok) {

            alert(
                "Registration failed: " +
                message
            );

            return;
        }


        // =================================================
        // OTP SENT
        // =================================================

        if (
            message.trim() ===
            "OTP sent successfully"
        ) {

            alert(
                "Registration details accepted!\n\n" +
                "An OTP has been sent to your email."
            );


            // IMPORTANT
            // Save email temporarily in browser
            // so Resend OTP knows which email to use.

            localStorage.setItem(
                "pendingRegistrationEmail",
                user.email
            );


            // Hide registration section

            document.getElementById(
                "registrationSection"
            ).style.display = "none";


            // Show OTP section

            document.getElementById(
                "registrationOtpSection"
            ).style.display = "block";


            // Start 30-second resend timer

            startRegistrationOtpTimer();

        }
        else {

            alert(message);
        }


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend!"
        );
    }
}


// =====================================================
// VERIFY REGISTRATION OTP
// =====================================================

async function verifyRegistrationOtp() {

    const email =
        localStorage.getItem(
            "pendingRegistrationEmail"
        );


    const otp =
        document.getElementById(
            "registrationOtp"
        ).value.trim();


    // =================================================
    // EMAIL CHECK
    // =================================================

    if (!email) {

        alert(
            "Registration email not found. Please register again."
        );

        return;
    }


    // =================================================
    // OTP CHECK
    // =================================================

    if (otp === "") {

        alert(
            "Please enter the OTP."
        );

        return;
    }


    if (!/^\d{6}$/.test(otp)) {

        alert(
            "Please enter a valid 6-digit OTP."
        );

        return;
    }


    try {

        const response =
            await fetch(
                AUTH_URL +
                "/verify-registration-otp",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email: email,

                        otp: otp
                    })
                }
            );


        const message =
            await response.text();


        if (!response.ok) {

            alert(
                message ||
                "OTP verification failed."
            );

            return;
        }


        // =================================================
        // SUCCESS
        // =================================================

        if (
            message.trim() ===
            "Registration verified successfully"
        ) {

            alert(
                "Registration verified successfully!"
            );


            // Registration is complete.
            // Remove temporary email.

            localStorage.removeItem(
                "pendingRegistrationEmail"
            );


            // Go to login

            window.location.href =
                "index.html";

        }
        else {

            alert(message);
        }


    } catch (error) {

        console.error(
            "OTP verification error:",
            error
        );

        alert(
            "Unable to connect to backend!"
        );
    }
}


// =====================================================
// RESEND REGISTRATION OTP
// =====================================================

async function resendRegistrationOtp() {

    const email =
        localStorage.getItem(
            "pendingRegistrationEmail"
        );


    const messageElement =
        document.getElementById(
            "registrationOtpMessage"
        );


    const resendButton =
        document.getElementById(
            "resendRegistrationOtpButton"
        );


    // =================================================
    // CHECK EMAIL
    // =================================================

    if (!email) {

        messageElement.innerText =
            "❌ Registration email not found.";

        return;
    }


    // Disable immediately

    resendButton.disabled = true;


    messageElement.innerText =
        "Sending new OTP...";


    try {

        const response =
            await fetch(
                AUTH_URL +
                "/resend-registration-otp",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email: email
                    })
                }
            );


        const data =
            await response.json();


        // =================================================
        // ERROR
        // =================================================

        if (!response.ok) {

            messageElement.innerText =
                "❌ " +
                (
                    data.message ||
                    "Unable to resend OTP."
                );


            // Allow retry after timer

            startRegistrationOtpTimer();

            return;
        }


        // =================================================
        // SUCCESS
        // =================================================

        messageElement.innerText =
            "✅ New OTP sent to your email.";


        // Clear old OTP

        document.getElementById(
            "registrationOtp"
        ).value = "";


        // Start timer again

        startRegistrationOtpTimer();


    } catch (error) {

        console.error(error);


        messageElement.innerText =
            "❌ Cannot connect to backend.";


        startRegistrationOtpTimer();
    }
}


// =====================================================
// RESEND OTP TIMER
// =====================================================

let registrationOtpCountdown = 30;

let registrationOtpTimerInterval;


function startRegistrationOtpTimer() {

    clearInterval(
        registrationOtpTimerInterval
    );


    registrationOtpCountdown = 30;


    const resendButton =
        document.getElementById(
            "resendRegistrationOtpButton"
        );


    const timer =
        document.getElementById(
            "registrationOtpTimer"
        );


    if (!resendButton || !timer) {
        return;
    }


    resendButton.disabled = true;


    resendButton.innerText =
        "Resend OTP";


    timer.innerText =
        `Resend available in ${registrationOtpCountdown} seconds`;


    registrationOtpTimerInterval =
        setInterval(() => {

            registrationOtpCountdown--;


            if (registrationOtpCountdown <= 0) {

                clearInterval(
                    registrationOtpTimerInterval
                );


                resendButton.disabled = false;


                resendButton.innerText =
                    "Resend OTP";


                timer.innerText =
                    "You can request a new OTP.";


                return;
            }


            timer.innerText =
                `Resend available in ${registrationOtpCountdown} seconds`;

        }, 1000);
}



// =====================================================
// LOGOUT
// =====================================================

function logout() {

    localStorage.removeItem("token");

    localStorage.removeItem("userId");

    localStorage.removeItem("role");

    window.location.href =
        "index.html";
}


// =====================================================
// SAVE PROFILE
// =====================================================

async function saveProfile() {

    const userId =
        localStorage.getItem("userId");

    const token =
        localStorage.getItem("token");

    if (!userId || !token) {

        alert(
            "Please login first."
        );

        window.location.href =
            "index.html";

        return;
    }

    const profileData = {

        userId:
            Number(userId),

        fullName:
            document.getElementById(
                "fullName"
            ).value.trim(),

        dateOfBirth:
            document.getElementById(
                "dateOfBirth"
            ).value,

        bloodGroup:
            document.getElementById(
                "bloodGroup"
            ).value,

        medicalConditions:
            document.getElementById(
                "medicalConditions"
            ).value.trim(),

        medications:
            document.getElementById(
                "medications"
            ).value.trim(),

        allergies:
            document.getElementById(
                "allergies"
            ).value.trim(),

        emergencyDetails:
            document.getElementById(
                "emergencyDetails"
            ).value.trim(),

        preferredHospital:
            document.getElementById(
                "preferredHospital"
            ).value.trim()
    };

    if (
        profileData.fullName === "" ||
        profileData.dateOfBirth === "" ||
        profileData.bloodGroup === ""
    ) {

        alert(
            "Please fill Full Name, Date of Birth and Blood Group."
        );

        return;
    }

    try {

        const response =
            await fetch(
                BASE_URL +
                "/api/profile/save",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token
                    },

                    body:
                        JSON.stringify(profileData)
                }
            );

        if (!response.ok) {

            const error =
                await response.text();

            alert(
                "Failed to save profile: " +
                error
            );

            return;
        }

        alert(
            "Profile saved successfully!"
        );

        window.location.href =
            "contacts.html";
        return;

    } catch (error) {

        console.error(error);

        alert(
            "Cannot connect to backend."
        );
    }
}


// =====================================================
// LOAD EXISTING PROFILE
// =====================================================

async function loadProfile() {

    const userId =
        localStorage.getItem("userId");

    const token =
        localStorage.getItem("token");

    if (!userId || !token) {

        alert(
            "Please login first."
        );

        window.location.href =
            "index.html";

        return;
    }

    try {

        const response =
            await fetch(
                BASE_URL +
                "/api/profile/user/" +
                userId,
                {

                    method: "GET",

                    headers: {

                        "Authorization":
                            "Bearer " + token
                    }
                }
            );

        if (!response.ok) {

            const error =
                await response.text();

            console.error(
                "Profile loading failed:",
                response.status,
                error
            );

            alert(
                "Unable to load profile."
            );

            return;
        }

        const profile =
            await response.json();

        console.log(
            "Profile loaded:",
            profile
        );

        document.getElementById(
            "fullName"
        ).value =
            profile.fullName || "";

        document.getElementById(
            "dateOfBirth"
        ).value =
            profile.dateOfBirth || "";

        document.getElementById(
            "bloodGroup"
        ).value =
            profile.bloodGroup || "";

        document.getElementById(
            "medicalConditions"
        ).value =
            profile.medicalConditions || "";

        document.getElementById(
            "medications"
        ).value =
            profile.medications || "";

        document.getElementById(
            "allergies"
        ).value =
            profile.allergies || "";

        document.getElementById(
            "emergencyDetails"
        ).value =
            profile.emergencyDetails || "";

        document.getElementById(
            "preferredHospital"
        ).value =
            profile.preferredHospital || "";

        const button =
            document.getElementById(
                "saveProfileButton"
            );

        if (button) {

            button.innerText =
                "✓ Save Changes";
        }

    } catch (error) {

        console.error(
            "Profile loading error:",
            error
        );

        alert(
            "Unable to load profile."
        );
    }
}


// =====================================================
// LIVE LOCATION
// =====================================================

function getLocation() {

    const status =
        document.getElementById(
            "locationStatus"
        );

    if (!navigator.geolocation) {

        if (status) {

            status.innerText =
                "❌ Geolocation is not supported.";
        }

        return;
    }

    if (status) {

        status.innerText =
            "📍 Getting your location...";
    }

    navigator.geolocation.getCurrentPosition(

        showPosition,

        locationError,

        {
            enableHighAccuracy: true,

            timeout: 10000,

            maximumAge: 0
        }
    );
}


// =====================================================
// SHOW AND SAVE LOCATION
// =====================================================

async function showPosition(position) {

    const latitude =
        position.coords.latitude;

    const longitude =
        position.coords.longitude;

    const accuracy =
        position.coords.accuracy;

    const latitudeElement =
        document.getElementById(
            "latitude"
        );

    if (latitudeElement) {

        latitudeElement.value =
            latitude;
    }

    const longitudeElement =
        document.getElementById(
            "longitude"
        );

    if (longitudeElement) {

        longitudeElement.value =
            longitude;
    }

    const accuracyElement =
        document.getElementById(
            "accuracy"
        );

    if (accuracyElement) {

        accuracyElement.value =
            Math.round(accuracy) +
            " meters";
    }

    const token =
        localStorage.getItem("token");

    const userId =
        localStorage.getItem("userId");
        const incidentId =
    localStorage.getItem("currentIncidentId");

    if (!token || !userId) {

        alert(
            "Please login first."
        );

        window.location.href =
            "index.html";

        return;
    }

    try {

        const response =
            await fetch(
                LOCATION_URL + "/save",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token
                    },

                    body: JSON.stringify({

    userId:
        Number(userId),

    incidentId:
        incidentId
            ? Number(incidentId)
            : null,

    latitude:
        latitude,

    longitude:
        longitude,

    accuracy:
        accuracy
})
                }
            );

        const message =
            await response.text();

        if (!response.ok) {

            const status =
                document.getElementById(
                    "locationStatus"
                );

            if (status) {

                status.innerText =
                    "❌ Location save failed.";
            }

            alert(
                "Location save failed: " +
                response.status +
                "\n" +
                message
            );

            return;
        }

        const status =
            document.getElementById(
                "locationStatus"
            );

        if (status) {

            status.innerHTML =
                "✅ Location detected and saved successfully.";
        }

        console.log(
            "Backend:",
            message
        );

    } catch (error) {

        console.error(error);

        alert(
            "Unable to save location to backend."
        );
    }
}


// =====================================================
// LOCATION ERROR
// =====================================================

function locationError(error) {

    const status =
        document.getElementById(
            "locationStatus"
        );

    if (!status) {
        return;
    }

    if (error.code === 1) {

        status.innerText =
            "❌ Please allow location permission.";

    } else if (error.code === 2) {

        status.innerText =
            "❌ Location unavailable.";

    } else if (error.code === 3) {

        status.innerText =
            "❌ Location request timed out.";

    } else {

        status.innerText =
            "❌ Unable to get location.";
    }
}


// =====================================================
// OPEN GOOGLE MAPS
// =====================================================

function openMap() {

    const latitudeElement =
        document.getElementById(
            "latitude"
        );

    const longitudeElement =
        document.getElementById(
            "longitude"
        );

    if (
        !latitudeElement ||
        !longitudeElement
    ) {

        alert(
            "Location fields not found."
        );

        return;
    }

    const latitude =
        latitudeElement.value;

    const longitude =
        longitudeElement.value;

    if (!latitude || !longitude) {

        alert(
            "First click Get My Location."
        );

        return;
    }

    const mapsUrl =
        "https://www.google.com/maps/search/?api=1&query=" +
        latitude +
        "," +
        longitude;

    window.open(
        mapsUrl,
        "_blank"
    );
}
// =====================================================
// SOS AUDIO + VIDEO RECORDING
// =====================================================

let sosMediaStream = null;

let sosAudioRecorder = null;
let sosVideoRecorder = null;

let sosAudioChunks = [];
let sosVideoChunks = [];

let sosRecordingStarted = false;
let sosRecordingTimeout = null;

// Exact time when SOS was activated
let sosActivationTime = null;
let sosRecordingStartTime = null;
let sosAudioDuration = 0;
let sosVideoDuration = 0;
// =====================================================
// START AUDIO + VIDEO WHEN SOS IS ACTIVATED
// =====================================================

// =====================================================
// SOS AUDIO + VIDEO RECORDING
// =====================================================

async function startSOSRecording(incidentId) {

    // Prevent duplicate recording
    if (sosRecordingStarted) {

        console.log(
            "SOS recording already active."
        );

        return;
    }


    // =====================================================
    // SAVE EXACT SOS ACTIVATION TIME
    // =====================================================

    sosActivationTime = new Date();
    sosRecordingStartTime = new Date();

    const sosTime =
        sosActivationTime.toISOString();


    console.log(
        "🚨 SOS ACTIVATED AT:",
        sosTime
    );


    try {

        console.log(
            "🎙️ Starting emergency audio + video..."
        );


        // =================================================
        // CHECK MEDIA DEVICES
        // =================================================

        if (
            !navigator.mediaDevices ||
            !navigator.mediaDevices.getUserMedia
        ) {

            throw new Error(
                "Camera and microphone are not supported by this browser."
            );
        }


        // =================================================
        // START CAMERA + MICROPHONE
        //
        // Permission was already requested during login.
        // We only create the recording stream here.
        // =================================================

        sosMediaStream =
            await navigator.mediaDevices.getUserMedia({

                audio: true,

                video: {
                    facingMode: "user"
                }

            });


        console.log(
            "✅ Camera + microphone ready."
        );


        // =================================================
        // AUDIO STREAM
        // =================================================

        const audioStream =
            new MediaStream(
                sosMediaStream.getAudioTracks()
            );


        sosAudioChunks = [];


        sosAudioRecorder =
            new MediaRecorder(
                audioStream
            );


        // =================================================
        // AUDIO DATA
        // =================================================

        sosAudioRecorder.ondataavailable =
            function(event) {

                if (
                    event.data &&
                    event.data.size > 0
                ) {

                    sosAudioChunks.push(
                        event.data
                    );

                }

            };


        // =================================================
        // AUDIO STOP
        // =================================================

        sosAudioRecorder.onstop =
        
            async function() {
                const sosAudioEndTime = new Date();

sosAudioDuration =
    Math.round(
        (sosAudioEndTime - sosRecordingStartTime) / 1000
    );

console.log(
    "🎙️ SOS Audio duration:",
    sosAudioDuration,
    "seconds"
);


                try {

                    const audioBlob =
                        new Blob(
                            sosAudioChunks,
                            {
                                type:
                                    "audio/webm"
                            }
                        );


                    console.log(
                        "🎙️ Audio recording stopped."
                    );


                    // Upload using SOS activation timestamp

                    await uploadEvidence(
                        audioBlob,
                        "audio",
                        sosTime,incidentId,sosAudioDuration
                    );


                    // Timeline

                    addIncidentEvent({

                        type:
                            "AUDIO",

                        icon:
                            "🎙️",

                        title:
                            "Audio Recording Uploaded",

                        description:
                            "Emergency audio evidence was recorded and uploaded.",

                        fileName:
                            "SOS Audio",

                        status:
                            "Uploaded",

                        timestamp:
                            sosTime

                    });


                } catch (error) {

                    console.error(
                        "Audio upload error:",
                        error
                    );


                    addIncidentEvent({

                        type:
                            "AUDIO",

                        icon:
                            "🎙️",

                        title:
                            "Audio Recording Error",

                        description:
                            error.message,

                        fileName:
                            "SOS Audio",

                        status:
                            "Upload Failed",

                        timestamp:
                            new Date().toISOString()

                    });

                }

            };


        // =================================================
        // VIDEO STREAM
        // =================================================

        const videoStream =
            new MediaStream(
                sosMediaStream.getVideoTracks()
            );


        sosVideoChunks = [];


        sosVideoRecorder =
            new MediaRecorder(
                videoStream
            );


        // =================================================
        // VIDEO DATA
        // =================================================

        sosVideoRecorder.ondataavailable =
            function(event) {

                if (
                    event.data &&
                    event.data.size > 0
                ) {

                    sosVideoChunks.push(
                        event.data
                    );

                }

            };


        // =================================================
        // VIDEO STOP
        // =================================================

        sosVideoRecorder.onstop =
            async function() {
                const sosVideoEndTime = new Date();

sosVideoDuration =
    Math.round(
        (sosVideoEndTime - sosRecordingStartTime) / 1000
    );

console.log(
    "🎥 SOS Video duration:",
    sosVideoDuration,
    "seconds"
);

                try {

                    const videoBlob =
                        new Blob(
                            sosVideoChunks,
                            {
                                type:
                                    "video/webm"
                            }
                        );


                    console.log(
                        "🎥 Video recording stopped."
                    );


                    // Upload using SOS activation timestamp

                    await uploadEvidence(
                        videoBlob,
                        "video",
                        sosTime,incidentId,sosVideoDuration
                    );


                    // Timeline

                    addIncidentEvent({

                        type:
                            "VIDEO",

                        icon:
                            "🎥",

                        title:
                            "Video Recording Uploaded",

                        description:
                            "Emergency video evidence was recorded and uploaded.",

                        fileName:
                            "SOS Video",

                        status:
                            "Uploaded",

                        timestamp:
                            sosTime

                    });


                } catch (error) {

                    console.error(
                        "Video upload error:",
                        error
                    );


                    addIncidentEvent({

                        type:
                            "VIDEO",

                        icon:
                            "🎥",

                        title:
                            "Video Recording Error",

                        description:
                            error.message,

                        fileName:
                            "SOS Video",

                        status:
                            "Upload Failed",

                        timestamp:
                            new Date().toISOString()

                    });

                }

            };


        // =================================================
        // START RECORDERS
        // =================================================

        sosAudioRecorder.start();

        sosVideoRecorder.start();


        sosRecordingStarted =
            true;
            sosAudioRecorder.onerror = function(event) {
    console.error(
        "❌ AUDIO RECORDER ERROR:",
        event.error
    );
};

sosVideoRecorder.onerror = function(event) {
    console.error(
        "❌ VIDEO RECORDER ERROR:",
        event.error
    );
};
            // =====================================================
// AUTOMATIC SOS RECORDING TIMEOUT — 10 MINUTES
// =====================================================

sosRecordingTimeout = setTimeout(function () {

    if (sosRecordingStarted) {

        console.log(
            "⏱️ 10-minute SOS recording timeout reached."
        );

        addIncidentEvent({
            type: "RESPONSE",
            icon: "⏱️",
            title: "Automatic Recording Stop",
            description:
                "SOS recording automatically stopped after the maximum recording duration.",
            status: "10-minute timeout",
            timestamp: new Date().toISOString()
        });

        stopSOSRecording();
    }

}, 20000);


        console.log(
            "✅ SOS AUDIO + VIDEO RECORDING STARTED"
        );


        // =================================================
        // INCIDENT TIMELINE
        // =================================================

        addIncidentEvent({

            type:
                "AUDIO",

            icon:
                "🎙️",

            title:
                "Audio Recording Started",

            description:
                "Emergency audio recording started.",

            fileName:
                "SOS Audio",

            status:
                "Recording",

            timestamp:
                sosTime

        });


        addIncidentEvent({

            type:
                "VIDEO",

            icon:
                "🎥",

            title:
                "Video Recording Started",

            description:
                "Emergency video recording started.",

            fileName:
                "SOS Video",

            status:
                "Recording",

            timestamp:
                sosTime

        });


        // =================================================
        // UPDATE SOS STATUS
        // =================================================

        const status =
            document.getElementById(
                "sosStatus"
            );


        if (status) {

            status.innerHTML +=

                "<br><br>" +

                "🎙️ Audio recording started." +

                "<br>" +

                "📹 Video recording started." +

                "<br>" +

                "🔐 Emergency evidence is being secured.";

        }


    } catch (error) {

        console.error(
            "❌ SOS recording error:",
            error
        );


        const status =
            document.getElementById(
                "sosStatus"
            );


        if (status) {

            status.innerHTML +=

                "<br><br>" +

                "⚠️ Recording could not start: " +

                error.message;

        }


        addIncidentEvent({

            type:
                "RESPONSE",

            icon:
                "⚠️",

            title:
                "Recording Could Not Start",

            description:
                error.message,

            status:
                "Recording Failed",

            timestamp:
                new Date().toISOString()

        });

    }

}// =====================================================
// STOP SOS AUDIO + VIDEO
// =====================================================

// =====================================================
// STOP SOS RECORDING
// =====================================================


function stopSOSRecording() {

    if (!sosRecordingStarted) {

        console.log(
            "No SOS recording is active."
        );

        return;
    }


    console.log(
        "⛔ Stopping SOS recording..."
    );


    // Stop audio recorder

    if (
        sosAudioRecorder &&
        sosAudioRecorder.state !== "inactive"
    ) {

        sosAudioRecorder.stop();

    }


    // Stop video recorder

    if (
        sosVideoRecorder &&
        sosVideoRecorder.state !== "inactive"
    ) {

        sosVideoRecorder.stop();

    }


    // Stop camera + microphone

    if (sosMediaStream) {

        sosMediaStream
            .getTracks()
            .forEach(function(track) {

                track.stop();

            });

    }


    sosRecordingStarted =
        false;


    addIncidentEvent({

        type:
            "RESPONSE",

        icon:
            "⛔",

        title:
            "Emergency Recording Stopped",

        description:
            "Audio and video emergency recording was stopped.",

        status:
            "Stopped",

        timestamp:
            new Date().toISOString()

    });


    console.log(
        "✅ SOS recording stopped."
    );


    const status =
        document.getElementById(
            "sosStatus"
        );


    if (status) {

        status.innerHTML +=

            "<br><br>" +

            "⛔ Audio and video recording stopped.";

    }
    if (sosRecordingTimeout) {
    clearTimeout(sosRecordingTimeout);
    sosRecordingTimeout = null;
}
smartSOSActive = false;
smartSOSStarting = false;
strongShakeCount = 0;
previousShakeMagnitude = 0;

}
/* =========================================================
   SAVE BOTH AUDIO + VIDEO WHEN LEAVING PAGE
   ========================================================= */

function handleSOSPageExit() {

    if (
        typeof sosRecordingStarted === "undefined" ||
        !sosRecordingStarted
    ) {
        return;
    }

    console.log("⚠️ Page leaving during SOS recording.");

    /*
     * Stop AUDIO recorder
     */
    if (
        sosAudioRecorder &&
        sosAudioRecorder.state !== "inactive"
    ) {
        sosAudioRecorder.stop();
    }

    /*
     * Stop VIDEO recorder
     */
    if (
        sosVideoRecorder &&
        sosVideoRecorder.state !== "inactive"
    ) {
        sosVideoRecorder.stop();
    }

    /*
     * IMPORTANT:
     * Do NOT immediately call:
     *
     * sosMediaStream.getTracks().forEach(track => track.stop());
     *
     * because the video recorder still needs the final
     * video data to fire its onstop handler.
     */

    sosRecordingStarted = false;

    /*
     * Clear timeout
     */
    if (sosRecordingTimeout) {

        clearTimeout(sosRecordingTimeout);

        sosRecordingTimeout = null;
    }

    console.log(
        "🎥 Audio + video recorders instructed to stop."
    );
}


/*
 * Handle normal navigation / Back
 */
window.addEventListener(
    "pagehide",
    handleSOSPageExit
);

window.addEventListener(
    "beforeunload",
    handleSOSPageExit
);
// ============================================================
// SMART SOS CONTROLLER
// ============================================================

// =====================================================
// SMART SOS ACTIVATION GUARD
// =====================================================


let smartSOSActive = false;
let smartSOSStarting = false;

async function activateSmartSOS(source) {
    if (smartSOSActive || smartSOSStarting) {
        console.log("Smart SOS already active. Ignoring:", source);
        return;
    }

    smartSOSStarting = true;

    console.log("🚨 Smart SOS triggered by:", source);

    try {
        await triggerSOS();

        // Keep repeated triggers from creating duplicate incidents.
        // Reset this flag when the emergency is stopped.
        smartSOSActive = true;

    } catch (error) {
        console.error("Smart SOS activation failed:", error);
        smartSOSActive = false;

        const status = document.getElementById("sosStatus");
        if (status) {
            status.textContent =
                "SOS activation encountered an error. Please try again.";
        }
    } finally {
        smartSOSStarting = false;
    }
}

// =====================================================
// SOS LOCATION WITH FALLBACK
// =====================================================

async function getSOSLocation() {

    try {

        const position =
            await getSheSafeCurrentLocation();

        return {

            success: true,

            latitude:
                position.coords.latitude,

            longitude:
                position.coords.longitude,

            accuracy:
                position.coords.accuracy
        };

    } catch (error) {

        console.error(
            "❌ SOS location unavailable:",
            error
        );


        // If live tracking already has a location,
        // use that as the emergency fallback.

        if (
            currentLatitude !== null &&
            currentLongitude !== null
        ) {

            console.log(
                "📍 Using last known live location."
            );

            return {

                success: true,

                latitude:
                    currentLatitude,

                longitude:
                    currentLongitude,

                accuracy:
                    currentAccuracy || 0,

                fallback: true
            };
        }


        // No location available at all.

        return {

            success: false,

            latitude: null,

            longitude: null,

            accuracy: null
        };
    }
}

// =====================================================
// SOS
// =====================================================

async function triggerSOS() {

    const token =
        localStorage.getItem("token");

    const userId =
        localStorage.getItem("userId");

    if (!token || !userId) {
        alert("Please login first.");
        return;
    }


    // =========================================
    // CREATE DATABASE INCIDENT
    // =========================================

    const incidentId =
        await createDatabaseIncident();

    if (!incidentId) {

        alert(
            "Unable to create emergency incident."
        );

        return;
    }


   localStorage.setItem(
    "currentIncidentId",
    incidentId
);


    // =========================================
    // START AUDIO / VIDEO RECORDING
    // =========================================

    await startSOSRecording(incidentId);


    // =====================================================
    // RECORD SOS ACTIVATION
    // =====================================================

    addIncidentEvent({

        type: "SOS_ACTIVATED",

        icon: "🚨",

        title: "SOS Activated",

        description:
            "Emergency SOS button was activated."

    });


    const status =
        document.getElementById(
            "sosStatus"
        );


    if (status) {

        status.innerHTML =
            "🚨 Getting your location...";

    }


    if (!navigator.geolocation) {

        if (status) {

            status.innerHTML =
                "❌ Geolocation is not supported.";

        }

        return;
    }


    navigator.geolocation.getCurrentPosition(

        async function(position) {

            const latitude =
                position.coords.latitude;

            const longitude =
                position.coords.longitude;

            const accuracy =
                position.coords.accuracy;


            try {

               // =================================================
// SAVE LOCATION TO BACKEND
// =================================================

const locationResponse =
    await fetch(
        LOCATION_URL + "/save",
        {

            method: "POST",

            headers: {

                "Content-Type":
                    "application/json",

                "Authorization":
                    "Bearer " + token

            },

            body:
                JSON.stringify({

                    userId:
                        Number(userId),

                    incidentId:
                        Number(incidentId),

                    latitude:
                        latitude,

                    longitude:
                        longitude,

                    accuracy:
                        accuracy

                })

        }
    );


if (!locationResponse.ok) {

    throw new Error(
        "Unable to save location"
    );
}

console.log(
    "SOS location saved for incident:",
    incidentId
);
try {

    const emailResponse = await fetch(
        `${BASE_URL}/api/email/send-sos`,
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },

            body: JSON.stringify({
                userId: Number(userId),
                incidentId: Number(incidentId),
                latitude: latitude,
                longitude: longitude
            })
        }
    );

    if (emailResponse.ok) {

        console.log(
            "✅ SOS email sent to emergency contacts"
        );

        addIncidentEvent({
            type: "EMAIL",
            icon: "📧",
            title: "Emergency Email Sent",
            description:
                "SOS alert email was sent to emergency contacts."
        });

    } else {

        const error =
            await emailResponse.text();

        console.error(
            "❌ SOS email failed:",
            error
        );
    }

} catch (emailError) {

    console.error(
        "❌ Email service error:",
        emailError
    );
}

                // =================================================
                // RECORD GPS IN TIMELINE
                // =================================================

                addIncidentEvent({

                    type: "LOCATION",

                    icon: "📍",

                    title:
                        "GPS Location Updated",

                    description:
                        "Emergency location was recorded.",

                    latitude:
                        latitude,

                    longitude:
                        longitude,

                    accuracy:
                        accuracy

                });


                // =================================================
                // GET EMERGENCY CONTACTS
                // =================================================

                const response =
                    await fetch(

                        EMERGENCY_URL +
                        "/user/" +
                        userId,

                        {

                            method: "GET",

                            headers: {

                                "Authorization":
                                    "Bearer " + token

                            }

                        }

                    );


                if (!response.ok) {

                    throw new Error(
                        "Unable to get emergency contacts"
                    );
                }


                const contacts =
                    await response.json();


                // =================================================
                // NO CONTACTS
                // =================================================

                if (
                    contacts.length === 0
                ) {

                    if (status) {

                        status.innerHTML =

                            "🚨 <b>SOS ACTIVATED!</b><br><br>" +

                            "📍 Location recorded.<br><br>" +

                            "❌ No emergency contacts found.";

                    }


                    addIncidentEvent({

                        type: "EMERGENCY_RESPONSE",

                        icon: "🚑",

                        title:
                            "Emergency Response",

                        description:
                            "SOS activated but no emergency contacts were found.",

                        status:
                            "No contacts found"

                    });


                    return;
                }


                // =================================================
                // GOOGLE MAPS
                // =================================================

                const mapsUrl =
                    "https://www.google.com/maps/search/?api=1&query=" +
                    latitude +
                    "," +
                    longitude;


                // =================================================
                // EMERGENCY MESSAGE
                // =================================================

                const message =

                    "🚨 EMERGENCY ALERT 🚨\n\n" +

                    "I need help immediately.\n\n" +

                    "📍 My current location:\n" +

                    mapsUrl;


                // =================================================
                // STATUS
                // =================================================

                if (status) {

                    status.innerHTML =

                        "🚨 <b>SOS ACTIVATED!</b><br><br>" +

                        "📍 Location: " +

                        latitude +

                        ", " +

                        longitude +

                        "<br><br>" +

                        "👥 Emergency contacts found: " +

                        contacts.length;

                }


                // =================================================
                // RECORD EMERGENCY RESPONSE
                // =================================================

                addIncidentEvent({

                    type: "RESPONSE",

                    icon: "🚑",

                    title:
                        "Emergency Response Started",

                    description:
                        "SOS activated and emergency contacts were found.",

                    status:
                        contacts.length +
                        " emergency contacts available"

                });


                // =================================================
                // REMOVE OLD WHATSAPP BOX
                // =================================================

                const oldBox =
                    document.getElementById(
                        "whatsappContacts"
                    );


                if (oldBox) {

                    oldBox.remove();

                }


                // =================================================
                // CREATE WHATSAPP SECTION
                // =================================================

                const box =
                    document.createElement(
                        "div"
                    );


                box.id =
                    "whatsappContacts";


                const title =
                    document.createElement(
                        "h3"
                    );


                title.innerText =
                    "📱 Send Emergency Alert";


                box.appendChild(
                    title
                );


// =================================================
// GROUP CONTACTS
// =================================================

const contactGroups = {};

contacts.forEach(function(contact) {

    const groupName =
        contact.groupName || "Other";

    if (!contactGroups[groupName]) {
        contactGroups[groupName] = [];
    }

    contactGroups[groupName].push(contact);
});


// =================================================
// DISPLAY GROUPS
// =================================================

Object.keys(contactGroups).forEach(
    function(groupName) {

        const groupContacts =
            contactGroups[groupName];

        // Sort contacts by priority
        groupContacts.sort(
            function(a, b) {

                return Number(a.priority) -
                       Number(b.priority);
            }
        );

        const groupBox =
            document.createElement("div");

        groupBox.style.background =
            "#f8f7ff";

        groupBox.style.padding =
            "15px";

        groupBox.style.margin =
            "12px 0";

        groupBox.style.borderRadius =
            "12px";

        groupBox.style.border =
            "1px solid #ddd";


        // =================================================
        // GROUP CHECKBOX
        // =================================================

        const groupLabel =
            document.createElement("label");

        groupLabel.style.display =
            "flex";

        groupLabel.style.alignItems =
            "center";

        groupLabel.style.gap =
            "10px";

        groupLabel.style.fontSize =
            "18px";

        groupLabel.style.fontWeight =
            "bold";

        groupLabel.style.cursor =
            "pointer";


        const groupCheckbox =
            document.createElement("input");

        groupCheckbox.type =
            "checkbox";

        groupCheckbox.style.width =
            "20px";

        groupCheckbox.style.height =
            "20px";


        groupLabel.appendChild(
            groupCheckbox
        );


        const groupText =
            document.createElement("span");

        const firstPriority =
            groupContacts[0].priority;

        groupText.innerText =
            getGroupIcon(groupName) +
            " " +
            groupName +
            " — Priority " +
            firstPriority;


        groupLabel.appendChild(
            groupText
        );

        groupBox.appendChild(
            groupLabel
        );


        // =================================================
        // CONTACT LIST
        // =================================================

        const membersBox =
            document.createElement("div");

        membersBox.style.marginTop =
            "12px";

        membersBox.style.paddingLeft =
            "30px";


        const memberCheckboxes = [];


        groupContacts.forEach(
            function(contact) {

                const memberLabel =
                    document.createElement("label");

                memberLabel.style.display =
                    "block";

                memberLabel.style.margin =
                    "8px 0";

                memberLabel.style.cursor =
                    "pointer";


                const checkbox =
                    document.createElement("input");

                checkbox.type =
                    "checkbox";

                checkbox.className =
                    "emergency-contact-checkbox";

                checkbox.dataset.contactId =
                    contact.id;

                checkbox.style.marginRight =
                    "8px";


                memberLabel.appendChild(
                    checkbox
                );


                memberLabel.appendChild(
                    document.createTextNode(
                        "💬 " +
                        contact.contactName +
                        " (Priority " +
                        contact.priority +
                        ")"
                    )
                );


                membersBox.appendChild(
                    memberLabel
                );


                memberCheckboxes.push({
                    checkbox: checkbox,
                    contact: contact
                });

            }
        );


        groupBox.appendChild(
            membersBox
        );


        // =================================================
        // GROUP SELECT / UNSELECT
        // =================================================

        groupCheckbox.onchange =
            function() {

                memberCheckboxes.forEach(
                    function(item) {

                        item.checkbox.checked =
                            groupCheckbox.checked;

                    }
                );

            };


        // =================================================
        // IF MEMBER SELECTED MANUALLY
        // UPDATE GROUP CHECKBOX
        // =================================================

        memberCheckboxes.forEach(
            function(item) {

                item.checkbox.onchange =
                    function() {

                        const allSelected =
                            memberCheckboxes.every(
                                function(member) {
                                    return member
                                        .checkbox
                                        .checked;
                                }
                            );

                        groupCheckbox.checked =
                            allSelected;

                    };

            }
        );


        box.appendChild(
            groupBox
        );

    }
);


// =================================================
// SEND SELECTED BUTTON
// =================================================

const sendSelectedButton =
    document.createElement("button");


sendSelectedButton.innerHTML =
    "📤 Send Selected";


sendSelectedButton.style.display =
    "block";

sendSelectedButton.style.width =
    "100%";

sendSelectedButton.style.marginTop =
    "20px";

sendSelectedButton.style.padding =
    "16px";

sendSelectedButton.style.background =
    "#7c3aed";

sendSelectedButton.style.color =
    "white";

sendSelectedButton.style.border =
    "none";

sendSelectedButton.style.borderRadius =
    "10px";

sendSelectedButton.style.fontSize =
    "18px";

sendSelectedButton.style.fontWeight =
    "bold";

sendSelectedButton.style.cursor =
    "pointer";


// =================================================
// SEND SELECTED CONTACTS
// =================================================

// =================================================
// SEND SELECTED CONTACTS ONE BY ONE
// =================================================

let selectedContacts = [];

let currentContactIndex = 0;


// =================================================
// SEND SELECTED BUTTON
// =================================================

sendSelectedButton.onclick =
    function() {

        selectedContacts = [];

        // Get all checked contacts
        document
            .querySelectorAll(
                ".emergency-contact-checkbox"
            )
            .forEach(
                function(checkbox) {

                    if (checkbox.checked) {

                        const contactId =
                            checkbox.dataset.contactId;

                        contacts.forEach(
                            function(contact) {

                                if (
                                    String(contact.id) ===
                                    String(contactId)
                                ) {

                                    selectedContacts.push(
                                        contact
                                    );

                                }

                            }
                        );

                    }

                }
            );


        // =========================================
        // CHECK SELECTION
        // =========================================

        if (
            selectedContacts.length === 0
        ) {

            alert(
                "Please select at least one contact."
            );

            return;
        }


        // Start from first contact
        currentContactIndex = 0;


        // Show first contact
        showNextEmergencyContact();

    };


// =================================================
// SHOW NEXT CONTACT
// =================================================

function showNextEmergencyContact() {

    if (
        currentContactIndex >=
        selectedContacts.length
    ) {

        alert(
            "🚨 All selected contacts have been processed."
        );

        sendSelectedButton.innerHTML =
            "✅ All Contacts Processed";

        sendSelectedButton.disabled =
            true;

        return;
    }


    const contact =
        selectedContacts[
            currentContactIndex
        ];


    // =========================================
    // CREATE PHONE NUMBER
    // =========================================

    let phone =
        contact.contactNumber
            .replace(/\D/g, "");


    // India country code
    if (
        phone.length === 10
    ) {

        phone =
            "91" +
            phone;

    }


    // =========================================
    // CREATE WHATSAPP URL
    // =========================================

    const whatsappUrl =
        "https://wa.me/" +
        phone +
        "?text=" +
        encodeURIComponent(
            message
        );


    // =========================================
    // CREATE SENDING PANEL
    // =========================================

    const existingPanel =
        document.getElementById(
            "emergencySendingPanel"
        );


    if (existingPanel) {

        existingPanel.remove();

    }


    const panel =
        document.createElement("div");


    panel.id =
        "emergencySendingPanel";


    panel.style.background =
        "#ffffff";

    panel.style.padding =
        "20px";

    panel.style.marginTop =
        "20px";

    panel.style.borderRadius =
        "15px";

    panel.style.border =
        "2px solid #7c3aed";

    panel.style.textAlign =
        "center";


    // =========================================
    // PROGRESS
    // =========================================

    const progress =
        document.createElement("div");

    progress.innerHTML =
        "📤 Sending " +
        (currentContactIndex + 1) +
        " of " +
        selectedContacts.length;

    progress.style.fontSize =
        "18px";

    progress.style.fontWeight =
        "bold";

    progress.style.color =
        "#7c3aed";

    progress.style.marginBottom =
        "12px";


    panel.appendChild(
        progress
    );


    // =========================================
    // CONTACT NAME
    // =========================================

    const contactName =
        document.createElement("div");

    contactName.innerHTML =
        "💬 " +
        contact.contactName;

    contactName.style.fontSize =
        "24px";

    contactName.style.fontWeight =
        "bold";

    contactName.style.marginBottom =
        "8px";


    panel.appendChild(
        contactName
    );


    // =========================================
    // PHONE
    // =========================================

    const phoneText =
        document.createElement("div");

    phoneText.innerText =
        contact.contactNumber;

    phoneText.style.color =
        "#666";

    phoneText.style.marginBottom =
        "15px";


    panel.appendChild(
        phoneText
    );


    // =========================================
    // OPEN WHATSAPP BUTTON
    // =========================================

    const whatsappButton =
        document.createElement("button");


    whatsappButton.innerHTML =
        "💬 Open WhatsApp";


    whatsappButton.style.width =
        "100%";

    whatsappButton.style.padding =
        "15px";

    whatsappButton.style.background =
        "#25D366";

    whatsappButton.style.color =
        "white";

    whatsappButton.style.border =
        "none";

    whatsappButton.style.borderRadius =
        "10px";

    whatsappButton.style.fontSize =
        "18px";

    whatsappButton.style.fontWeight =
        "bold";

    whatsappButton.style.cursor =
        "pointer";


    whatsappButton.onclick =
        async function() {

            // Open WhatsApp
            window.open(
                whatsappUrl,
                "_blank"
            );


            // Save message record
            await saveMessageRecord({

                recipientName:
                    contact.contactName,

                recipientPhone:
                    contact.contactNumber,

                message:
                    message,

                deliveryStatus:
                    "SENT"

            });


            // Record timeline event
            addIncidentEvent({

                type:
                    "MESSAGE",

                icon:
                    "💬",

                title:
                    "Emergency Message",

                description:
                    "Emergency alert prepared for " +
                    contact.contactName,

                recipient:
                    contact.contactName,

                message:
                    message,

                status:
                    "WhatsApp opened"

            });


            // Change button
            whatsappButton.innerHTML =
                "✅ WhatsApp Opened";


            whatsappButton.disabled =
                true;


            whatsappButton.style.background =
                "#16a34a";


            // Enable next button
            nextButton.style.display =
                "block";

        };


    panel.appendChild(
        whatsappButton
    );


    // =========================================
    // NEXT CONTACT BUTTON
    // =========================================

    const nextButton =
        document.createElement("button");


    nextButton.innerHTML =
        currentContactIndex ===
        selectedContacts.length - 1

        ? "✅ Finish"

        : "➡️ Next Contact";


    nextButton.style.width =
        "100%";

    nextButton.style.padding =
        "15px";

    nextButton.style.marginTop =
        "12px";

    nextButton.style.background =
        "#7c3aed";

    nextButton.style.color =
        "white";

    nextButton.style.border =
        "none";

    nextButton.style.borderRadius =
        "10px";

    nextButton.style.fontSize =
        "18px";

    nextButton.style.fontWeight =
        "bold";

    nextButton.style.cursor =
        "pointer";


    // Initially hidden
    nextButton.style.display =
        "none";


    nextButton.onclick =
        function() {

            currentContactIndex++;

            showNextEmergencyContact();

        };


    panel.appendChild(
        nextButton
    );


    // =========================================
    // ADD PANEL
    // =========================================

    box.appendChild(
        panel
    );

}

box.appendChild(
    sendSelectedButton
);


                if (status) {

                    status.parentNode
                        .appendChild(box);

                }


            } catch (error) {

                console.error(
                    error
                );


                addIncidentEvent({

                    type:
                        "RESPONSE",

                    icon:
                        "❌",

                    title:
                        "Emergency Response Error",

                    description:
                        error.message,

                    status:
                        "Failed"

                });


                if (status) {

                    status.innerHTML =
                        "❌ SOS failed.";

                }


                alert(
                    error.message
                );

            }

        },


        function(error) {

            const status =
                document.getElementById(
                    "sosStatus"
                );


            if (status) {

                if (
                    error.code === 1
                ) {

                    status.innerHTML =
                        "❌ Please allow location permission.";

                }

                else if (
                    error.code === 2
                ) {

                    status.innerHTML =
                        "❌ Location unavailable.";

                }

                else if (
                    error.code === 3
                ) {

                    status.innerHTML =
                        "❌ Location request timed out.";

                }

                else {

                    status.innerHTML =
                        "❌ Unable to get location.";

                }

            }


            // Record GPS failure

            addIncidentEvent({

                type:
                    "RESPONSE",

                icon:
                    "❌",

                title:
                    "GPS Location Error",

                description:
                    "Unable to obtain emergency location.",

                status:
                    "GPS failed"

            });


            console.error(
                error
            );

        },

        {

            enableHighAccuracy:
                true,

            timeout:
                10000,

            maximumAge:
                0

        }

    );

}

async function createDatabaseIncident() {

    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
        console.error("Missing token or userId");
        return null;
    }

    try {

        const response = await fetch(
            `${BASE_URL}/api/incidents/create/${userId}`,
            {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        if (!response.ok) {

            const error = await response.text();

            console.error(
                "Failed to create incident:",
                error
            );

            return null;
        }

        const incident = await response.json();

        console.log(
            "Incident created:",
            incident
        );

        // Save incident ID
        localStorage.setItem(
            "currentIncidentId",
            incident.id
        );

        return incident.id;

    } catch (error) {

        console.error(
            "Incident creation error:",
            error
        );

        return null;
    }
}
function callPolice() {

    const phoneNumber = "112";


    /*
     * Save call record
     */
    saveCallRecord({
        contactName: "Police",
        phoneNumber: phoneNumber,
        callStatus: "INITIATED",
        durationSeconds: 0
    });


    /*
     * Add incident timeline event
     */
    addIncidentEvent({

        type: "CALL",

        icon: "📞",

        title: "Police Call",

        description:
            "Police emergency call initiated.",

        number: phoneNumber,

        status: "Call initiated"

    });


    /*
     * REAL PHONE CALL
     */
    callEmergencyContact(
        phoneNumber
    );
}
function callAmbulance() {

    const phoneNumber = "108";


    saveCallRecord({

        contactName: "Ambulance",

        phoneNumber: phoneNumber,

        callStatus: "INITIATED",

        durationSeconds: 0

    });


    addIncidentEvent({

        type: "CALL",

        icon: "📞",

        title: "Ambulance Call",

        description:
            "Ambulance emergency call initiated.",

        number: phoneNumber,

        status: "Call initiated"

    });


    callEmergencyContact(
        phoneNumber
    );
}
function callWomenHelpline() {

    const phoneNumber = "181";


    saveCallRecord({

        contactName: "Women Helpline",

        phoneNumber: phoneNumber,

        callStatus: "INITIATED",

        durationSeconds: 0

    });


    addIncidentEvent({

        type: "CALL",

        icon: "📞",

        title: "Women Helpline Call",

        description:
            "Women Helpline call initiated.",

        number: phoneNumber,

        status: "Call initiated"

    });


    callEmergencyContact(
        phoneNumber
    );
}


// =====================================================
// SHARE LOCATION
// =====================================================

function shareLocation() {

    if (!navigator.geolocation) {

        alert(
            "Location is not supported by this browser."
        );

        return;
    }

    navigator.geolocation.getCurrentPosition(

        function(position) {

            const latitude =
                position.coords.latitude;

            const longitude =
                position.coords.longitude;

            const mapsUrl =
                "https://www.google.com/maps/search/?api=1&query=" +
                latitude +
                "," +
                longitude;

            const message =
                "🚨 EMERGENCY ALERT 🚨\n\n" +
                "I need help.\n\n" +
                "📍 My current location:\n" +
                mapsUrl;


            if (navigator.share) {

                navigator.share({

                    title:
                        "Emergency Location",

                    text:
                        message

                })
                .catch(
                    function(error) {

                        console.log(
                            "Share cancelled:",
                            error
                        );

                        window.open(
                            mapsUrl,
                            "_blank"
                        );
                    }
                );

            } else {

                window.open(
                    mapsUrl,
                    "_blank"
                );
            }

        },

        function(error) {

            if (error.code === 1) {

                alert(
                    "Please allow location permission."
                );

            } else if (error.code === 2) {

                alert(
                    "Location is unavailable."
                );

            } else if (error.code === 3) {

                alert(
                    "Location request timed out."
                );

            } else {

                alert(
                    "Unable to get your location."
                );
            }
        },

        {

            enableHighAccuracy: true,

            timeout: 10000,

            maximumAge: 0
        }
    );
}


// =====================================================
// ADD / UPDATE EMERGENCY CONTACT
// =====================================================

async function addEmergencyContact() {

    const token =
        localStorage.getItem("token");

    const userId =
        localStorage.getItem("userId");

    if (!token || !userId) {

        alert("Please login first.");

        window.location.href =
            "index.html";

        return;
    }

    const contactIdElement =
        document.getElementById("contactId");

    const contactNameElement =
        document.getElementById("contactName");

    const contactNumberElement =
        document.getElementById("contactNumber");

    const contactEmailElement =
        document.getElementById("contactEmail");

    const relationshipElement =
        document.getElementById("relationship");

    const groupNameElement =
        document.getElementById("groupName");

    const priorityElement =
        document.getElementById("priority");


    if (
        !contactNameElement ||
        !contactNumberElement ||
        !contactEmailElement ||
        !relationshipElement ||
        !groupNameElement ||
        !priorityElement
    ) {

        alert("Contact form fields not found.");

        return;
    }


    const contactId =
        contactIdElement
            ? contactIdElement.value
            : "";


    const contactName =
        contactNameElement.value.trim();

    const contactNumber =
        contactNumberElement.value.trim();

    const contactEmail =
        contactEmailElement.value.trim();

    const relationship =
        relationshipElement.value.trim();

    const groupName =
        groupNameElement.value;

    const priority =
        Number(priorityElement.value);


    // ==============================
    // VALIDATION
    // ==============================

    if (contactName === "") {

        alert("Please enter contact name.");

        return;
    }

    if (contactNumber === "") {

        alert("Please enter contact number.");

        return;
    }

    if (contactEmail === "") {

        alert("Please enter contact email.");

        return;
    }

    if (relationship === "") {

        alert("Please enter relationship.");

        return;
    }

    if (groupName === "") {

        alert("Please select contact group.");

        return;
    }


    // ==============================
    // CONTACT OBJECT
    // ==============================

    const contact = {

        userId:
            Number(userId),

        contactName:
            contactName,

        contactNumber:
            contactNumber,

        contactEmail:
            contactEmail,

        relationship:
            relationship,

        groupName:
            groupName,

        priority:
            priority
    };


    try {

        let url;
        let method;


        // ==============================
        // UPDATE
        // ==============================

        if (contactId) {

            url =
                EMERGENCY_URL +
                "/update/" +
                contactId;

            method =
                "PUT";

        } else {

            // ==========================
            // ADD
            // ==========================

            url =
                EMERGENCY_URL +
                "/add";

            method =
                "POST";
        }


        const response =
            await fetch(
                url,
                {

                    method:
                        method,

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " +
                            token
                    },

                    body:
                        JSON.stringify(
                            contact
                        )
                }
            );


        const message =
            await response.text();


        if (!response.ok) {

            alert(
                "Operation failed:\n" +
                response.status +
                "\n" +
                message
            );

            return;
        }


        if (contactId) {

            alert(
                "Emergency contact updated successfully!"
            );

        } else {

            alert(
                "Emergency contact added successfully!"
            );
        }


        clearContactForm();

        await loadEmergencyContacts();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to backend."
        );
    }
}


// =====================================================
// LOAD EMERGENCY CONTACTS
// =====================================================

async function loadEmergencyContacts() {

    const token =
        localStorage.getItem("token");

    const userId =
        localStorage.getItem("userId");

    if (!token || !userId) {

        console.error(
            "Missing token or userId"
        );

        return;
    }


    const container =
        document.getElementById(
            "contactsContainer"
        );


    if (!container) {
        return;
    }


    try {

        container.innerHTML = `
            <div class="contacts-loading">
                ⏳ Loading contacts...
            </div>
        `;


        const response =
            await fetch(
                EMERGENCY_URL +
                "/user/" +
                userId,
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                            "Bearer " +
                            token
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to load contacts"
            );

        }


        const contacts =
            await response.json();


        container.innerHTML = "";


        /* =========================================
           CONTACT COUNT
        ========================================= */

        const countElement =
            document.getElementById(
                "contactCount"
            );


        if (countElement) {

            countElement.innerText =
                contacts
                    ? contacts.length
                    : 0;

        }


        /* =========================================
           NO CONTACTS
        ========================================= */

        if (
            !contacts ||
            contacts.length === 0
        ) {

            container.innerHTML = `

                <div class="contacts-empty">

                    <div class="contacts-empty-icon">
                        👥
                    </div>

                    <h3>
                        No emergency contacts yet
                    </h3>

                    <p>
                        Add someone you trust to
                        receive emergency assistance.
                    </p>

                </div>

            `;

            return;
        }


        /* =========================================
           SORT BY PRIORITY
        ========================================= */

        contacts.sort(
            function(a, b) {

                return (
                    Number(a.priority || 999) -
                    Number(b.priority || 999)
                );

            }
        );


        /* =========================================
           CREATE MOBILE CONTACT CARDS
        ========================================= */

        contacts.forEach(
            function(contact) {


                const card =
                    document.createElement(
                        "div"
                    );


                card.className =
                    "she-safe-contact-card";


                /* =================================
                   INITIAL
                ================================= */

                const name =
                    contact.contactName ||
                    "Emergency Contact";


                const initial =
                    name
                        .trim()
                        .charAt(0)
                        .toUpperCase();


                /* =================================
                   PHONE
                ================================= */

                const phone =
                    contact.contactNumber ||
                    "";


                /* =================================
                   RELATIONSHIP
                ================================= */

                const relationship =
                    contact.relationship ||
                    "";


                /* =================================
                   GROUP
                ================================= */

                const group =
                    contact.groupName ||
                    "Other";


                /* =================================
                   CARD HTML
                ================================= */

                card.innerHTML = `

                    <div class="contact-avatar">

                        ${initial}

                    </div>


                    <div class="contact-main-info">

                        <h3>

                            ${escapeHtml(
                                name
                            )}

                        </h3>


                        <p>

                            ${phone
                                ? escapeHtml(phone)
                                : "No phone number"}

                        </p>


                        ${
                            relationship
                            ?
                            `
                                <span
                                    class="contact-relationship">

                                    ${escapeHtml(
                                        relationship
                                    )}

                                </span>
                            `
                            :
                            ""
                        }

                    </div>


                    <div class="contact-actions">


                        <!-- CALL -->

                        ${
                            phone
                            ?
                            `
                                <button
                                    type="button"
                                    class="contact-call-button"
                                    onclick="callEmergencyContact('${escapeJs(phone)}')">

                                    📞

                                </button>
                            `
                            :
                            ""
                        }


                        <!-- EDIT -->

                        <button
                            type="button"
                            class="contact-call-button"
                            onclick="editEmergencyContactById(${contact.id})">

                            ✏️

                        </button>


                        <!-- DELETE -->

                        <button
                            type="button"
                            class="contact-delete-button"
                            onclick="deleteEmergencyContact(${contact.id})">

                            ×

                        </button>

                    </div>

                `;


                container.appendChild(
                    card
                );

            }
        );


    } catch (error) {

        console.error(
            "Unable to load emergency contacts:",
            error
        );


        container.innerHTML = `

            <div class="contacts-empty">

                <div class="contacts-empty-icon">
                    ⚠️
                </div>

                <h3>
                    Unable to load contacts
                </h3>

                <p>
                    Please check your connection
                    and try again.
                </p>

            </div>

        `;

    }

}
/* =====================================================
   ESCAPE HTML
===================================================== */

function escapeHtml(value) {

    return String(value || "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

}


/* =====================================================
   ESCAPE JAVASCRIPT STRING
===================================================== */

function escapeJs(value) {

    return String(value || "")
        .replace(/\\/g, "\\\\")
        .replace(/'/g, "\\'");

}


/* =====================================================
   CALL CONTACT
===================================================== */


async function callEmergencyContact(phone) {
    if (!phone) {
        alert("No phone number available.");
        return;
    }

    const cleanNumber = String(phone).replace(/[^\d+]/g, "");

    if (!cleanNumber) {
        alert("Invalid phone number.");
        return;
    }

    // Native Android / Capacitor
    if (
        window.Capacitor &&
        window.Capacitor.isNativePlatform()
    ) {
        try {
            const PhoneCall =
                window.Capacitor?.Plugins?.PhoneCall;

            if (PhoneCall && typeof PhoneCall.call === "function") {
                await PhoneCall.call({ number: cleanNumber });
                return;
            }

            console.warn(
                "PhoneCall plugin is unavailable. Opening dialer instead."
            );
        } catch (error) {
            console.error("Phone call plugin error:", error);
            alert("Direct calling failed. Opening the phone dialer instead.");
        }
    }

    // Fallback: opens the dialer; user confirms the call.
    window.location.href = "tel:" + cleanNumber;
}

/* =====================================================
   EDIT CONTACT BY ID
===================================================== */

async function editEmergencyContactById(id) {

    const token =
        localStorage.getItem("token");

    const userId =
        localStorage.getItem("userId");


    if (!token || !userId) {
        return;
    }


    try {

        const response =
            await fetch(
                EMERGENCY_URL +
                "/user/" +
                userId,
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                            "Bearer " +
                            token
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to load contact"
            );

        }


        const contacts =
            await response.json();


        const contact =
            contacts.find(
                function(item) {

                    return (
                        Number(item.id) ===
                        Number(id)
                    );

                }
            );


        if (!contact) {

            alert(
                "Contact not found."
            );

            return;

        }


        /* =====================================
           FILL FORM
        ===================================== */

        document.getElementById(
            "contactId"
        ).value =
            contact.id || "";


        document.getElementById(
            "contactName"
        ).value =
            contact.contactName || "";


        document.getElementById(
            "contactNumber"
        ).value =
            contact.contactNumber || "";


        document.getElementById(
            "contactEmail"
        ).value =
            contact.contactEmail || "";


        document.getElementById(
            "relationship"
        ).value =
            contact.relationship || "";


        document.getElementById(
            "groupName"
        ).value =
            contact.groupName || "";


        document.getElementById(
            "priority"
        ).value =
            contact.priority || "1";


        /* =====================================
           CHANGE TITLE
        ===================================== */

        const title =
            document.getElementById(
                "contactFormTitle"
            );


        if (title) {

            title.innerText =
                "Edit Contact";

        }


        const saveButton =
            document.getElementById(
                "contactSaveButton"
            );


        if (saveButton) {

            saveButton.innerText =
                "✓ Update Contact";

        }


        /* =====================================
           OPEN BOTTOM SHEET
        ===================================== */

        const overlay =
            document.getElementById(
                "contactFormOverlay"
            );


        if (overlay) {

            overlay.classList.add(
                "show"
            );

        }


    } catch (error) {

        console.error(
            "Edit contact error:",
            error
        );

        alert(
            "Unable to load contact."
        );

    }

}


// =====================================================
// EDIT EMERGENCY CONTACT
// =====================================================

function editEmergencyContact(
    contact
) {

    const contactId =
        document.getElementById(
            "contactId"
        );

    const contactName =
        document.getElementById(
            "contactName"
        );

    const contactNumber =
        document.getElementById(
            "contactNumber"
        );
        const contactEmail =
    document.getElementById("contactEmail");

    const relationship =
        document.getElementById(
            "relationship"
        );

    const groupName =
        document.getElementById(
            "groupName"
        );

    const priority =
        document.getElementById(
            "priority"
        );


    if (contactId) {

        contactId.value =
            contact.id;
    }

    if (contactName) {

        contactName.value =
            contact.contactName || "";
    }

    if (contactNumber) {

        contactNumber.value =
            contact.contactNumber || "";
    }
    if (contactEmail) {

    contactEmail.value =
        contact.contactEmail || "";
}

    if (relationship) {

        relationship.value =
            contact.relationship || "";
    }

    if (groupName) {

        groupName.value =
            contact.groupName ||
            "Other";
    }

    if (priority) {

        priority.value =
            contact.priority || "1";
    }


    const title =
        document.getElementById(
            "contactFormTitle"
        );

    if (title) {

        title.innerText =
            "Edit Emergency Contact";
    }


    const saveButton =
        document.getElementById(
            "contactSaveButton"
        );

    if (saveButton) {

        saveButton.innerText =
            "✓ Update Contact";
    }


    const cancelButton =
        document.getElementById(
            "cancelEditButton"
        );

    if (cancelButton) {

        cancelButton.style.display =
            "block";
    }


    window.scrollTo({

        top: 0,

        behavior: "smooth"
    });
}


// =====================================================
// DELETE EMERGENCY CONTACT
// =====================================================

async function deleteEmergencyContact(
    id
) {

    const token =
        localStorage.getItem(
            "token"
        );


    if (!token) {

        alert(
            "Please login first."
        );

        return;
    }


    const confirmDelete =
        confirm(
            "Are you sure you want to delete this contact?"
        );


    if (!confirmDelete) {

        return;
    }


    try {

        const response =
            await fetch(
                EMERGENCY_URL +
                "/delete/" +
                id,
                {

                    method: "DELETE",

                    headers: {

                        "Authorization":
                            "Bearer " +
                            token
                    }
                }
            );


        const message =
            await response.text();


        if (!response.ok) {

            alert(
                "Delete failed:\n" +
                message
            );

            return;
        }


        alert(
            message ||
            "Emergency contact deleted successfully!"
        );


        await loadEmergencyContacts();


    } catch (error) {

        console.error(
            error
        );

        alert(
            "Unable to connect to backend."
        );
    }
}


// =====================================================
// GROUP ICON
// =====================================================

function getGroupIcon(
    groupName
) {

    if (
        groupName ===
        "Family"
    ) {

        return "👨‍👩‍👧";
    }


    if (
        groupName ===
        "Friends"
    ) {

        return "👭";
    }


    if (
        groupName ===
        "Colleagues"
    ) {

        return "💼";
    }


    return "👤";
}


// =====================================================
// CLEAR CONTACT FORM
// =====================================================

function clearContactForm() {

    const contactId =
        document.getElementById(
            "contactId"
        );

    if (contactId) {

        contactId.value = "";
    }


    const contactName =
        document.getElementById(
            "contactName"
        );

    if (contactName) {

        contactName.value = "";
    }


    const contactNumber =
        document.getElementById(
            "contactNumber"
        );

    if (contactNumber) {

        contactNumber.value = "";
    }


    const relationship =
        document.getElementById(
            "relationship"
        );

    if (relationship) {

        relationship.value = "";
    }


    const groupName =
        document.getElementById(
            "groupName"
        );

    if (groupName) {

        groupName.value = "";
    }


    const priority =
        document.getElementById(
            "priority"
        );

    if (priority) {

        priority.value = "1";
    }


    const title =
        document.getElementById(
            "contactFormTitle"
        );

    if (title) {

        title.innerText =
            "Add Emergency Contact";
    }


    const saveButton =
        document.getElementById(
            "contactSaveButton"
        );

    if (saveButton) {

        saveButton.innerText =
            "➕ Add Contact";
    }


    const cancelButton =
        document.getElementById(
            "cancelEditButton"
        );

    if (cancelButton) {

        cancelButton.style.display =
            "none";
    }
}


// =====================================================
// CANCEL EDIT
// =====================================================

function cancelContactEdit() {

    clearContactForm();
}


// =====================================================
// FINISH PROFILE / CONTACT SETUP
// =====================================================

function finishSetup() {

    window.location.href =
        "home.html";
}


// =====================================================
// CHATBOT
// =====================================================

function sendMessage() {

    const input =
        document.getElementById(
            "userMessage"
        );

    const chat =
        document.getElementById(
            "chatBox"
        );

    if (!input || !chat) {
        return;
    }


    if (
        input.value.trim() === ""
    ) {

        return;
    }


    chat.innerHTML +=

        `<div class="user-message">
            ${input.value}
        </div>`;


    chat.innerHTML +=

        `<div class="bot-message">
            AI Assistant is thinking...
        </div>`;


    input.value = "";


    chat.scrollTop =
        chat.scrollHeight;
}


// =====================================================
// SAFE ROUTE
// =====================================================

async function findRoute() {

    const destinationElement =
        document.getElementById("destination");

    const routeResult =
        document.getElementById("routeResult");

    const routeDistance =
        document.getElementById("routeDistance");

    const routeETA =
        document.getElementById("routeETA");


    // Check destination field

    if (!destinationElement) {

        alert("Destination field not found.");

        return;
    }


    const destination =
        destinationElement.value.trim();


    if (destination === "") {

        alert("Please enter a destination.");

        return;
    }


    // Show loading message

    if (routeResult) {

        routeResult.innerHTML =
            "📍 Getting your current location...";
    }


    // Check GPS support

    if (!navigator.geolocation) {

        if (routeResult) {

            routeResult.innerHTML =
                "❌ GPS is not supported by your browser.";
        }

        return;
    }


    // Get current location

    navigator.geolocation.getCurrentPosition(

        function(position) {

            const latitude =
                position.coords.latitude;

            const longitude =
                position.coords.longitude;


            // ==========================================
            // GOOGLE MAPS DIRECTIONS URL
            // ==========================================

            const mapsUrl =
                "https://www.google.com/maps/dir/?api=1" +
                "&origin=" +
                latitude +
                "," +
                longitude +
                "&destination=" +
                encodeURIComponent(destination) +
                "&travelmode=driving";


            // ==========================================
            // SHOW RESULT
            // ==========================================

            if (routeResult) {

                routeResult.innerHTML =

                    "<h3>🗺️ Route Found</h3>" +

                    "<p>" +
                    "<strong>Starting Point:</strong><br>" +
                    "📍 Your Current Location" +
                    "</p>" +

                    "<p>" +
                    "<strong>Destination:</strong><br>" +
                    "📌 " +
                    destination +
                    "</p>" +

                    "<p style='color:green'>" +
                    "✅ Route is ready." +
                    "</p>" +

                    "<button onclick=\"window.open('" +
                    mapsUrl +
                    "','_blank')\">" +

                    "🚗 Open Route in Google Maps" +

                    "</button>";
            }


            // ==========================================
            // ROUTE INFORMATION
            // ==========================================

            if (routeDistance) {

                routeDistance.innerText =
                    "Available in Google Maps";
            }


            if (routeETA) {

                routeETA.innerText =
                    "Available in Google Maps";
            }


            // ==========================================
            // AUTOMATICALLY OPEN GOOGLE MAPS
            // ==========================================

            window.open(
                mapsUrl,
                "_blank"
            );

        },


        // ==========================================
        // GPS ERROR
        // ==========================================

        function(error) {

            console.error(
                "Route GPS error:",
                error
            );


            if (routeResult) {

                routeResult.innerHTML =
                    "❌ Unable to get your current location.";
            }


            if (error.code === 1) {

                alert(
                    "Please allow location permission."
                );

            } else if (error.code === 2) {

                alert(
                    "Your location could not be determined."
                );

            } else if (error.code === 3) {

                alert(
                    "GPS request timed out."
                );

            } else {

                alert(
                    "Unable to get your current location."
                );
            }

        },


        // ==========================================
        // GPS OPTIONS
        // ==========================================

        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }

    );
}

// =====================================================
// DASHBOARD NAVIGATION
// =====================================================

function openGPS() {

    window.location.href =
        "gps.html";
}


function openContacts() {

    window.location.href =
        "contacts.html";
}


function openSOS() {

    window.location.href =
        "sos.html";
}


function openProfile() {

    window.location.href =
        "profile.html";
}


function openChatbot() {

    window.location.href =
        "chatbot.html";
}


function openRoute() {

    window.location.href =
        "route.html";
}


// =====================================================
// FORGOT PASSWORD - SEND OTP
// =====================================================

async function sendForgotOtp() {

    const email =
        document
            .getElementById(
                "forgotEmail"
            )
            .value
            .trim();


    if (email === "") {

        alert(
            "Please enter your email."
        );

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL +
                "/api/password/send-otp",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email:
                            email
                    })
                }
            );


        const message =
            await response.text();


        console.log(
            "HTTP Status:",
            response.status
        );

        console.log(
            "Backend Response:",
            message
        );


        if (!response.ok) {

            alert(
                "Server Error (" +
                response.status +
                "):\n\n" +
                (
                    message ||
                    "No error message returned by server."
                )
            );

            return;
        }


        if (
            !message ||
            message.trim() === ""
        ) {

            alert(
                "Backend returned an empty response."
            );

            return;
        }


        alert(
            message
        );


        if (
            message.trim() ===
            "OTP sent successfully"
        ) {

            const emailSection =
                document.getElementById(
                    "emailSection"
                );

            const otpSection =
                document.getElementById(
                    "otpSection"
                );


            if (emailSection) {

                emailSection.style.display =
                    "none";
            }


            if (otpSection) {

                otpSection.style.display =
                    "block";
            }
        }

    } catch (error) {

        console.error(
            "Forgot password error:",
            error
        );

        alert(
            "Cannot connect to backend.\n\n" +
            error.message
        );
    }
}


// =====================================================
// RESET PASSWORD
// =====================================================

async function resetPassword() {

    const email =
        document.getElementById(
            "forgotEmail"
        ).value.trim();


    const otp =
        document.getElementById(
            "forgotOtp"
        ).value.trim();


    const newPassword =
        document.getElementById(
            "newPassword"
        ).value;


    const confirmPassword =
        document.getElementById(
            "confirmPassword"
        ).value;


    if (otp === "") {

        alert(
            "Please enter OTP."
        );

        return;
    }


    if (newPassword === "") {

        alert(
            "Please enter new password."
        );

        return;
    }


    if (
        newPassword !==
        confirmPassword
    ) {

        alert(
            "Passwords do not match."
        );

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL +
                "/api/password/reset",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({

                        email:
                            email,

                        otp:
                            otp,

                        newPassword:
                            newPassword
                    })
                }
            );


        const message =
            await response.text();


        alert(
            message
        );


        if (
            message ===
            "Password reset successfully"
        ) {

            window.location.href =
                "index.html";
        }


    } catch (error) {

        console.error(
            error
        );

        alert(
            "Cannot connect to server."
        );
    }
}


// =====================================================
// AUTOMATIC PAGE LOADING
// =====================================================

document.addEventListener(
    "DOMContentLoaded",
    function() {

        // Profile page

        if (
            document.getElementById(
                "medicalConditions"
            )
        ) {

            loadProfile();
        }


        // Emergency contacts page

        if (
            document.getElementById(
                "contactsContainer"
            ) ||
            document.getElementById(
                "contactsTable"
            )
        ) {

            loadEmergencyContacts();
        }
    }
);
// =====================================================
// LIVE GPS TRACKING + INTERACTIVE MAP
// =====================================================

let gpsWatchId = null;

let currentLatitude = null;
let currentLongitude = null;
let currentAccuracy = null;

let liveMap = null;
let locationMarker = null;
let accuracyCircle = null;


// =====================================================
// INITIALIZE LIVE MAP
// =====================================================

function initializeLiveMap(latitude, longitude) {

    // Check if Leaflet is loaded
    if (typeof L === "undefined") {

        console.error("Leaflet library is not loaded.");

        const status =
            document.getElementById("locationStatus");

        if (status) {
            status.innerText =
                "❌ Map library could not be loaded.";
        }

        return;
    }


    // Create map only once
    if (liveMap === null) {

        liveMap = L.map("liveMap").setView(
            [latitude, longitude],
            16
        );


        // OpenStreetMap tiles
        L.tileLayer(
            "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            {
                maxZoom: 19,

                attribution:
                    "&copy; OpenStreetMap contributors"
            }
        ).addTo(liveMap);

    }
}


// =====================================================
// UPDATE LIVE MAP
// =====================================================

function updateLiveMap(
    latitude,
    longitude,
    accuracy
) {

    initializeLiveMap(
        latitude,
        longitude
    );


    if (!liveMap) {
        return;
    }


    // ================================================
    // CREATE / UPDATE LOCATION MARKER
    // ================================================

    if (locationMarker === null) {

        locationMarker =
            L.marker([
                latitude,
                longitude
            ])
            .addTo(liveMap)
            .bindPopup(
                "📍 <b>Your Current Location</b>"
            );

    } else {

        locationMarker.setLatLng([
            latitude,
            longitude
        ]);
    }


    // ================================================
    // CREATE / UPDATE ACCURACY CIRCLE
    // ================================================

    if (accuracyCircle === null) {

        accuracyCircle =
            L.circle(
                [
                    latitude,
                    longitude
                ],
                {
                    radius: accuracy
                }
            ).addTo(liveMap);

    } else {

        accuracyCircle.setLatLng([
            latitude,
            longitude
        ]);

        accuracyCircle.setRadius(
            accuracy
        );
    }


    // ================================================
    // MOVE MAP TO CURRENT LOCATION
    // ================================================

    liveMap.setView(
        [
            latitude,
            longitude
        ],
        16
    );
}


// =====================================================
// START GPS TRACKING
// =================================================// =====================================================
// START GPS TRACKING
// =====================================================

async function startGPSTracking() {

    if (gpsWatchId !== null) {

        console.log(
            "GPS tracking is already active."
        );

        return;
    }


    const locationStatus =
        document.getElementById(
            "locationStatus"
        );

    const trackingStatus =
        document.getElementById(
            "trackingStatus"
        );


    if (locationStatus) {

        locationStatus.innerText =
            "🟡 Getting your current location...";
    }


    if (trackingStatus) {

        trackingStatus.innerText =
            "Starting...";
    }


    try {

        // =================================================
        // GET FIRST LOCATION
        // =================================================

        const position =
            await getSheSafeCurrentLocation();


        currentLatitude =
            position.coords.latitude;

        currentLongitude =
            position.coords.longitude;

        currentAccuracy =
            position.coords.accuracy;


        console.log(
            "📍 Initial location:",
            currentLatitude,
            currentLongitude
        );


        // =================================================
        // UPDATE UI
        // =================================================

        const latitudeElement =
            document.getElementById("latitude");

        if (latitudeElement) {

            latitudeElement.value =
                currentLatitude.toFixed(6);
        }


        const longitudeElement =
            document.getElementById("longitude");

        if (longitudeElement) {

            longitudeElement.value =
                currentLongitude.toFixed(6);
        }


        const accuracyElement =
            document.getElementById("accuracy");

        if (accuracyElement) {

            accuracyElement.value =
                currentAccuracy.toFixed(2)
                + " meters";
        }


        if (locationStatus) {

            locationStatus.innerText =
                "🟢 Live location detected.";
        }


        if (trackingStatus) {

            trackingStatus.innerText =
                "Active";
        }


        const gpsAccuracy =
            document.getElementById(
                "gpsAccuracy"
            );

        if (gpsAccuracy) {

            gpsAccuracy.innerText =
                currentAccuracy.toFixed(2)
                + " meters";
        }


        const lastUpdated =
            document.getElementById(
                "lastUpdated"
            );

        if (lastUpdated) {

            lastUpdated.innerText =
                new Date().toLocaleTimeString();
        }


        // =================================================
        // UPDATE MAP
        // =================================================

        updateLiveMap(
            currentLatitude,
            currentLongitude,
            currentAccuracy
        );


        // =================================================
        // SAVE LOCATION
        // =================================================

        await saveLiveLocationToBackend();


        // =================================================
        // START CONTINUOUS UPDATES
        // =================================================

        if (navigator.geolocation) {

            gpsWatchId =
                navigator.geolocation.watchPosition(

                    async function(position) {

                        currentLatitude =
                            position.coords.latitude;

                        currentLongitude =
                            position.coords.longitude;

                        currentAccuracy =
                            position.coords.accuracy;


                        console.log(
                            "📍 Location updated:",
                            currentLatitude,
                            currentLongitude
                        );


                        const lat =
                            document.getElementById(
                                "latitude"
                            );

                        if (lat) {

                            lat.value =
                                currentLatitude.toFixed(6);
                        }


                        const lon =
                            document.getElementById(
                                "longitude"
                            );

                        if (lon) {

                            lon.value =
                                currentLongitude.toFixed(6);
                        }


                        const accuracy =
                            document.getElementById(
                                "accuracy"
                            );

                        if (accuracy) {

                            accuracy.value =
                                currentAccuracy.toFixed(2)
                                + " meters";
                        }


                        if (locationStatus) {

                            locationStatus.innerText =
                                "🟢 Live GPS tracking is active.";
                        }


                        if (trackingStatus) {

                            trackingStatus.innerText =
                                "Active";
                        }


                        if (gpsAccuracy) {

                            gpsAccuracy.innerText =
                                currentAccuracy.toFixed(2)
                                + " meters";
                        }


                        if (lastUpdated) {

                            lastUpdated.innerText =
                                new Date().toLocaleTimeString();
                        }


                        updateLiveMap(
                            currentLatitude,
                            currentLongitude,
                            currentAccuracy
                        );


                        await saveLiveLocationToBackend();

                    },


                    function(error) {

                        console.warn(
                            "⚠️ Continuous GPS update error:",
                            error
                        );

                        // Do NOT immediately destroy
                        // the current known location.

                        if (locationStatus) {

                            locationStatus.innerText =
                                "🟡 Location monitoring continues...";
                        }
                    },


                    {
                        enableHighAccuracy: true,

                        maximumAge: 30000,

                        timeout: 30000
                    }

                );
        }


    } catch (error) {

        console.error(
            "❌ Unable to obtain location:",
            error
        );


        if (locationStatus) {

            locationStatus.innerText =
                "🔴 Unable to obtain current location.";
        }


        if (trackingStatus) {

            trackingStatus.innerText =
                "Location unavailable";
        }


        alert(
            "Unable to obtain your current location.\n\n" +
            "Please make sure Location is ON and try again."
        );
    }
}


// =====================================================
// SAVE LIVE LOCATION TO BACKEND
// =====================================================

async function saveLiveLocationToBackend() {

    const token =
        localStorage.getItem("token");

    const userId =
        localStorage.getItem("userId");

    const incidentId =
        localStorage.getItem("currentIncidentId");


    if (
        !token ||
        !userId ||
        currentLatitude === null ||
        currentLongitude === null
    ) {
        return;
    }





    try {

        const response =
            await fetch(
                LIVE_LOCATION_URL + "/update",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token

                    },

                    body:
    JSON.stringify({

        userId:
            Number(userId),

        

        latitude:
            currentLatitude,

        longitude:
            currentLongitude,

       
    })

                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();

            console.error(
                "Unable to save live location:",
                errorText
            );

            return;
        }


       


    } catch (error) {

        console.error(
            "Live location save error:",
            error
        );

    }
}


// =====================================================
// STOP GPS TRACKING
// =====================================================

function stopGPSTracking() {

    if (gpsWatchId !== null) {

        navigator.geolocation.clearWatch(
            gpsWatchId
        );

        gpsWatchId = null;
    }


    const locationStatus =
        document.getElementById(
            "locationStatus"
        );

    const trackingStatus =
        document.getElementById(
            "trackingStatus"
        );


    if (locationStatus) {

        locationStatus.innerText =
            "🔴 Live GPS tracking stopped.";
    }


    if (trackingStatus) {

        trackingStatus.innerText =
            "Not Active";
    }
}


// =====================================================
// OPEN CURRENT LOCATION IN GOOGLE MAPS
// =====================================================

function openMap() {

    if (
        currentLatitude === null ||
        currentLongitude === null
    ) {

        alert(
            "Please start GPS tracking first."
        );

        return;
    }


    const mapUrl =
        "https://www.google.com/maps?q="
        + currentLatitude
        + ","
        + currentLongitude;


    window.open(
        mapUrl,
        "_blank"
    );
}


// =====================================================
// SHARE LIVE LOCATION
// =====================================================

function shareLiveLocation() {

    if (
        currentLatitude === null ||
        currentLongitude === null
    ) {

        alert(
            "Please start GPS tracking first."
        );

        return;
    }


    const locationUrl =
        "https://www.google.com/maps?q="
        + currentLatitude
        + ","
        + currentLongitude;


    const message =
        "📍 SheSafe Live Location\n\n"
        +
        "I am sharing my current location with you.\n\n"
        +
        locationUrl;


    // Mobile share
    if (navigator.share) {

        navigator.share({

            title:
                "SheSafe Live Location",

            text:
                message

        }).catch(
            function(error) {

                console.log(
                    "Share cancelled:",
                    error
                );
            }
        );

    }

    // Desktop fallback
    else {

        if (
            navigator.clipboard
        ) {

            navigator.clipboard
                .writeText(message)
                .then(
                    function() {

                        alert(
                            "Location link copied. You can now send it to your family."
                        );
                    }
                );

        } else {

            window.open(
                locationUrl,
                "_blank"
            );
        }
    }
}
// =====================================================
// NEARBY SERVICES
// =====================================================

let nearbyLatitude = null;
let nearbyLongitude = null;


// =====================================================
// GET CURRENT LOCATION
// =====================================================

function findNearbyServices() {

    const status =
        document.getElementById("nearbyStatus");

    if (!navigator.geolocation) {

        if (status) {
            status.innerText =
                "❌ Geolocation is not supported.";
        }

        return;
    }

    if (status) {

        status.innerText =
            "📍 Getting your current location...";
    }


    navigator.geolocation.getCurrentPosition(

        function(position) {

            nearbyLatitude =
                position.coords.latitude;

            nearbyLongitude =
                position.coords.longitude;


            if (status) {

                status.innerHTML =
                    "✅ Location detected.<br><br>" +

                    "Latitude: " +
                    nearbyLatitude +

                    "<br>" +

                    "Longitude: " +
                    nearbyLongitude +

                    "<br><br>" +

                    "Select a service below.";
            }

        },

        function(error) {

            if (status) {

                if (error.code === 1) {

                    status.innerText =
                        "❌ Please allow location permission.";

                } else if (error.code === 2) {

                    status.innerText =
                        "❌ Location unavailable.";

                } else if (error.code === 3) {

                    status.innerText =
                        "❌ Location request timed out.";

                } else {

                    status.innerText =
                        "❌ Unable to get location.";
                }
            }

        },

        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
    );
}


// =====================================================
// FIND NEARBY SERVICE
// =====================================================

function findNearby(service) {

    const result =
        document.getElementById("nearbyResults");


    // Check location

    if (
        nearbyLatitude === null ||
        nearbyLongitude === null
    ) {

        alert(
            "First click 'Find Nearby Services'."
        );

        return;
    }


    // Google Maps nearby search

    const mapsUrl =
        "https://www.google.com/maps/search/" +

        encodeURIComponent(service) +

        "/@" +

        nearbyLatitude +

        "," +

        nearbyLongitude +

        ",14z";


    // Display result

    if (result) {

        result.innerHTML =

            "<h3>📍 Nearby " +
            service +
            "</h3>" +

            "<p>" +

            "Searching near your current location." +

            "</p>" +

            "<button onclick=\"window.open('" +

            mapsUrl +

            "','_blank')\">" +

            "🗺️ Open Nearby " +

            service +

            " in Google Maps" +

            "</button>";
    }


    // Open Google Maps

    window.open(
        mapsUrl,
        "_blank"
    );
}
// =====================================================
// EMERGENCY RECORDING VARIABLES
// =====================================================

let audioRecorder = null;
let audioChunks = [];

let videoRecorder = null;
let videoChunks = [];

let cameraStream = null;

let recordingStartTime = null;

let audioRecordingDuration = 0;
let videoRecordingDuration = 0;
// =====================================================
// START AUDIO RECORDING
// =====================================================

async function startAudioRecording() {

    try {

        const stream =
            await navigator.mediaDevices.getUserMedia({
                audio: true
            });


        audioChunks = [];

        audioRecorder =
            new MediaRecorder(stream);


        recordingStartTime =
            new Date();


        audioRecorder.ondataavailable =
            function(event) {

                if (event.data.size > 0) {

                    audioChunks.push(
                        event.data
                    );
                }
            };


        audioRecorder.onstop = function() {

    // =================================================
    // CALCULATE AUDIO DURATION
    // =================================================

    const recordingEndTime = new Date();

audioRecordingDuration =
    Math.round(
        (recordingEndTime - recordingStartTime) / 1000
    );

console.log(
    "🎵 Audio duration:",
    audioRecordingDuration,
    "seconds"
);


    // =================================================
    // CREATE AUDIO BLOB
    // =================================================

    const audioBlob =
        new Blob(
            audioChunks,
            {
                type: "audio/webm"
            }
        );


    // =================================================
    // TIMESTAMP
    // =================================================

    const timestamp =
        recordingStartTime.toISOString();


    // =================================================
    // UPDATE STATUS
    // =================================================

    updateAudioStatus(
        "✅ Audio recording completed<br>" +
        "Duration: " +
        formatMediaTime(
            audioRecordingDuration
        ) +
        "<br>" +
        "Timestamp: " +
        timestamp
    );


    // =================================================
    // UPLOAD
    // =================================================

    uploadEvidence(
        audioBlob,
        "audio",
        timestamp,
        incidentId,
        audioRecordingDuration
    );


    // =================================================
    // STOP MICROPHONE
    // =================================================

    stream
        .getTracks()
        .forEach(
            track => track.stop()
        );

};


        audioRecorder.start();


        updateAudioStatus(
            "🔴 Audio recording is ACTIVE"
        );

    }

    catch (error) {

        console.error(
            "Audio recording error:",
            error
        );


        updateAudioStatus(
            "❌ Microphone permission denied."
        );
    }
}
// =====================================================
// STOP AUDIO RECORDING
// =====================================================

function stopAudioRecording() {

    if (
        audioRecorder &&
        audioRecorder.state === "recording"
    ) {

        audioRecorder.stop();

    } else {

        updateAudioStatus(
            "⚠️ Audio recording is not active."
        );
    }
}
function updateAudioStatus(message) {

    const element =
        document.getElementById(
            "audioStatus"
        );


    if (element) {

        element.innerHTML =
            message;
    }
}
// =====================================================
// START VIDEO RECORDING
// =====================================================

async function startVideoRecording(camera) {

    try {

        // Stop previous camera

        if (cameraStream) {

            cameraStream
                .getTracks()
                .forEach(
                    track => track.stop()
                );
        }


        cameraStream =
            await navigator.mediaDevices
                .getUserMedia({

                    video: {
                        facingMode: camera
                    },

                    audio: true
                });


        const video =
            document.getElementById(
                "cameraPreview"
            );


        video.srcObject =
            cameraStream;


        videoChunks = [];


        videoRecorder =
            new MediaRecorder(
                cameraStream
            );


        recordingStartTime =
            new Date();


        videoRecorder.ondataavailable =
            function(event) {

                if (event.data.size > 0) {
                    videoChunks.push(
                        event.data
                    );
                }
            };


       videoRecorder.onstop = function() {

    // =================================================
    // CALCULATE VIDEO DURATION
    // =================================================

    const recordingEndTime = new Date();

videoRecordingDuration =
    Math.round(
        (recordingEndTime - recordingStartTime) / 1000
    );

console.log(
    "🎥 Video duration:",
    videoRecordingDuration,
    "seconds"
);


    // =================================================
    // CREATE VIDEO BLOB
    // =================================================

    const videoBlob =
        new Blob(
            videoChunks,
            {
                type: "video/webm"
            }
        );


    // =================================================
    // TIMESTAMP
    // =================================================

    const timestamp =
        recordingStartTime.toISOString();


    // =================================================
    // UPDATE STATUS
    // =================================================

    updateVideoStatus(
        "✅ Video recording completed<br>" +
        "Duration: " +
        formatMediaTime(
            videoRecordingDuration
        ) +
        "<br>" +
        "Timestamp: " +
        timestamp
    );


    // =================================================
    // UPLOAD
    // =================================================

    uploadEvidence(
        videoBlob,
        "video",
        timestamp,
        incidentId,
        videoRecordingDuration
    );

};

        videoRecorder.start();


        updateVideoStatus(
            "🔴 Video recording is ACTIVE"
        );

    }

    catch (error) {

        console.error(
            "Camera error:",
            error
        );


        updateVideoStatus(
            "❌ Camera/microphone permission denied."
        );
    }
}
// =====================================================
// STOP VIDEO RECORDING
// =====================================================

function stopVideoRecording() {

    if (
        videoRecorder &&
        videoRecorder.state === "recording"
    ) {

        videoRecorder.stop();


        if (cameraStream) {

            cameraStream
                .getTracks()
                .forEach(
                    track => track.stop()
                );
        }

    } else {

        updateVideoStatus(
            "⚠️ Video recording is not active."
        );
    }
}
function updateVideoStatus(message) {

    const element =
        document.getElementById(
            "videoStatus"
        );


    if (element) {

        element.innerHTML =
            message;
    }
}
// =====================================================
// UPLOAD AUDIO / VIDEO EVIDENCE
// =====================================================

async function uploadEvidence(blob, fileType, recordedAt,incidentId,duration) {

    try {

        // Get logged-in user ID
        const userId =
            localStorage.getItem("userId");

        // Get current incident ID
        

        // Get JWT token
        const token =
            localStorage.getItem("token");


        // =================================================
        // CHECK LOGIN + INCIDENT
        // =================================================

        if (!userId || !token || !incidentId) {

            console.error(
                "Missing userId, token or incidentId"
            );

            const status =
                document.getElementById(
                    "evidenceStatus"
                );

            if (status) {
                status.innerText =
                    "❌ Emergency incident not found.";
            }

            return;
        }


        console.log(
            "Uploading evidence for Incident:",
            incidentId
        );


        // =================================================
        // CREATE FORM DATA
        // =================================================

        const formData =
            new FormData();


        const fileName =
            fileType +
            "_" +
            Date.now() +
            ".webm";


        const file =
            new File(
                [blob],
                fileName,
                {
                    type:
                        blob.type ||
                        "video/webm"
                }
            );


        formData.append(
            "file",
            file
        );


        formData.append(
            "userId",
            userId
        );


        // ⭐ IMPORTANT
        formData.append(
            "incidentId",
            incidentId
        );


        formData.append(
            "fileType",
            fileType
        );


        formData.append(
            "recordedAt",
            recordedAt
        );
    const safeDuration =
    Number.isFinite(Number(duration))
        ? Math.round(Number(duration))
        : 0;

formData.append(
    "duration",
    safeDuration
);
console.log(
    "📦 Upload duration:",
    safeDuration,
    "seconds"
);


        // =================================================
        // SEND TO SPRING BOOT
        // =================================================

        const response =
            await fetch(
                `${BASE_URL}/api/evidence/upload`,
                {
                    method: "POST",

                    headers: {

                        "Authorization":
                            "Bearer " + token

                    },

                    body:
                        formData
                }
            );


        // =================================================
        // READ RESPONSE
        // =================================================

        const result =
            await response.text();


        console.log(
            "Upload response:",
            response.status,
            result
        );


        if (!response.ok) {

            throw new Error(
                "HTTP " +
                response.status +
                ": " +
                result
            );
        }


        // =================================================
        // SUCCESS
        // =================================================

        const status =
            document.getElementById(
                "evidenceStatus"
            );


        if (status) {

            status.innerHTML =
                "✅ " +
                fileType.toUpperCase() +
                " evidence uploaded successfully.<br><br>" +

                "🚨 Incident ID: " +
                incidentId +
                "<br><br>" +

                "🕒 Recorded: " +
                recordedAt;
        }


        console.log(
            "Evidence uploaded successfully.",
            "Incident ID:",
            incidentId
        );


    } catch (error) {

        console.error(
            "Evidence upload error:",
            error
        );


        const status =
            document.getElementById(
                "evidenceStatus"
            );


        if (status) {

            status.innerHTML =
                "❌ Evidence upload failed.<br><br>" +
                error.message;
        }
    }
}
// =====================================================
// FORMAT MEDIA TIME
// =====================================================

function formatMediaTime(seconds) {

    if (!isFinite(seconds) || seconds < 0) {
        return "0:00";
    }

    const minutes =
        Math.floor(seconds / 60);

    const remainingSeconds =
        Math.floor(seconds % 60);

    return (
        minutes +
        ":" +
        String(remainingSeconds).padStart(2, "0")
    );
}
// =====================================================
// LOAD EMERGENCY EVIDENCE
// =====================================================

async function loadEvidence(searchDate = "") {

    const userId =
        localStorage.getItem("userId");

    const token =
        localStorage.getItem("token");

    const container =
        document.getElementById("evidenceList");

    const countElement =
        document.getElementById("recordingCount");


    if (!userId || !token) {

        container.innerHTML = `
            <div class="evidence-empty">
                ❌ Please login again.
            </div>
        `;

        return;
    }


    try {

        // =================================================
        // GET EVIDENCE LIST
        // =================================================

        const response =
            await fetch(
                `${BASE_URL}/api/evidence/user/${userId}`,
                {
                    method: "GET",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to load evidence: "
                + response.status
            );
        }


        let evidence =
            await response.json();
            // =====================================================
// FILTER BY SELECTED DATE
// =====================================================

if (searchDate) {

    evidence = evidence.filter(function(item) {

        if (!item.recordedAt) {
            return false;
        }

        const recordedDate =
            new Date(item.recordedAt);

        if (isNaN(recordedDate.getTime())) {
            return false;
        }

        const year =
            recordedDate.getFullYear();

        const month =
            String(
                recordedDate.getMonth() + 1
            ).padStart(2, "0");

        const day =
            String(
                recordedDate.getDate()
            ).padStart(2, "0");

        const evidenceDate =
            year +
            "-" +
            month +
            "-" +
            day;

        return evidenceDate === searchDate;

    });

}


        // =================================================
        // EMPTY
        // =================================================

        if (!evidence || evidence.length === 0) {

            if (searchDate) {

    container.innerHTML = `
        <div class="evidence-empty">
            📅 No emergency recordings found for
            <strong>${searchDate}</strong>.
        </div>
    `;

} else {

    container.innerHTML = `
        <div class="evidence-empty">
            📁 No emergency recordings found.
        </div>
    `;

}

            if (countElement) {
                countElement.innerText =
                    "0 Recordings";
            }

            return;
        }


        // =================================================
        // NEWEST FIRST
        // =================================================

        evidence.sort(function(a, b) {

            return new Date(
                b.recordedAt
            ) - new Date(
                a.recordedAt
            );

        });


        // =================================================
        // COUNT
        // =================================================

        if (countElement) {

            countElement.innerText =
                evidence.length +
                (
                    evidence.length === 1
                        ? " Recording"
                        : " Recordings"
                );
        }


        container.innerHTML = "";


        // =================================================
        // LOAD EACH RECORDING
        // =================================================

        for (const item of evidence) {

            const card =
                document.createElement("div");

            card.className =
                "evidence-card";


            // =================================================
            // TYPE
            // =================================================

            const isAudio =
                item.fileType &&
                item.fileType.toLowerCase() === "audio";


            const icon =
                isAudio
                    ? "🎙️"
                    : "📹";


            const typeName =
                isAudio
                    ? "Audio Recording"
                    : "Video Recording";


            // =================================================
            // DATE
            // =================================================

            const date =
                new Date(item.recordedAt);


            const dateText =
                date.toLocaleDateString(
                    "en-IN",
                    {
                        day: "2-digit",
                        month: "short",
                        year: "numeric"
                    }
                );


            const timeText =
                date.toLocaleTimeString(
                    "en-IN",
                    {
                        hour: "2-digit",
                        minute: "2-digit",
                        second: "2-digit"
                    }
                );


            // =================================================
            // FILE URL
            // =================================================

            const fileURL =
                `${BASE_URL}/api/evidence/file/${item.id}`;


            // =================================================
            // FETCH FILE WITH JWT
            // =================================================

            const fileResponse =
                await fetch(
                    fileURL,
                    {
                        method: "GET",

                        headers: {
                            "Authorization":
                                "Bearer " + token
                        }
                    }
                );


            if (!fileResponse.ok) {

                card.innerHTML = `

                    <div class="evidence-card-header">

                        <div class="evidence-type">

                            <div class="evidence-icon">
                                ❌
                            </div>

                            <div>
                                <h3>
                                    Unable to load recording
                                </h3>
                            </div>

                        </div>

                    </div>

                    <div class="evidence-details">

                        File access failed:
                        ${fileResponse.status}

                    </div>

                `;

                container.appendChild(card);

                continue;
            }


            // =================================================
            // BLOB
            // =================================================

            const blob =
                await fileResponse.blob();


            const blobURL =
                URL.createObjectURL(blob);


            // =================================================
            // MEDIA PLAYER
            // =================================================

            // =====================================================
// MEDIA PLAYER
// =====================================================

let mediaHTML = "";

if (isAudio) {

   mediaHTML = `

    <div class="evidence-media-wrapper">

        <audio
            class="evidence-audio"
            controls
            preload="metadata">

            <source
                src="${blobURL}"
                type="${blob.type || "audio/webm"}">

        </audio>


    </div>

`;

} else {

    mediaHTML = `

    <div class="evidence-media-wrapper">

        <video
            class="evidence-video"
            controls
            preload="metadata"
            playsinline>

            <source
                src="${blobURL}"
                type="${blob.type || "video/webm"}">

        </video>

    </div>

`;
}


            // =================================================
            // CARD
            // =================================================

            card.innerHTML = `

                <div class="evidence-card-header">

                    <div class="evidence-type">

                        <div class="evidence-icon">
                            ${icon}
                        </div>

                        <div>

                            <h3>
                                ${typeName}
                            </h3>

                        </div>

                    </div>


                    <div class="evidence-date">

                        ${dateText}
                        <br>
                        ${timeText}

                    </div>

                </div>


                <div class="evidence-details">

                    <span>
                        🚨 Emergency Recording
                    </span>

                    <span>
                        📄 ${item.fileName}
                    </span>

                </div>


                <div class="evidence-player">

                    ${mediaHTML}

                </div>


                <details class="evidence-hash">

                    <summary>
                        🔐 View Evidence Hash
                    </summary>

                    <p>
                        ${item.fileHash}
                    </p>

                </details>


                <div class="evidence-actions">

                    <button
                        class="delete-evidence-btn"
                        onclick="deleteEvidence(${item.id})">

                        🗑️ Delete

                    </button>

                </div>

            `;
            // =====================================================
// MEDIA DURATION + CURRENT TIME
// =====================================================

const mediaElement =
    card.querySelector(
        isAudio
            ? ".evidence-audio"
            : ".evidence-video"
    );

const mediaTime =
    card.querySelector(".media-time");

if (mediaElement && mediaTime) {

    const savedDuration =
        Number(item.durationSeconds);

    mediaElement.addEventListener(
        "timeupdate",
        function() {

            mediaTime.textContent =
                formatMediaTime(
                    mediaElement.currentTime
                ) +
                " / " +
                formatMediaTime(
                    savedDuration
                );

        }
    );
}


            container.appendChild(card);

        }


    } catch (error) {

        console.error(
            "Evidence loading error:",
            error
        );


        container.innerHTML = `

            <div class="evidence-empty">

                ❌ Unable to load emergency recordings.

                <br><br>

                ${error.message}

            </div>

        `;
    }
}
async function deleteEvidence(id) {

    if (!confirm("Are you sure you want to delete this recording?")) {
        return;
    }

    const token =
        localStorage.getItem("token");

    try {

        const response =
            await fetch(
                `${BASE_URL}/api/evidence/delete/${id}`,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );

        const result =
            await response.text();

        if (!response.ok) {

            alert(
                "Delete failed: " +
                response.status
            );

            return;
        }

        alert(result);

        // Reload evidence list
        loadEvidence();

    } catch (error) {

        console.error(
            "Delete error:",
            error
        );

        alert(
            "Unable to delete recording."
        );
    }
}
// =====================================================
// FAKE CALL GENERATOR
// =====================================================

let fakeCallTimer = null;

let fakeCallAudioContext = null;

let fakeCallOscillator = null;

let fakeCallRinging = false;
// =====================================================
// SCHEDULE FAKE CALL
// =====================================================

function scheduleFakeCall() {

    const callerName =
        document.getElementById(
            "fakeCallerName"
        ).value.trim();


    const delay =
        parseInt(
            document.getElementById(
                "fakeCallDelay"
            ).value
        );


    const status =
        document.getElementById(
            "fakeCallStatus"
        );


    if (!callerName) {

        alert(
            "Please enter a caller name."
        );

        return;
    }


    // Cancel previous timer
    if (fakeCallTimer) {

        clearTimeout(
            fakeCallTimer
        );
    }


    fakeCallTimer =
        setTimeout(
            function() {

                showFakeCall(
                    callerName
                );

            },
            delay * 1000
        );


    const callTime =
        new Date(
            Date.now() +
            delay * 1000
        );


    status.innerHTML =

        "📅 Fake call scheduled.<br><br>" +

        "👤 Caller: " +
        callerName +
        "<br>" +

        "⏰ Call time: " +
        callTime.toLocaleTimeString();

}
// =====================================================
// CALL NOW
// =====================================================

function callNow() {

    const callerName =
        document.getElementById(
            "fakeCallerName"
        ).value.trim();


    if (!callerName) {

        alert(
            "Please enter a caller name."
        );

        return;
    }


    showFakeCall(
        callerName
    );
}
// =====================================================
// SHOW FAKE CALL
// =====================================================

function showFakeCall(callerName) {

    const screen =
        document.getElementById("fakeCallScreen");

    const name =
        document.getElementById("incomingCallerName");

    if (!screen || !name) {
        console.error("Fake call elements not found.");
        return;
    }

    name.innerText = callerName;

    screen.style.display = "block";

    // Start ringtone
    startFakeRingtone();
}
// =====================================================
// FAKE RINGTONE
// =====================================================

function startFakeRingtone() {

    // Prevent duplicate ringtone
    if (fakeCallRinging) {
        return;
    }

    fakeCallRinging = true;

    try {

        const AudioContext =
            window.AudioContext ||
            window.webkitAudioContext;

        if (!AudioContext) {

            console.error(
                "Web Audio API is not supported."
            );

            return;
        }

        fakeCallAudioContext =
            new AudioContext();


        // Android WebView can create the
        // AudioContext in suspended state.
        if (
            fakeCallAudioContext.state ===
            "suspended"
        ) {

            fakeCallAudioContext.resume();
        }


        function beep() {

            if (
                !fakeCallRinging ||
                !fakeCallAudioContext
            ) {
                return;
            }


            const oscillator =
                fakeCallAudioContext
                    .createOscillator();

            const gain =
                fakeCallAudioContext
                    .createGain();


            oscillator.type = "sine";

            oscillator.frequency.setValueAtTime(
                700,
                fakeCallAudioContext.currentTime
            );


            gain.gain.setValueAtTime(
                0.35,
                fakeCallAudioContext.currentTime
            );


            oscillator.connect(gain);

            gain.connect(
                fakeCallAudioContext.destination
            );


            oscillator.start();


            oscillator.stop(
                fakeCallAudioContext.currentTime +
                0.5
            );


            oscillator.onended = function () {

                if (
                    fakeCallRinging
                ) {

                    setTimeout(
                        beep,
                        700
                    );
                }
            };
        }


        beep();

        console.log(
            "Fake ringtone started."
        );

    } catch (error) {

        console.error(
            "Fake ringtone error:",
            error
        );
    }
}


// =====================================================
// ANSWER FAKE CALL
// =====================================================


async function answerFakeCall() {
    stopFakeRingtone();

    const messageElement = document.getElementById("fakeVoiceMessage");
    const genderElement = document.getElementById("fakeVoiceGender");
    const status = document.getElementById("fakeCallStatus");

    const message = messageElement ? messageElement.value.trim() : "";
    const gender = genderElement ? genderElement.value : "female";

    if (!message) {
        alert("Please enter a voice message.");
        return;
    }

    if (status) {
        status.textContent = "Generating natural voice...";
    }

    let audioUrl = null;

    try {
        const token = localStorage.getItem("token");

        if (!token) {
            throw new Error("Your session has expired. Please log in again.");
        }

        const response = await fetch(
            `${BASE_URL}/api/fake-call/voice`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    text: message,
                    gender: gender
                })
            }
        );

        const contentType =
            (response.headers.get("content-type") || "").toLowerCase();

        console.log("Fake voice HTTP status:", response.status);
        console.log("Fake voice content type:", contentType);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(
                `Voice API returned HTTP ${response.status}: ` +
                errorText.slice(0, 300)
            );
        }

        if (!contentType.startsWith("audio/")) {
            const responseText = await response.text();

            console.error("Expected audio but received:", responseText.slice(0, 500));

            throw new Error(
                "The server did not return audio. Received: " +
                (contentType || "unknown content type")
            );
        }

        const audioBlob = await response.blob();

        if (!audioBlob.size) {
            throw new Error("The voice server returned an empty audio file.");
        }

        console.log("Fake voice audio size:", audioBlob.size);

        audioUrl = URL.createObjectURL(audioBlob);

        const audio = new Audio();
        audio.preload = "auto";
        audio.src = audioUrl;

        await new Promise((resolve, reject) => {
            audio.onended = resolve;
            audio.onerror = () => {
    console.error("Audio element error:", audio.error);
    console.error("Audio URL:", audioUrl);
    console.error("Audio MIME type:", audioBlob.type);
    console.error("Audio size:", audioBlob.size);

    reject(
        new Error(
            "Audio playback failed. MIME type: " +
            (audioBlob.type || "unknown") +
            ", size: " + audioBlob.size +
            " bytes. Check the console for details."
        )
    );
};

            audio.play().catch(reject);
        });

        if (status) {
            status.textContent = "Voice message played successfully.";
        }

    } catch (error) {
        console.error("Fake-call voice error:", error);

        if (status) {
            status.textContent = "Voice playback failed: " + error.message;
        }

        alert("Fake-call voice error:\n\n" + error.message);

    } finally {
        if (audioUrl) {
            URL.revokeObjectURL(audioUrl);
        }
    }
}

// =====================================================
// REJECT FAKE CALL
// =====================================================

function rejectFakeCall() {

    stopFakeRingtone();


    const screen =
        document.getElementById(
            "fakeCallScreen"
        );


    screen.style.display =
        "none";


    const status =
        document.getElementById(
            "fakeCallStatus"
        );


    status.innerHTML =
        "📵 Fake call ended.";
}
// =====================================================
// STOP RINGTONE
// =====================================================

function stopFakeRingtone() {

    fakeCallRinging =
        false;


    if (fakeCallOscillator) {

        try {

            fakeCallOscillator.stop();

        } catch (e) {}

        fakeCallOscillator =
            null;
    }


    if (fakeCallAudioContext) {

        fakeCallAudioContext
            .close();

        fakeCallAudioContext =
            null;
    }
}
// =====================================================
// CANCEL FAKE CALL
// =====================================================

function cancelFakeCall() {

    if (fakeCallTimer) {

        clearTimeout(
            fakeCallTimer
        );

        fakeCallTimer =
            null;
    }


    stopFakeRingtone();


    const screen =
        document.getElementById(
            "fakeCallScreen"
        );


    if (screen) {

        screen.style.display =
            "none";
    }


    const status =
        document.getElementById(
            "fakeCallStatus"
        );


    if (status) {

        status.innerText =
            "⛔ Fake call cancelled.";
    }
}

// =====================================================
// SHE SAFE NORMAL CHATBOT
// =====================================================
// =========================================
// TOGGLE FLOATING CHATBOT
// =========================================

function toggleChatbot() {

    const chatbot =
        document.getElementById("chatbotWindow");

    chatbot.classList.toggle("active");

}

function sendAIMessage() {

    const input =
        document.getElementById("aiMessage");

    const message =
        input.value.trim();

    if (!message) {
        return;
    }

    // Show user message
    addUserMessage(message);

    // Clear input
    input.value = "";

    // Get normal chatbot response
    const reply =
        getChatbotResponse(message);

    // Small delay to look like chatbot is responding
    setTimeout(function () {

        addAIMessage(reply);

    }, 400);
}


// =====================================================
// QUICK QUESTIONS
// =====================================================




// =====================================================
// CHATBOT LOGIC
// =====================================================

function getChatbotResponse(message) {

    const text =
        message.toLowerCase();


    // =================================================
    // GREETING
    // =================================================

    if (
        text.includes("hi") ||
        text.includes("hello") ||
        text.includes("hey")
    ) {

        return `
            👋 Hello! I am the SheSafe Assistant.

            <br><br>

            I can help you with:

            <br><br>

            🛡️ Safety guidance<br>
            🚨 Emergency instructions<br>
            🩹 First aid<br>
            🚔 Police assistance<br>
            🏥 Hospital assistance<br>
            ⚖️ Legal information<br>
            📍 Location sharing<br>
            👥 Emergency contacts<br>
            🆘 SOS assistance
        `;
    }


    // =================================================
    // SAFETY
    // =================================================

    if (
        text.includes("safety") ||
        text.includes("safe") ||
        text.includes("protect")
    ) {

        return `
            🛡️ <strong>Safety Guidance</strong>

            <br><br>

            • Stay in well-lit and populated areas.<br>
            • Keep your phone charged.<br>
            • Keep trusted emergency contacts saved.<br>
            • Avoid isolated places when possible.<br>
            • Share your location with trusted people
              when necessary.<br>
            • If you feel unsafe, move to a public place.<br>
            • Use the SOS feature if you are in immediate danger.
        `;
    }


    // =================================================
    // EMERGENCY
    // =================================================

    if (
        text.includes("emergency") ||
        text.includes("danger") ||
        text.includes("threat")
    ) {

        return `
            🚨 <strong>Emergency Instructions</strong>

            <br><br>

            If you are in immediate danger:

            <br><br>

            1️⃣ Move to a safe/public place.<br>
            2️⃣ Activate the SOS button.<br>
            3️⃣ Contact your emergency contacts.<br>
            4️⃣ Call the appropriate emergency service.<br>
            5️⃣ Share your location with trusted contacts.<br>
            6️⃣ Avoid confronting the person threatening you.
        `;
    }


    // =================================================
    // SOS
    // =================================================

    if (
        text.includes("sos")
    ) {

        return `
            🆘 <strong>SOS Assistance</strong>

            <br><br>

            The SOS feature can be used when you
            are in immediate danger.

            <br><br>

            Activate SOS to start your emergency
            response process and notify your trusted
            emergency contacts according to your
            SheSafe settings.

            <br><br>

            🚨 If there is immediate danger,
            contact emergency services as well.
        `;
    }


    // =================================================
    // FIRST AID
    // =================================================

    if (
        text.includes("first aid") ||
        text.includes("injury") ||
        text.includes("bleeding") ||
        text.includes("hurt")
    ) {

        return `
            🩹 <strong>Basic First Aid</strong>

            <br><br>

            For a minor injury:

            <br><br>

            • Move to a safe location.<br>
            • Check the injury carefully.<br>
            • For bleeding, apply gentle direct pressure
              with clean material.<br>
            • Seek medical assistance for serious injuries.<br>
            • Do not ignore severe pain, heavy bleeding,
              breathing problems or loss of consciousness.
        `;
    }


    // =================================================
    // POLICE
    // =================================================

    if (
        text.includes("police") ||
        text.includes("station")
    ) {

        return `
            🚔 <strong>Police Assistance</strong>

            <br><br>

            You can use the <strong>Nearby Services</strong>
            feature in SheSafe to find nearby police stations.

            <br><br>

            If you are in immediate danger,
            contact the police/emergency service
            appropriate to your location.
        `;
    }


    // =================================================
    // HOSPITAL
    // =================================================

    if (
        text.includes("hospital") ||
        text.includes("doctor") ||
        text.includes("medical")
    ) {

        return `
            🏥 <strong>Hospital Assistance</strong>

            <br><br>

            Use the <strong>Nearby Services</strong>
            feature to find hospitals near your current
            location.

            <br><br>

            For a serious medical emergency,
            seek emergency medical assistance immediately.
        `;
    }


    // =================================================
    // LEGAL
    // =================================================

    if (
        text.includes("legal") ||
        text.includes("law") ||
        text.includes("lawyer")
    ) {

        return `
            ⚖️ <strong>Legal Assistance</strong>

            <br><br>

            If you need legal help:

            <br><br>

            • Keep relevant documents and evidence safe.<br>
            • Record important incident details.<br>
            • Contact a qualified lawyer or legal-aid service.<br>
            • For immediate danger, seek emergency assistance first.
        `;
    }


    // =================================================
    // LOCATION
    // =================================================

    if (
        text.includes("location") ||
        text.includes("gps") ||
        text.includes("where am i")
    ) {

        return `
            📍 <strong>Location Assistance</strong>

            <br><br>

            You can use the <strong>Live Location</strong>
            feature to obtain your current GPS location.

            <br><br>

            You can also share your location with
            trusted emergency contacts.
        `;
    }


    // =================================================
    // EMERGENCY CONTACTS
    // =================================================

    if (
        text.includes("contact") ||
        text.includes("family") ||
        text.includes("friend")
    ) {

        return `
            👥 <strong>Emergency Contacts</strong>

            <br><br>

            You can add trusted people such as:

            <br><br>

            👩 Mother<br>
            👨 Father<br>
            👭 Friends<br>
            👨‍👩‍👧 Family members

            <br><br>

            You can also assign priorities to your
            emergency contacts.
        `;
    }


    // =================================================
    // RECORDING
    // =================================================

    if (
        text.includes("record") ||
        text.includes("audio") ||
        text.includes("video")
    ) {

        return `
            🎥 <strong>Emergency Recording</strong>

            <br><br>

            SheSafe provides emergency audio and video
            recording features.

            <br><br>

            Recordings can be used as emergency evidence
            according to your application's configuration.
        `;
    }


    // =================================================
    // FAKE CALL
    // =================================================

    if (
        text.includes("fake call") ||
        text.includes("fake phone")
    ) {

        return `
            📞 <strong>Fake Call</strong>

            <br><br>

            The Fake Call feature can help you leave
            an uncomfortable situation.

            <br><br>

            You can configure a caller name,
            ringtone and call timing.
        `;
    }


    // =================================================
    // THANK YOU
    // =================================================

    if (
        text.includes("thank") ||
        text.includes("thanks")
    ) {

        return `
            😊 You're welcome!

            <br><br>

            Stay safe and take care. 💜
        `;
    }


    // =================================================
    // DEFAULT RESPONSE
    // =================================================

    return `
        🤖 <strong>SheSafe Assistant</strong>

        <br><br>

        I can help you with:

        <br><br>

        🛡️ Safety guidance<br>
        🚨 Emergency instructions<br>
        🩹 First aid<br>
        🚔 Police assistance<br>
        🏥 Hospitals<br>
        ⚖️ Legal information<br>
        📍 GPS and location sharing<br>
        👥 Emergency contacts<br>
        🎥 Audio and video recording<br>
        📞 Fake calls

        <br><br>

        Try asking something like:

        <br><br>

        <em>
        "What should I do in an emergency?"
        </em>
    `;
}


// =====================================================
// ADD USER MESSAGE
// =====================================================





// =====================================================
// QUICK AI QUESTIONS
// =====================================================

function askAI(message) {

    const input =
        document.getElementById(
            "aiMessage"
        );

    input.value =
        message;

    sendAIMessage();
}
// =====================================================
// ADD USER MESSAGE
// =====================================================

function addUserMessage(message) {

    const chat =
        document.getElementById(
            "chatMessages"
        );


    const div =
        document.createElement("div");


    div.style.cssText =
        `
        background:#e8f0ff;
        padding:12px;
        border-radius:10px;
        margin-bottom:10px;
        text-align:right;
        `;


    div.innerHTML =
        `
        👤 <strong>You:</strong>
        <br>
        ${escapeAIHTML(message)}
        `;


    chat.appendChild(div);

    chat.scrollTop =
        chat.scrollHeight;
}


// =====================================================
// ADD AI MESSAGE
// =====================================================

function addAIMessage(message) {

    const chat =
        document.getElementById(
            "chatMessages"
        );


    const div =
        document.createElement("div");


    div.style.cssText =
        `
        background:#eee7ff;
        padding:12px;
        border-radius:10px;
        margin-bottom:10px;
        `;


    div.innerHTML =
        `
        🤖 <strong>SheSafe Assistant:</strong>
        <br>
        ${message}
        `;


    chat.appendChild(div);

    chat.scrollTop =
        chat.scrollHeight;
}


// =====================================================
// ENTER KEY
// =====================================================

function handleAIEnter(event) {

    if (event.key === "Enter") {

        sendAIMessage();

    }
}


// =====================================================
// HTML ESCAPE
// =====================================================

function escapeAIHTML(text) {

    const div =
        document.createElement("div");

    div.textContent =
        text;

    return div.innerHTML;
}
// =====================================================
// FIND NEARBY POLICE
// =====================================================

function findNearbyPolice() {

    if (!navigator.geolocation) {

        addAIMessage(
            "❌ Your browser does not support location services."
        );

        return;
    }


    addAIMessage(
        "📍 Getting your current location..."
    );


    navigator.geolocation.getCurrentPosition(

        function(position) {

            const lat =
                position.coords.latitude;

            const lon =
                position.coords.longitude;


            const mapURL =
                "https://www.google.com/maps/search/police+station/"
                + "@"
                + lat
                + ","
                + lon
                + ",14z";


            addAIMessage(`

                🚔 <strong>Nearby Police Stations</strong><br><br>

                Your location has been detected.<br><br>

                <a
                    href="${mapURL}"
                    target="_blank">

                    🗺️ Open Nearby Police Stations

                </a>

            `);

        },

        function() {

            addAIMessage(
                "❌ Unable to access your location. Please allow location permission."
            );

        }

    );
}
// =====================================================
// FIND NEARBY HOSPITALS
// =====================================================

function findNearbyHospitals() {

    if (!navigator.geolocation) {

        addAIMessage(
            "❌ Your browser does not support location services."
        );

        return;
    }


    addAIMessage(
        "📍 Getting your current location..."
    );


    navigator.geolocation.getCurrentPosition(

        function(position) {

            const lat =
                position.coords.latitude;

            const lon =
                position.coords.longitude;


            const mapURL =
                "https://www.google.com/maps/search/hospital/"
                + "@"
                + lat
                + ","
                + lon
                + ",14z";


            addAIMessage(`

                🏥 <strong>Nearby Hospitals</strong><br><br>

                Your location has been detected.<br><br>

                <a
                    href="${mapURL}"
                    target="_blank">

                    🗺️ Open Nearby Hospitals

                </a>

            `);

        },

        function() {

            addAIMessage(
                "❌ Unable to access your location. Please allow location permission."
            );

        }

    );
}

// =====================================================
// INCIDENT TIMELINE SYSTEM
// =====================================================


// =====================================================
// ADD INCIDENT EVENT
// =====================================================

function addIncidentEvent(event) {

    let timeline =
        JSON.parse(
            localStorage.getItem(
                "incidentTimeline"
            ) || "[]"
        );


    // Automatically generate timestamp

    if (!event.timestamp) {

        event.timestamp =
            new Date().toISOString();

    }


    timeline.push(event);


    localStorage.setItem(
        "incidentTimeline",
        JSON.stringify(timeline)
    );


    console.log(
        "Incident event recorded:",
        event
    );
}


// =====================================================
// FORMAT TIMESTAMP
// =====================================================

function formatIncidentTime(timestamp) {

    if (!timestamp) {
        return "--";
    }


    const date =
        new Date(timestamp);


    return date.toLocaleString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",

            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        }
    );
}


// =====================================================
// LOAD INCIDENT TIMELINE
// =====================================================

// =====================================================
// DATABASE INCIDENT TIMELINE
// =====================================================

let databaseTimelineEvents = [];


// =====================================================
// LOAD TIMELINE FROM DATABASE
// =====================================================
async function loadDatabaseIncidentTimeline() {

    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
        console.error("User not logged in");
        return;
    }

    const timelineContainer =
        document.getElementById("incidentTimeline");

    if (!timelineContainer) {
        console.error("incidentTimeline element not found");
        return;
    }

    timelineContainer.innerHTML =
        "<p>Loading incident history...</p>";

    try {

        // ==========================================
        // GET ALL INCIDENTS FOR THIS USER
        // ==========================================

        const incidentsResponse =
            await fetch(
                `${BASE_URL}/api/incidents/user/${userId}`,
                {
                    method: "GET",
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );

        if (!incidentsResponse.ok) {

            throw new Error(
                "Unable to load incident history"
            );
        }

        const incidents =
            await incidentsResponse.json();


        // ==========================================
        // NO INCIDENTS
        // ==========================================

        if (!incidents || incidents.length === 0) {

            timelineContainer.innerHTML =
                `
                <div class="no-incidents">
                    📋 No incident activity recorded.
                </div>
                `;

            return;
        }


        // ==========================================
        // CLEAR CONTAINER
        // ==========================================

        timelineContainer.innerHTML = "";


        // ==========================================
        // LOAD EVERY INCIDENT
        // ==========================================

        for (const incident of incidents) {

            try {

                const response =
                    await fetch(
                        `${BASE_URL}/api/incidents/${incident.id}/timeline`,
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
                        "Unable to load incident:",
                        incident.id
                    );
                    continue;
                }

                const timeline =
                    await response.json();

                displayIncidentTimeline(
                    timeline,
                    timelineContainer
                );

            } catch (error) {

                console.error(
                    "Error loading incident " +
                    incident.id,
                    error
                );
            }
        }

    } catch (error) {

        console.error(
            "Timeline error:",
            error
        );

        timelineContainer.innerHTML =
            `
            <div class="no-incidents">
                ❌ Unable to load incident history.
            </div>
            `;
    }
}
// =====================================================
// DISPLAY DATABASE TIMELINE
// =====================================================

function displayIncidentTimeline(
    timeline,
    container
) {

    const incidentBox =
        document.createElement("div");

    incidentBox.className =
        "incident-history-card";


    // ==========================================
    // INCIDENT HEADER
    // ==========================================

    const header =
        document.createElement("div");

    header.className =
        "incident-history-header";


    const activationTime =
        timeline.sosActivationTime
            ? new Date(
                timeline.sosActivationTime
              ).toLocaleString()
            : "Unknown";


    // NO ACTIVE / RESOLVED / RESPONSE STATUS
    header.innerHTML = `
        <div>
            <h2>🚨 Incident #${timeline.incidentNumber}</h2>

            <p>
                SOS Activated:
                <strong>${activationTime}</strong>
            </p>
        </div>
    `;


    incidentBox.appendChild(header);


    // ==========================================
    // EVENTS
    // ==========================================

    const events =
        timeline.timeline || [];


    // ==========================================
    // REMOVE RESPONSE STATUS EVENTS
    // ==========================================

    const filteredEvents =
        events.filter(function(event) {

            return (
                event.type !== "EMERGENCY_RESPONSE" &&
                event.type !== "RESPONSE"
            );

        });


    if (filteredEvents.length === 0) {

        const empty =
            document.createElement("p");

        empty.innerHTML =
            "📋 No activity recorded for this incident.";

        empty.className =
            "incident-empty";

        incidentBox.appendChild(
            empty
        );

        container.appendChild(
            incidentBox
        );

        return;
    }


    // ==========================================
    // ACTIVITY
    // ==========================================

    const activity =
        document.createElement("div");

    activity.className =
        "incident-events";


    // ==========================================
    // CREATE EACH EVENT
    // ==========================================

    filteredEvents.forEach(function(event) {

        const eventBox =
            document.createElement("div");


        eventBox.className =
            "timeline-event";


        // ⭐ IMPORTANT FOR FILTERS

        eventBox.dataset.eventType =
            event.type;


        let icon = "📋";


        // ======================================
        // EVENT ICON
        // ======================================

        switch (event.type) {

            case "SOS_ACTIVATED":

                icon = "🚨";

                break;


            case "LOCATION":

                icon = "📍";

                break;


            case "CALL":

                icon = "📞";

                break;


            case "MESSAGE":

                icon = "💬";

                break;


            case "MEDIA":

                if (
                    event.description &&
                    event.description
                        .toUpperCase()
                        .includes("VIDEO")
                ) {

                    icon = "🎥";

                } else {

                    icon = "🎙️";

                }

                break;


            default:

                icon = "📋";

        }


        // ======================================
        // EVENT TIME
        // ======================================

        const eventTime =
            event.time
                ? new Date(
                    event.time
                  ).toLocaleString()
                : "Unknown";


        // ======================================
        // EVENT TYPE NAME
        // ======================================

        let eventTitle =
            String(
                event.type || "EVENT"
            ).replace(
                /_/g,
                " "
            );


        // ======================================
        // EVENT CONTENT
        // ======================================

        eventBox.innerHTML = `

            <div class="timeline-dot">
                ${icon}
            </div>


            <div class="timeline-content">

                <div class="timeline-title">

                    ${icon}

                    ${eventTitle}

                </div>


                <div class="timeline-description">

                    ${
                        event.description
                            ? event.description
                                .replace(
                                    /\n/g,
                                    "<br>"
                                )
                            : ""
                    }


                    ${
                        event.type === "LOCATION" &&
                        event.latitude != null &&
                        event.longitude != null

                        ?

                        `
                        <br><br>

                        <a
                            href="https://www.google.com/maps?q=${event.latitude},${event.longitude}"
                            target="_blank"
                            rel="noopener noreferrer"
                            class="view-map-button">

                            🗺️ View Location on Map

                        </a>
                        `

                        :

                        ""
                    }

                </div>


                <div class="timeline-time">

                    🕐 ${eventTime}

                </div>

            </div>

        `;


        activity.appendChild(
            eventBox
        );

    });


    // ==========================================
    // ADD ACTIVITY TO INCIDENT
    // ==========================================

    incidentBox.appendChild(
        activity
    );


    // ==========================================
    // ADD INCIDENT TO CONTAINER
    // ==========================================

    container.appendChild(
        incidentBox
    );

}


// =====================================================
// FILTER TIMELINE
// =====================================================

function filterIncidentTimeline(type) {

    console.log("FILTER CLICKED:", type);

    const events =
        document.querySelectorAll(
            "#incidentTimeline .timeline-event"
        );

    const buttons =
        document.querySelectorAll(
            ".timeline-filter"
        );


    // ==========================================
    // UPDATE ACTIVE BUTTON
    // ==========================================

    buttons.forEach(function(button) {

        if (
            button.dataset.filterType === type
        ) {

            button.classList.add("active");

        } else {

            button.classList.remove("active");

        }

    });


    // ==========================================
    // FILTER EVENTS
    // ==========================================

    let visibleCount = 0;

    events.forEach(function(eventElement) {

        const eventType =
            eventElement.dataset.eventType;

        console.log(
            "Event type:",
            eventType
        );


        if (
            type === "ALL" ||
            eventType === type
        ) {

            eventElement.classList.remove(
                "timeline-hidden"
            );

            visibleCount++;

        } else {

            eventElement.classList.add(
                "timeline-hidden"
            );

        }

    });


    // ==========================================
    // EMPTY FILTER MESSAGE
    // ==========================================

    let emptyMessage =
        document.getElementById(
            "filterEmptyMessage"
        );


    if (!emptyMessage) {

        emptyMessage =
            document.createElement("div");

        emptyMessage.id =
            "filterEmptyMessage";

        emptyMessage.className =
            "filter-empty-message";


        document
            .getElementById("incidentTimeline")
            .appendChild(emptyMessage);

    }


    if (visibleCount === 0) {

        emptyMessage.innerHTML =
            "📋 No " +
            getFilterName(type) +
            " recorded.";

        emptyMessage.style.display =
            "block";

    } else {

        emptyMessage.style.display =
            "none";

    }

}
function getFilterName(type) {

    switch (type) {

        case "LOCATION":
            return "GPS locations";

        case "CALL":
            return "emergency calls";

        case "MESSAGE":
            return "emergency messages";

        case "MEDIA":
            return "recordings";

        case "ALL":
            return "incident activity";

        default:
            return "activity";
    }

}
// =====================================================
// DATABASE TIMESTAMP
// =====================================================

function formatDatabaseTime(timestamp) {

    if (!timestamp) {
        return "--";
    }


    const date =
        new Date(timestamp);


    if (isNaN(date.getTime())) {
        return timestamp;
    }


    return date.toLocaleString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
            hour12: true
        }
    );
}


// =====================================================
// ESCAPE HTML
// =====================================================

function escapeIncidentHTML(text) {

    const div =
        document.createElement(
            "div"
        );


    div.textContent =
        String(text);


    return div.innerHTML;
}


// =====================================================
// CLEAR INCIDENT TIMELINE
// =====================================================

function clearIncidentTimeline() {

    const result =
        confirm(
            "Are you sure you want to clear the entire incident timeline?"
        );


    if (!result) {
        return;
    }


    localStorage.removeItem(
        "incidentTimeline"
    );


    alert(
        "Incident timeline cleared successfully."
    );


    location.reload();
}

addIncidentEvent({

    type: "AUDIO",

    icon: "🎙️",

    title: "Audio Recording Started",

    description:
        "Emergency audio recording started.",

    fileName:
        "Emergency Audio",

    status:
        "Recording"

});
addIncidentEvent({

    type: "AUDIO",

    icon: "🎙️",

    title: "Audio Recording Uploaded",

    description:
        "Emergency audio evidence uploaded successfully.",

    fileName:
        "Emergency Audio",

    status:
        "Uploaded"

});
addIncidentEvent({

    type: "VIDEO",

    icon: "🎥",

    title: "Video Recording Started",

    description:
        "Emergency video recording started.",

    fileName:
        "Emergency Video",

    status:
        "Recording"

});
addIncidentEvent({

    type: "VIDEO",

    icon: "🎥",

    title: "Video Recording Uploaded",

    description:
        "Emergency video evidence uploaded successfully.",

    fileName:
       "Emergency Video",

    status:
        "Uploaded"

});
// =====================================================
// REQUEST EMERGENCY PERMISSIONS
// =====================================================
// REQUEST EMERGENCY PERMISSIONS AFTER LOGIN
// =====================================================

// =====================================================
// REQUEST EMERGENCY PERMISSIONS AFTER LOGIN
// =====================================================

async function requestEmergencyPermissions() {

    console.log("=================================");
    console.log("SHESAFE PERMISSION SETUP");
    console.log("=================================");


    // =================================================
    // 1. LOCATION PERMISSION
    // =================================================

    let locationPermissionDenied = false;
    let locationServiceIssue = false;

    if (!navigator.geolocation) {

        console.error(
            "❌ Geolocation is not supported."
        );

        alert(
            "Location is not supported on this device."
        );

        return false;
    }


    console.log(
        "📍 Requesting Android Location permission..."
    );


    await new Promise(function(resolve) {

        navigator.geolocation.getCurrentPosition(

            // -----------------------------------------
            // LOCATION SUCCESS
            // -----------------------------------------

            function(position) {

                console.log(
                    "✅ Location permission granted."
                );

                console.log(
                    "Latitude:",
                    position.coords.latitude
                );

                console.log(
                    "Longitude:",
                    position.coords.longitude
                );

                resolve();
            },


            // -----------------------------------------
            // LOCATION ERROR
            // -----------------------------------------

            function(error) {

                console.warn(
                    "⚠️ Location result:",
                    error
                );

                console.warn(
                    "Location error code:",
                    error.code
                );

                console.warn(
                    "Location error message:",
                    error.message
                );


                // code 1 = permission denied
                if (error.code === 1) {

                    locationPermissionDenied = true;

                }


                // code 2 = location unavailable
                // Permission may still be granted.
                //
                // IMPORTANT:
                // Do NOT stop the login flow here.
                //
                if (error.code === 2) {

                    locationServiceIssue = true;

                    console.warn(
                        "⚠️ Location service unavailable, " +
                        "but continuing to request camera/microphone."
                    );
                }


                // code 3 = timeout
                // Also don't block camera/microphone.

                resolve();
            },


            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 60000
            }

        );

    });


    // =================================================
    // LOCATION PERMISSION WAS EXPLICITLY DENIED
    // =================================================

    if (locationPermissionDenied) {

        alert(
            "Please allow Location permission for SheSafe."
        );

        return false;
    }


    if (locationServiceIssue) {

        console.warn(
            "⚠️ Could not obtain GPS position."
        );

        console.warn(
            "Continuing with Camera + Microphone permission."
        );
    }


    // =================================================
    // 2. CAMERA + MICROPHONE
    // =================================================

    console.log(
        "📷🎤 Requesting Camera + Microphone..."
    );


    if (
        !navigator.mediaDevices ||
        !navigator.mediaDevices.getUserMedia
    ) {

        console.error(
            "❌ getUserMedia is unavailable."
        );

        alert(
            "Camera and microphone are not available in SheSafe."
        );

        return false;
    }


    try {

        const stream =
            await navigator.mediaDevices.getUserMedia({

                video: true,

                audio: true

            });


        console.log(
            "================================="
        );

        console.log(
            "✅ CAMERA PERMISSION GRANTED"
        );

        console.log(
            "✅ MICROPHONE PERMISSION GRANTED"
        );

        console.log(
            "================================="
        );


        // Stop temporary stream.
        // We only requested it to obtain permission.

        stream.getTracks().forEach(
            function(track) {

                track.stop();

            }
        );


    } catch (mediaError) {

        console.error(
            "❌ Camera/Microphone error:",
            mediaError
        );

        console.error(
            "Error name:",
            mediaError?.name
        );

        console.error(
            "Error message:",
            mediaError?.message
        );


        if (
            mediaError?.name ===
            "NotAllowedError"
        ) {

            alert(
                "Please allow Camera and Microphone permissions for SheSafe."
            );

        } else if (
            mediaError?.name ===
            "NotFoundError"
        ) {

            alert(
                "Camera or microphone could not be found on this device."
            );

        } else {

            alert(
                "Unable to access Camera and Microphone.\n\n" +
                "Please check SheSafe permissions in Android Settings."
            );
        }


        return false;
    }


    // =================================================
    // EVERYTHING SUCCESSFUL
    // =================================================

    console.log(
        "================================="
    );

    console.log(
        "✅ ALL REQUIRED PERMISSIONS GRANTED"
    );

    console.log(
        "================================="
    );


    return true;
}

async function saveCallRecord({
    contactName,
    phoneNumber,
    callStatus = "INITIATED",
    durationSeconds = 0
}) {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    const incidentId = localStorage.getItem("currentIncidentId");

    if (!token || !userId || !incidentId) {
        console.error("Missing token, userId or incidentId");
        return;
    }

    const callRecord = {
        incidentId: Number(incidentId),
        userId: Number(userId),
        contactName: contactName,
        phoneNumber: phoneNumber,
        callStatus: callStatus,
        callTime: new Date().toISOString(),
        durationSeconds: durationSeconds
    };

    try {
        const response = await fetch(`${CALLS_URL}/save`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(callRecord)
        });

        if (!response.ok) {
            console.error(
                "Failed to save call:",
                await response.text()
            );
            return;
        }

        console.log("Call record saved successfully");

    } catch (error) {
        console.error("Call record error:", error);
    }
}
async function saveMessageRecord({
    recipientName,
    recipientPhone,
    message,
    deliveryStatus = "SENT"
}) {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    const incidentId = localStorage.getItem("currentIncidentId");

    if (!token || !userId || !incidentId) {
        console.error("Missing token, userId or incidentId");
        return;
    }

    const messageRecord = {
        incidentId: Number(incidentId),
        userId: Number(userId),
        recipientName: recipientName,
        recipientPhone: recipientPhone,
        message: message,
        deliveryStatus: deliveryStatus,
        sentAt: new Date().toISOString()
    };

    try {
        const response = await fetch(`${MESSAGES_URL}/save`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(messageRecord)
        });

        if (!response.ok) {
            console.error(
                "Failed to save message:",
                await response.text()
            );
            return;
        }

        console.log("Message record saved successfully");

    } catch (error) {
        console.error("Message record error:", error);
    }
}
function checkCurrentLocation() {

    const status =
        document.getElementById("locationStatus");

    if (!navigator.geolocation) {

        status.innerHTML =
            "❌ Location services are not supported by this browser.";

        return;
    }

    status.innerHTML =
        "📍 Detecting your current location...";

    navigator.geolocation.getCurrentPosition(

        async function(position) {

            const latitude =
                position.coords.latitude;

            const longitude =
                position.coords.longitude;

            console.log(
                "Current Location:",
                latitude,
                longitude
            );

            status.innerHTML =
                "✅ Location detected. Checking risk...";

            await checkRiskForLocation(
                latitude,
                longitude
            );
        },

        function(error) {

            console.error(
                "Location error:",
                error
            );

            if (error.code === 1) {

                status.innerHTML =
                    "❌ Location permission denied. " +
                    "Please allow location access.";

            } else if (error.code === 2) {

                status.innerHTML =
                    "❌ Your location could not be determined.";

            } else if (error.code === 3) {

                status.innerHTML =
                    "❌ Location request timed out.";

            } else {

                status.innerHTML =
                    "❌ Unable to access your location.";
            }
        },

        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
    );
}


async function checkRiskForLocation(
    latitude,
    longitude
) {

    const result =
        document.getElementById("riskResult");

    const status =
        document.getElementById("locationStatus");

    const token =
        localStorage.getItem("token");

    if (!token) {

        status.innerHTML =
            "❌ Please login first.";

        return;
    }

    try {

        const response =
            await fetch(
                `${BASE_URL}/api/risk-prediction`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token
                    },

                    body: JSON.stringify({
                        latitude: Number(latitude),
                        longitude: Number(longitude)
                    })
                }
            );


        if (!response.ok) {

    const errorText =
        await response.text();

    console.error(
        "RISK PREDICTION STATUS:",
        response.status
    );

    console.error(
        "RISK PREDICTION ERROR:",
        errorText
    );

    status.innerHTML =
        "❌ Risk API error: " +
        response.status;

    return;
}


        const data =
            await response.json();

        console.log(
            "RISK PREDICTION:",
            data
        );


        result.style.display = "block";


        result.innerHTML = `

            <div class="risk-score">
                ${Math.round(data.riskScore)}/100
            </div>

            <div class="risk-level">
                ${data.riskLevel}
            </div>

            <div class="risk-recommendation">
                💡 ${data.recommendation}
            </div>

            <div class="risk-factors">

                <h3>🔍 Risk Factors</h3>

                <p>
                    🚨 Historical Crime:
                    ${Math.round(data.historicalCrimeScore)}
                </p>

                <p>
                    👥 User Reports:
                    ${Math.round(data.userReportScore)}
                </p>

                <p>
                    🌙 Time of Day:
                    ${Math.round(data.timeOfDayScore)}
                </p>

                <p>
                    🏙️ Population Density:
                    ${Math.round(data.populationDensityScore)}
                </p>

             <p>
    💡 Street Lighting:
    ${
        data.lightingScore === null ||
        data.lightingScore === undefined
            ? "Data unavailable"
            : Math.round(data.lightingScore)
    }
</p>

                <p>
                    🌦️ Weather:
                    ${Math.round(data.weatherScore)}
                </p>
             <p>
    🚦 Traffic:
    ${
        data.trafficScore === null ||
        data.trafficScore === undefined
            ? "Data unavailable"
            : Math.round(
                data.trafficScore
            )
    }
</p>

            </div>

        `;


        status.innerHTML =
            "✅ Risk analysis completed.";

    } catch (error) {

        console.error(
            "RISK PREDICTION REQUEST ERROR:",
            error
        );

        status.innerHTML =
            "❌ Unable to connect to risk prediction service.";
    }
}
// =====================================================
// INCIDENT REPORTING
// =====================================================
// =====================================================
// CROWD-SOURCED INCIDENT REPORTING
// =====================================================

window.sheSafeCrowdLatitude = null;
window.sheSafeCrowdLongitude = null;


// =====================================================
// DETECT INCIDENT LOCATION
// =====================================================

function detectIncidentReportLocation() {

    const status =
        document.getElementById(
            "incidentLocationStatus"
        );

    if (!status) {
        return;
    }


    status.innerHTML =
        "📍 Detecting your current location...";


    if (!navigator.geolocation) {

        status.innerHTML =
            "❌ Geolocation is not supported by this browser.";

        return;
    }


    navigator.geolocation.getCurrentPosition(

        function(position) {

            window.sheSafeCrowdLatitude =
                Number(position.coords.latitude);

            window.sheSafeCrowdLongitude =
                Number(position.coords.longitude);


            console.log(
                "CROWD INCIDENT LATITUDE:",
                window.sheSafeCrowdLatitude
            );

            console.log(
                "CROWD INCIDENT LONGITUDE:",
                window.sheSafeCrowdLongitude
            );


            status.innerHTML =
                "✅ Location detected successfully.";
        },


        function(error) {

            console.error(
                "GPS ERROR:",
                error
            );

            status.innerHTML =
                "❌ Unable to access your location. " +
                "Please allow location permission.";
        },


        {
            enableHighAccuracy: true,
            timeout: 15000,
            maximumAge: 0
        }
    );
}


// =====================================================
// SUBMIT CROWD INCIDENT
// =====================================================

async function submitCrowdIncident() {

    const incidentType =
        document.getElementById(
            "incidentType"
        ).value;


    const description =
        document.getElementById(
            "incidentDescription"
        ).value.trim();


    const status =
        document.getElementById(
            "incidentReportStatus"
        );


    const userId =
        localStorage.getItem("userId");


    const token =
        localStorage.getItem("token");


    if (!userId || !token) {

        status.innerHTML =
            "❌ Please login first.";

        return;
    }


    if (!incidentType) {

        status.innerHTML =
            "❌ Please select an incident type.";

        return;
    }


    if (!description) {

        status.innerHTML =
            "❌ Please enter a description.";

        return;
    }


    const latitude =
        window.sheSafeCrowdLatitude;


    const longitude =
        window.sheSafeCrowdLongitude;


    if (
        latitude === null ||
        latitude === undefined ||
        longitude === null ||
        longitude === undefined
    ) {

        status.innerHTML =
            "❌ Please detect your location first.";

        return;
    }


    const report = {

        userId: Number(userId),

        incidentType:
            incidentType,

        description:
            description,

        latitude:
            Number(latitude),

        longitude:
            Number(longitude),

        reportedAt:
            new Date().toISOString(),

        status:
            "REPORTED"
    };


    console.log(
        "FINAL INCIDENT REQUEST:",
        report
    );


    console.log(
        "FINAL INCIDENT JSON:",
        JSON.stringify(report)
    );


    status.innerHTML =
        "📤 Submitting incident report...";


    try {

        const response =
            await fetch(
                `${BASE_URL}/api/incident-reports`,
                {
                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token
                    },

                    body:
                        JSON.stringify(report)
                }
            );


        if (!response.ok) {

            const errorText =
                await response.text();


            console.error(
                "INCIDENT BACKEND ERROR:",
                errorText
            );


            status.innerHTML =
                "❌ Failed to submit incident report.";

            return;
        }


        const savedReport =
            await response.json();


        console.log(
            "INCIDENT SAVED:",
            savedReport
        );


        status.innerHTML =
            "✅ Incident reported successfully!";


        document.getElementById(
            "incidentType"
        ).value = "";


        document.getElementById(
            "incidentDescription"
        ).value = "";


    } catch (error) {

        console.error(
            "INCIDENT SUBMIT ERROR:",
            error
        );


        status.innerHTML =
            "❌ Unable to connect to server.";
    }
}
// ============================================================
// SHAKE SOS
// ============================================================

// =====================================================
// SHAKE SOS
// =====================================================

let shakeSosEnabled = false;
let lastShakeTime = 0;
let shakeListenerAttached = false;

let previousShakeMagnitude = 0;
let strongShakeCount = 0;

const SHAKE_THRESHOLD = 18;
const SHAKE_COOLDOWN = 5000;


async function toggleShakeSOS() {

    const button =
        document.getElementById("shakeSosToggle");

    const status =
        document.getElementById("shakeSosStatus");


    if (shakeSosEnabled) {

        shakeSosEnabled = false;

        button.textContent = "Enable Shake SOS";

        status.textContent =
            "Shake detection is disabled.";

        console.log("Shake SOS disabled.");

        return;
    }


    if (!("DeviceMotionEvent" in window)) {

        status.textContent =
            "Motion detection is not supported on this device.";

        return;
    }


    // Request permission if the platform requires it.
    if (
        typeof DeviceMotionEvent.requestPermission === "function"
    ) {

        try {

            const permission =
                await DeviceMotionEvent.requestPermission();

            if (permission !== "granted") {

                status.textContent =
                    "Motion permission was not granted.";

                return;
            }

        } catch (error) {

            console.error(
                "Motion permission error:",
                error
            );

            status.textContent =
                "Could not enable motion detection.";

            return;
        }
    }


    shakeSosEnabled = true;

    button.textContent = "Disable Shake SOS";

    status.textContent =
        "🟢 Shake SOS enabled while the app is open.";

    console.log("Shake SOS enabled.");


    if (!shakeListenerAttached) {

        window.addEventListener(
            "devicemotion",
            handleShakeMotion,
            true
        );

        shakeListenerAttached = true;
    }
}



function handleShakeMotion(event) {
    if (!shakeSosEnabled || smartSOSActive || smartSOSStarting) {
        return;
    }

    const acceleration =
        event.acceleration || event.accelerationIncludingGravity;

    if (!acceleration) return;

    const x = acceleration.x || 0;
    const y = acceleration.y || 0;
    const z = acceleration.z || 0;

    const magnitude = Math.sqrt(x * x + y * y + z * z);
    const now = Date.now();

    if (
        magnitude > SHAKE_THRESHOLD &&
        Math.abs(magnitude - previousShakeMagnitude) > 8
    ) {
        strongShakeCount++;
    } else {
        strongShakeCount = Math.max(0, strongShakeCount - 1);
    }

    previousShakeMagnitude = magnitude;

    if (strongShakeCount < 3) return;
    if (now - lastShakeTime < SHAKE_COOLDOWN) return;

    lastShakeTime = now;
    strongShakeCount = 0;

    console.warn("🚨 Shake SOS detected!");
    activateSmartSOS("SHAKE");
}

// =====================================================
// SHESAFE: GET CURRENT LOCATION
// =====================================================

async function getSheSafeCurrentLocation() {

    console.log("📍 Getting SheSafe current location...");

    // -------------------------------------------------
    // Try Capacitor native Geolocation first
    // -------------------------------------------------

    try {

        const isNative =
            window.Capacitor &&
            typeof window.Capacitor.isNativePlatform === "function" &&
            window.Capacitor.isNativePlatform();

        if (isNative) {

            const Geolocation =
                window.Capacitor?.Plugins?.Geolocation;

            if (Geolocation) {

                console.log(
                    "📱 Using native Capacitor Geolocation"
                );

                const position =
                    await Geolocation.getCurrentPosition({

                        enableHighAccuracy: true,

                        timeout: 30000,

                        maximumAge: 60000

                    });

                if (position && position.coords) {

                    console.log(
                        "✅ Native location obtained:",
                        position.coords.latitude,
                        position.coords.longitude
                    );

                    return position;
                }
            }
        }

    } catch (nativeError) {

        console.warn(
            "⚠️ Native location failed:",
            nativeError
        );
    }


    // -------------------------------------------------
    // Fallback to browser/WebView Geolocation
    // -------------------------------------------------

    if (!navigator.geolocation) {

        throw new Error(
            "Geolocation is not supported."
        );
    }


    console.log(
        "🌐 Trying WebView/browser location..."
    );


    return await new Promise(
        function(resolve, reject) {

            navigator.geolocation.getCurrentPosition(

                function(position) {

                    console.log(
                        "✅ WebView location obtained:",
                        position.coords.latitude,
                        position.coords.longitude
                    );

                    resolve(position);
                },


                function(error) {

                    console.error(
                        "❌ WebView location failed:",
                        error
                    );

                    reject(error);
                },


                {
                    enableHighAccuracy: true,

                    timeout: 30000,

                    maximumAge: 60000
                }
            );

        }
    );
}


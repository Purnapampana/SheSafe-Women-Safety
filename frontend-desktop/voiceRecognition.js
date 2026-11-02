

window.voiceRecognition = null;

window.voiceRecognitionActive = false;

window.voiceRestartTimer = null;

window.voiceEmergencyCooldown = false;


/* =====================================================
   SETTINGS
===================================================== */

const VOICE_SETTINGS = {
    
    autoRestart: true,
    restartDelay: 700,

    emergencyCooldown: 10000,

    minimumConfidence: 0.60

};
const SUPPORTED_VOICE_LANGUAGES = [

    "en-IN",
    "hi-IN",
    "te-IN",
    "ta-IN",
    "kn-IN",
    "ml-IN",
    "bn-IN",
    "mr-IN",
    "gu-IN",
    "pa-IN",
    "or-IN",
    "ur-IN"

];


/* =====================================================
   SUPPORTED EMERGENCY PHRASES
===================================================== */

window.voiceEmergencyPhrases = {

    // =================================================
    // ENGLISH
    // =================================================

    "en-IN": [
        "help me",
        "save me",
        "emergency",
        "call police",
        "please help",
        "someone help me",
        "help",
        "i need help"
    ],

    "en-US": [
        "help me",
        "save me",
        "emergency",
        "call police",
        "please help",
        "someone help me",
        "help",
        "i need help"
    ],

    "en-GB": [
        "help me",
        "save me",
        "emergency",
        "call police",
        "please help",
        "someone help me",
        "help",
        "i need help"
    ],


    // =================================================
    // HINDI
    // =================================================

    "hi-IN": [
        "मदद करो",
        "मेरी मदद करो",
        "मदद कीजिए",
        "मेरी मदद कीजिए",
        "बचाओ",
        "मुझे बचाओ",
        "आपातकाल",
        "पुलिस को बुलाओ",
        "पुलिस बुलाओ"
    ],


    // =================================================
    // TELUGU
    // =================================================

    "te-IN": [
        "నాకు సహాయం చేయండి",
        "నన్ను కాపాడండి",
        "అత్యవసరం",
        "పోలీసులకు కాల్ చేయండి",
        "నాకు సహాయం కావాలి",
        "సహాయం చేయండి",
        "నన్ను రక్షించండి",
        "పోలీసులను పిలవండి"
    ],


    // =================================================
    // TAMIL
    // =================================================

    "ta-IN": [
        "எனக்கு உதவி செய்யுங்கள்",
        "என்னை காப்பாற்றுங்கள்",
        "அவசரம்",
        "போலீசை அழைக்கவும்",
        "எனக்கு உதவி வேண்டும்",
        "உதவி செய்யுங்கள்",
        "என்னை காப்பாற்றுங்கள்",
        "போலீஸை கூப்பிடுங்கள்"
    ],


    // =================================================
    // KANNADA
    // =================================================

    "kn-IN": [
        "ನನಗೆ ಸಹಾಯ ಮಾಡಿ",
        "ನನ್ನನ್ನು ಉಳಿಸಿ",
        "ತುರ್ತು ಪರಿಸ್ಥಿತಿ",
        "ಪೊಲೀಸರಿಗೆ ಕರೆ ಮಾಡಿ",
        "ನನಗೆ ಸಹಾಯ ಬೇಕು",
        "ಸಹಾಯ ಮಾಡಿ",
        "ನನ್ನನ್ನು ಕಾಪಾಡಿ",
        "ಪೊಲೀಸರನ್ನು ಕರೆಸಿ"
    ],


    // =================================================
    // MALAYALAM
    // =================================================

    "ml-IN": [
        "എന്നെ സഹായിക്കൂ",
        "എനിക്ക് സഹായം വേണം",
        "എന്നെ രക്ഷിക്കൂ",
        "അത്യാവശ്യം",
        "പോലീസിനെ വിളിക്കൂ",
        "സഹായിക്കൂ"
    ],


    // =================================================
    // BENGALI
    // =================================================

    "bn-IN": [
        "আমাকে সাহায্য করুন",
        "আমাকে বাঁচান",
        "জরুরি অবস্থা",
        "পুলিশকে ডাকুন",
        "আমার সাহায্য দরকার",
        "সাহায্য করুন"
    ],


    // =================================================
    // MARATHI
    // =================================================

    "mr-IN": [
        "मला मदत करा",
        "मला वाचवा",
        "मदत करा",
        "आपत्कालीन परिस्थिती",
        "पोलिसांना बोलवा",
        "मला मदत हवी आहे"
    ],


    // =================================================
    // GUJARATI
    // =================================================

    "gu-IN": [
        "મારી મદદ કરો",
        "મને બચાવો",
        "મદદ કરો",
        "કટોકટી",
        "પોલીસને બોલાવો",
        "મને મદદ જોઈએ"
    ],


    // =================================================
    // PUNJABI
    // =================================================

    "pa-IN": [
        "ਮੇਰੀ ਮਦਦ ਕਰੋ",
        "ਮੈਨੂੰ ਬਚਾਓ",
        "ਮਦਦ ਕਰੋ",
        "ਐਮਰਜੈਂਸੀ",
        "ਪੁਲਿਸ ਨੂੰ ਬੁਲਾਓ",
        "ਮੈਨੂੰ ਮਦਦ ਚਾਹੀਦੀ ਹੈ"
    ],


    // =================================================
    // ODIA
    // =================================================

    "or-IN": [
        "ମୋତେ ସାହାଯ୍ୟ କରନ୍ତୁ",
        "ମୋତେ ବଞ୍ଚାନ୍ତୁ",
        "ସାହାଯ୍ୟ କରନ୍ତୁ",
        "ଜରୁରୀକାଳୀନ",
        "ପୋଲିସକୁ ଡାକନ୍ତୁ",
        "ମୋତେ ସାହାଯ୍ୟ ଦରକାର"
    ],


    // =================================================
    // URDU
    // =================================================

    "ur-IN": [
        "میری مدد کریں",
        "مجھے بچائیں",
        "مدد کریں",
        "ایمرجنسی",
        "پولیس کو بلائیں",
        "مجھے مدد چاہیے"
    ]

};


/* =====================================================
   CHECK BROWSER SUPPORT
===================================================== */

function isVoiceRecognitionSupported() {

    return !!(
        window.SpeechRecognition ||
        window.webkitSpeechRecognition
    );

}


/* =====================================================
   NORMALIZE TEXT
===================================================== */

function normalizeVoiceText(text) {

    if (!text) {
        return "";
    }

    return String(text)
        .toLowerCase()
        .trim()
        .replace(/[.,!?;:]/g, "")
        .replace(/\s+/g, " ");

}



/* =====================================================
   GET SELECTED VOICE LANGUAGE
===================================================== */



function getVoiceLanguage() {
    const savedLanguage =
        localStorage.getItem("preferredLanguage") || "en";

    const languageMap = {
        en: "en-IN",
        te: "te-IN",
        hi: "hi-IN",
        ur: "ur-IN"
    };

    const voiceLanguage =
        languageMap[savedLanguage] || "en-IN";

    console.log(
        "Voice recognition language:",
        voiceLanguage
    );

    return voiceLanguage;
}



/* =====================================================
   DETECT EMERGENCY PHRASE
===================================================== */


function detectEmergencyVoicePhrase(text) {

    const normalizedText =
        normalizeVoiceText(text);

    if (!normalizedText) {
        return null;
    }

    const selectedLanguage =
        getVoiceLanguage();

    console.log(
        "🔎 Checking emergency phrase:",
        normalizedText
    );

    console.log(
        "🌐 Checking selected language:",
        selectedLanguage
    );

    const phrases =
        window.voiceEmergencyPhrases[selectedLanguage] || [];

    for (const phrase of phrases) {

        const normalizedPhrase =
            normalizeVoiceText(phrase);

        if (
            normalizedText.includes(
                normalizedPhrase
            )
        ) {

            console.warn(
                "🚨 Emergency phrase matched:",
                phrase
            );

            console.warn(
                "🌐 Language:",
                selectedLanguage
            );

            return phrase;
        }
    }

    return null;
}


/* =====================================================
   START VOICE RECOGNITION
===================================================== */

function startVoiceRecognition() {


    /* =================================================
       CHECK SUPPORT
    ================================================= */

    if (
        !isVoiceRecognitionSupported()
    ) {

        alert(
            "Voice recognition is not supported by this browser."
        );


        setVoiceTranscript(
            "❌ Voice recognition is not supported by this browser."
        );


        return;

    }


    /* =================================================
       PREVENT DUPLICATE RECOGNITION
    ================================================= */

    if (
        window.voiceRecognitionActive
    ) {

        console.log(
            "🎙️ Voice recognition is already running."
        );


        setVoiceTranscript(
            "🎙️ Voice Safety is already active."
        );


        return;

    }


    /* =================================================
       GET API
    ================================================= */

    const SpeechRecognition =
        window.SpeechRecognition ||
        window.webkitSpeechRecognition;


    /* =================================================
       CREATE RECOGNITION
    ================================================= */

    const recognition =
        new SpeechRecognition();


    window.voiceRecognition =
        recognition;


    /* =================================================
       RECOGNITION SETTINGS
    ================================================= */

    recognition.continuous = true;

    recognition.interimResults = true;

    recognition.maxAlternatives = 3;


    /*
     * Automatically determine language.
     */
   recognition.lang =
    getVoiceLanguage();

console.log(
    "🌐 Primary recognition language:",
    recognition.lang
);

    /* =================================================
       STARTED
    ================================================= */

    recognition.onstart =
        function () {

            window.voiceRecognitionActive =
                true;


            updateVoiceStatus(true);


            const selectedLanguage =
    getVoiceLanguage();

setVoiceTranscript(
    "🎙️ Listening in " +
    getVoiceLanguageName(selectedLanguage) +
    "..."
);

            console.log(
                "🎙️ SheSafe Voice Safety started."
            );

        };


    /* =================================================
       SPEECH RESULT
    ================================================= */

    recognition.onresult =
        function (event) {


            let finalText = "";

            let interimText = "";


            /* -----------------------------------------
               READ RESULTS
            ----------------------------------------- */

            for (
                let i = event.resultIndex;

                i < event.results.length;

                i++
            ) {

                const result =
                    event.results[i];


                /*
                 * Check all alternatives instead
                 * of only relying on the first
                 * transcript where possible.
                 */

                const transcript =
                    result[0].transcript;


                if (
                    result.isFinal
                ) {

                    finalText +=
                        transcript;

                } else {

                    interimText +=
                        transcript;

                }

            }


            /* -----------------------------------------
               DISPLAY TRANSCRIPT
            ----------------------------------------- */

            const displayText =
                finalText ||
                interimText;


            if (displayText) {

                setVoiceTranscript(
                    "🎙️ " +
                    displayText
                );

            }


            /* -----------------------------------------
               ONLY PROCESS FINAL SPEECH
            ----------------------------------------- */

            if (!finalText) {
                return;
            }


            console.log(
                "🎙️ Recognized:",
                finalText
            );


            /* =================================================
               CONFIDENCE
            ================================================= */

            let confidence = 1;


            try {

                const result =
                    event.results[
                        event.resultIndex
                    ][0];


                if (
                    typeof result.confidence ===
                    "number"
                ) {

                    confidence =
                        result.confidence;

                }

            } catch (error) {

                console.warn(
                    "Speech confidence unavailable."
                );

            }


            console.log(
                "🎯 Confidence:",
                confidence
            );


            /* =================================================
               DETECT EMERGENCY PHRASE
            ================================================= */

            const detectedPhrase =
                detectEmergencyVoicePhrase(
                    finalText
                );


            if (!detectedPhrase) {
                return;
            }


            console.warn(
                "🚨 Emergency phrase detected:",
                detectedPhrase
            );


            /* =================================================
               CONFIDENCE CHECK
            ================================================= */

            if (
                confidence <
                VOICE_SETTINGS.minimumConfidence
            ) {

                console.warn(
                    "⚠️ Emergency phrase detected, but confidence is low:",
                    confidence
                );


                setVoiceTranscript(
                    "⚠️ Emergency phrase heard, but recognition confidence is low."
                );


                return;

            }


            /* =================================================
               HANDLE EMERGENCY
            ================================================= */

            handleVoiceEmergency(
                detectedPhrase
            );

        };


    /* =====================================================
       ERROR HANDLING
    ===================================================== */

   // =====================================================
// ERROR HANDLING
// =====================================================

recognition.onerror = function (event) {

    console.log(
        "🎙️ Voice recognition event:",
        event.error
    );

    // -------------------------------------------------
    // ABORTED
    // -------------------------------------------------
    // Chrome can report "aborted" when the recognition
    // service ends/restarts. Do not treat this as a
    // microphone failure.
    // -------------------------------------------------

    if (event.error === "aborted") {

        console.log(
            "🔄 Voice recognition aborted. Restarting..."
        );

        return;
    }


    // -------------------------------------------------
    // MICROPHONE PERMISSION
    // -------------------------------------------------

    if (event.error === "not-allowed") {

        window.voiceRecognitionActive = false;

        updateVoiceStatus(false);

        setVoiceTranscript(
            "❌ Microphone permission denied."
        );

        return;
    }


    // -------------------------------------------------
    // MICROPHONE UNAVAILABLE
    // -------------------------------------------------

    if (event.error === "audio-capture") {

        setVoiceTranscript(
            "❌ Microphone is not available."
        );

        return;
    }


    // -------------------------------------------------
    // NO SPEECH
    // -------------------------------------------------

    if (event.error === "no-speech") {

        console.log(
            "🎙️ No speech detected. Continuing..."
        );

        return;
    }


    // -------------------------------------------------
    // NETWORK
    // -------------------------------------------------

  // -------------------------------------------------
// NETWORK
// -------------------------------------------------

if (event.error === "network") {

    console.warn(
        "⚠️ Speech recognition network event."
    );

    // If Voice Safety was intentionally stopped
    // because an emergency was detected, don't show
    // a misleading network error.
    if (!window.voiceRecognitionActive) {

        console.log(
            "ℹ️ Network event ignored because Voice Safety is inactive."
        );

        return;
    }

    setVoiceTranscript(
        "⚠️ Speech recognition service temporarily unavailable."
    );

    return;
}


    console.warn(
        "⚠️ Voice recognition error:",
        event.error
    );
};


// =====================================================
// RECOGNITION ENDED
// =====================================================

recognition.onend = function () {

    console.log(
        "🎙️ Voice recognition ended."
    );


    // -------------------------------------------------
    // USER INTENTIONALLY STOPPED VOICE SAFETY
    // -------------------------------------------------

    if (!window.voiceRecognitionActive) {

        console.log(
            "⏹ Voice Safety is inactive. Not restarting."
        );

        updateVoiceStatus(false);

        return;
    }


    // -------------------------------------------------
    // AUTOMATIC RESTART
    // -------------------------------------------------

    if (!VOICE_SETTINGS.autoRestart) {

        updateVoiceStatus(false);

        return;
    }


    clearTimeout(
        window.voiceRestartTimer
    );


    window.voiceRestartTimer = setTimeout(
        function () {

            // User may have pressed Stop while waiting
            if (!window.voiceRecognitionActive) {

                console.log(
                    "⏹ Voice Safety stopped before restart."
                );

                return;
            }


            console.log(
                "🔄 Creating fresh voice recognition session..."
            );


            /*
             * IMPORTANT:
             *
             * Do NOT call recognition.start()
             * on the old recognition object.
             *
             * Start a completely new session.
             */

            window.voiceRecognitionActive = false;


            startVoiceRecognition();


        },
        VOICE_SETTINGS.restartDelay
    );

};

    /* =====================================================
       RECOGNITION ENDED
    ===================================================== */

    


    /* =====================================================
       START MICROPHONE
    ===================================================== */

    try {

        recognition.start();


        console.log(
            "🎙️ Starting microphone..."
        );

    } catch (error) {

        console.error(
            "❌ Unable to start voice recognition:",
            error
        );


        window.voiceRecognitionActive =
            false;


        updateVoiceStatus(
            false
        );


        setVoiceTranscript(
            "❌ Unable to start voice protection."
        );

    }

}


/* =====================================================
   STOP VOICE RECOGNITION
===================================================== */

function stopVoiceRecognition() {


    /* ---------------------------------------------
       DISABLE AUTO RESTART
    --------------------------------------------- */

    window.voiceRecognitionActive =
        false;


    /* ---------------------------------------------
       CANCEL RESTART TIMER
    --------------------------------------------- */

    if (
        window.voiceRestartTimer
    ) {

        clearTimeout(
            window.voiceRestartTimer
        );


        window.voiceRestartTimer =
            null;

    }


    /* ---------------------------------------------
       STOP RECOGNITION
    --------------------------------------------- */

    if (
        window.voiceRecognition
    ) {

        try {

            window.voiceRecognition.stop();

        } catch (error) {

            console.warn(
                "Voice stop error:",
                error
            );

        }

    }


    /* ---------------------------------------------
       UPDATE UI
    --------------------------------------------- */

    updateVoiceStatus(
        false
    );


    setVoiceTranscript(
        "Voice protection is OFF."
    );


    console.log(
        "⏹ Voice Safety stopped."
    );

}


/* =====================================================
   UPDATE STATUS UI
===================================================== */

function updateVoiceStatus(active) {

    const badge =
        document.getElementById(
            "voiceStatusBadge"
        );


    if (!badge) {
        return;
    }


    if (active) {

        badge.textContent =
            "LISTENING";


        badge.classList.add(
            "active"
        );

    } else {

        badge.textContent =
            "OFF";


        badge.classList.remove(
            "active"
        );

    }

}


/* =====================================================
   UPDATE TRANSCRIPT
===================================================== */

function setVoiceTranscript(text) {

    const element =
        document.getElementById(
            "voiceTranscript"
        );


    if (!element) {
        return;
    }


    element.textContent =
        text;

}


/* =====================================================
   HANDLE VOICE EMERGENCY
===================================================== */

function handleVoiceEmergency(
    detectedPhrase
) {

    console.warn(
        "🚨 VOICE EMERGENCY ACTIVATED"
    );


    console.warn(
        "Detected phrase:",
        detectedPhrase
    );


    /* =================================================
       PREVENT DUPLICATE SOS
    ================================================= */

    if (
        window.voiceEmergencyCooldown
    ) {

        console.warn(
            "⚠️ SOS cooldown is active."
        );


        return;

    }


    window.voiceEmergencyCooldown =
        true;


    /* =================================================
       SHOW EMERGENCY MESSAGE
    ================================================= */

    const message =
        document.getElementById(
            "voiceEmergencyMessage"
        );


    if (message) {

        message.textContent =
            "🚨 Emergency detected from voice: " +
            detectedPhrase;


        message.classList.add(
            "active"
        );

    }


    /* =================================================
       UPDATE TRANSCRIPT
    ================================================= */

    setVoiceTranscript(
        "🚨 Emergency phrase detected: " +
        detectedPhrase
    );


    /* =================================================
       STOP LISTENING TEMPORARILY
    ================================================= */

    window.voiceRecognitionActive =
        false;


    if (
        window.voiceRestartTimer
    ) {

        clearTimeout(
            window.voiceRestartTimer
        );


        window.voiceRestartTimer =
            null;

    }


    if (
        window.voiceRecognition
    ) {

        try {

            window.voiceRecognition.stop();

        } catch (error) {

            console.warn(
                "Voice stop error:",
                error
            );

        }

    }


    /* =================================================
       🚨 REAL SHESAFE SOS
       
       IMPORTANT:
       Use triggerSOS(), NOT openSOS().
    ================================================= */

    if (
        typeof triggerSOS ===
        "function"
    ) {

        console.log(
            "🚨 Triggering SheSafe SOS..."
        );


        /*
         * This calls your existing SOS system.
         *
         * triggerSOS() handles:
         *
         * - incident creation
         * - current incident ID
         * - SOS recording
         * - GPS
         * - emergency contacts
         * - email
         * - emergency response
         */

        triggerSOS();


    } else {

        console.error(
            "❌ triggerSOS() not found."
        );


        setVoiceTranscript(
            "🚨 Emergency detected, but SOS function is unavailable."
        );

    }


    /* =================================================
       RESET COOLDOWN
    ================================================= */

    setTimeout(
        function () {

            window.voiceEmergencyCooldown =
                false;

        },

        VOICE_SETTINGS.emergencyCooldown
    );

}


/* =====================================================
   CLEAR EMERGENCY MESSAGE
===================================================== */

function clearVoiceEmergencyMessage() {

    const message =
        document.getElementById(
            "voiceEmergencyMessage"
        );


    if (message) {

        message.textContent =
            "";


        message.classList.remove(
            "active"
        );

    }

}


/* =====================================================
   INITIALIZE VOICE SYSTEM
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "🎙️ Initializing SheSafe Voice Recognition..."
        );


        /* ---------------------------------------------
           INITIAL STATUS
        --------------------------------------------- */

        updateVoiceStatus(
            false
        );


        /* ---------------------------------------------
           BROWSER SUPPORT
        --------------------------------------------- */

        if (
            !isVoiceRecognitionSupported()
        ) {

            setVoiceTranscript(
                "⚠️ Voice recognition is not supported by this browser."
            );


            console.warn(
                "⚠️ SpeechRecognition API not supported."
            );

        } else {

            console.log(
                "✅ SpeechRecognition API supported."
            );


            console.log(
                "🌐 Automatic voice language:",
                getVoiceLanguage()
            );

        }

    }
);
/* =====================================================
   LANGUAGE SELECTION
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const languageSelect =
            document.getElementById(
                "voiceLanguageSelect"
            );

        if (!languageSelect) {
            return;
        }

        languageSelect.addEventListener(
            "change",
            function () {

                const selectedLanguage =
                    this.value;

                const languageName =
                    getVoiceLanguageName(
                        selectedLanguage
                    );

                console.log(
                    "🌐 Voice language changed:",
                    selectedLanguage
                );

                /*
                 * If Voice Safety is currently running,
                 * restart recognition using the new language.
                 */

                if (
                    window.voiceRecognitionActive
                ) {

                    console.log(
                        "🔄 Restarting voice recognition with:",
                        selectedLanguage
                    );

                    stopVoiceRecognition();

                    setTimeout(
                        function () {

                            startVoiceRecognition();

                        },
                        500
                    );

                    return;
                }

                setVoiceTranscript(
                    "🌐 Voice language selected: " +
                    languageName
                );

            }
        );

    }
);
/* =====================================================
   LANGUAGE DISPLAY NAME
===================================================== */

function getVoiceLanguageName(languageCode) {

    const languageNames = {

        "en-IN": "English",
        "hi-IN": "Hindi",
        "te-IN": "Telugu",
        "ta-IN": "Tamil",
        "kn-IN": "Kannada",
        "ml-IN": "Malayalam",
        "bn-IN": "Bengali",
        "mr-IN": "Marathi",
        "gu-IN": "Gujarati",
        "pa-IN": "Punjabi",
        "or-IN": "Odia",
        "ur-IN": "Urdu"

    };

    return languageNames[languageCode] ||
           "English";
}
from datetime import datetime


# ============================================================
# TIME OF DAY
# ============================================================

def get_time_risk():

    hour = datetime.now().hour

    if 6 <= hour < 18:
        return 0.0

    elif 18 <= hour < 21:
        return 0.25

    elif 21 <= hour < 23:
        return 0.50

    else:
        return 0.75


# ============================================================
# POPULATION DENSITY
# ============================================================

def get_population_risk(population_density):
    """
    population_density = people per square kilometer
    """

    if population_density is None:
        return 0.0

    if population_density < 1000:
        return 0.10

    elif population_density < 5000:
        return 0.30

    elif population_density < 10000:
        return 0.60

    else:
        return 0.80


# ============================================================
# WEATHER
# ============================================================

def get_weather_risk(weather_condition):

    if not weather_condition:
        return 0.0

    condition = weather_condition.lower()

    if "clear" in condition:
        return 0.0

    elif "cloud" in condition:
        return 0.05

    elif "rain" in condition:
        return 0.30

    elif "storm" in condition:
        return 0.70

    elif "thunder" in condition:
        return 0.70

    elif "fog" in condition:
        return 0.50

    else:
        return 0.10


# ============================================================
# FINAL RISK SCORE
# ============================================================

def calculate_risk_score(
    incident_risk=0,
    traffic_penalty=0,
    safety_score=0,
    time_risk=0,
    population_risk=0,
    weather_risk=0
):

    # ------------------------------------
    # Incident / crime contribution
    # ------------------------------------

    incident_component = (
        incident_risk * 45
    )

    # ------------------------------------
    # Traffic contribution
    # ------------------------------------

    traffic_component = (
        min(traffic_penalty, 1) * 15
    )

    # ------------------------------------
    # Night-time contribution
    # ------------------------------------

    time_component = (
        time_risk * 15
    )

    # ------------------------------------
    # Population contribution
    # ------------------------------------

    population_component = (
        population_risk * 10
    )

    # ------------------------------------
    # Weather contribution
    # ------------------------------------

    weather_component = (
        weather_risk * 5
    )

    # ------------------------------------
    # Safety infrastructure protection
    # ------------------------------------

    safety_protection = min(
        safety_score * 5,
        10
    )

    # ------------------------------------
    # Final score
    # ------------------------------------

    score = (
        incident_component
        + traffic_component
        + time_component
        + population_component
        + weather_component
        - safety_protection
    )

    score = max(
        0,
        min(score, 100)
    )

    return score


# ============================================================
# RISK CLASSIFICATION
# ============================================================

def classify_risk_zone(score):

    if score >= 75:
        return "CRIME_HOTSPOT"

    elif score >= 50:
        return "HIGH_RISK"

    elif score >= 25:
        return "MODERATE_RISK"

    else:
        return "SAFE_ZONE"


# ============================================================
# APPLY RISK ZONES
# ============================================================

def apply_risk_zones(
    graph,
    population_density=5000,
    weather_condition="Clear"
):

    time_risk = get_time_risk()

    population_risk = get_population_risk(
        population_density
    )

    weather_risk = get_weather_risk(
        weather_condition
    )

    current_hour = datetime.now().hour

    print(
        "Current hour:",
        current_hour
    )

    print(
        "Time risk:",
        round(time_risk, 2)
    )

    print(
        "Population density:",
        population_density,
        "people/km²"
    )

    print(
        "Population risk:",
        round(population_risk, 2)
    )

    print(
        "Weather:",
        weather_condition
    )

    print(
        "Weather risk:",
        round(weather_risk, 2)
    )

    for u, v, key, data in graph.edges(
        keys=True,
        data=True
    ):

        incident_risk = data.get(
            "incident_risk",
            0
        )

        traffic_penalty = data.get(
            "traffic_penalty",
            0
        )

        safety_score = data.get(
            "safety_score",
            0
        )

        score = calculate_risk_score(
            incident_risk=incident_risk,
            traffic_penalty=traffic_penalty,
            safety_score=safety_score,
            time_risk=time_risk,
            population_risk=population_risk,
            weather_risk=weather_risk
        )

        zone = classify_risk_zone(
            score
        )

        data["risk_score"] = score

        data["risk_zone"] = zone

    return graph
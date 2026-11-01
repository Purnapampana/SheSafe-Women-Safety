import os
import requests
import pandas as pd


BACKEND_URL = "http://localhost:8081"


def get_incident_reports(token):

    url = BACKEND_URL + "/api/incident-reports"

    headers = {
        "Authorization": "Bearer " + token
    }

    print("Getting SheSafe incident reports...")

    try:

        response = requests.get(
            url,
            headers=headers,
            timeout=15
        )

        if response.status_code != 200:

            print(
                "❌ Backend returned:",
                response.status_code
            )

            print(response.text)

            return []

        incidents = response.json()

        print(
            "✅ Incident reports received:",
            len(incidents)
        )

        return incidents

    except requests.exceptions.RequestException as error:

        print(
            "❌ Unable to connect to Spring Boot:"
        )

        print(error)

        return []


def get_incident_risk(incident_type):

    if not incident_type:
        return 0.4

    incident_type = (
        incident_type
        .strip()
        .lower()
    )

    high_risk = [
        "assault",
        "sexual assault",
        "harassment",
        "stalking",
        "violence",
        "robbery",
        "attack"
    ]

    medium_risk = [
        "theft",
        "threat",
        "unsafe",
        "suspicious activity"
    ]

    if incident_type in high_risk:
        return 1.0

    if incident_type in medium_risk:
        return 0.6

    return 0.4


def get_time_risk(reported_at):

    if not reported_at:
        return 0.0

    try:

        hour = pd.to_datetime(
            reported_at
        ).hour

        if 6 <= hour < 18:
            return 0.0

        elif 18 <= hour < 21:
            return 0.25

        elif 21 <= hour < 23:
            return 0.50

        else:
            return 0.75

    except Exception:

        return 0.0


def prepare_incidents(incidents):

    prepare = []

    for incident in incidents:

        latitude = incident.get(
            "latitude"
        )

        longitude = incident.get(
            "longitude"
        )

        if (
            latitude is None
            or longitude is None
        ):
            continue

        incident_risk = get_incident_risk(
            incident.get("incidentType")
        )

        prepare.append({

            "id": incident.get("id"),

            "latitude": float(latitude),

            "longitude": float(longitude),

            "incident_type":
                incident.get("incidentType"),

            "risk":
                incident_risk,

            "time_risk":
                get_time_risk(
                    incident.get("reportedAt")
                ),

            "reported_at":
                incident.get("reportedAt"),

            "status":
                incident.get("status")
        })

    print(
        "Prepare incidents:",
        len(prepare)
    )

    return prepare


def create_ml_training_data(
    incidents,
    population_density,
    weather_risk
):

    rows = []

    for incident in incidents:

        incident_risk = incident.get(
            "risk",
            0.4
        )

        time_risk = incident.get(
            "time_risk",
            0
        )

        # These features will later come
        # from the road segment itself.
        traffic_risk = 0.0

        safety_level = 0.5

        population_risk = 0.5

        if population_density is not None:

            if population_density < 1000:
                population_risk = 0.1

            elif population_density < 5000:
                population_risk = 0.3

            elif population_density < 10000:
                population_risk = 0.6

            else:
                population_risk = 0.8

        score = (
            incident_risk * 0.65
            + time_risk * 0.35
        )

        if score >= 0.75:

            risk_zone = "CRIME_HOTSPOT"

        elif score >= 0.50:

            risk_zone = "HIGH_RISK"

        elif score >= 0.25:

            risk_zone = "MODERATE_RISK"

        else:

            risk_zone = "SAFE_ZONE"

        rows.append({

            "incident_risk":
                incident_risk,

            "traffic_risk":
                traffic_risk,

            "time_risk":
                time_risk,

            "population_risk":
                population_risk,

            "safety_level":
                safety_level,

            "weather_risk":
                weather_risk,

            "risk_zone":
                risk_zone
        })

    return pd.DataFrame(rows)
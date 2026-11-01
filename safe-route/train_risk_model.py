import os
import joblib
import pandas as pd

from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report

MODEL_FILE = "risk_model.pkl"


def get_incident_risk(incident_type):

    if not incident_type:
        return 0.4

    incident_type = incident_type.strip().lower()

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
        hour = pd.to_datetime(reported_at).hour

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


def create_training_data(incidents):

    rows = []

    for incident in incidents:

        incident_risk = get_incident_risk(
            incident.get("incidentType")
        )

        time_risk = get_time_risk(
            incident.get("reportedAt")
        )

        # Current incident data does not contain
        # traffic/population/weather/safety values.
        # Keep these as neutral values for now.
        traffic_risk = 0.0
        population_risk = 0.5
        safety_level = 0.5
        weather_risk = 0.0

        # Temporary baseline label derived from
        # the incident severity and time.
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

        rows.append([
            incident_risk,
            traffic_risk,
            time_risk,
            population_risk,
            safety_level,
            weather_risk,
            risk_zone
        ])

    columns = [
        "incident_risk",
        "traffic_risk",
        "time_risk",
        "population_risk",
        "safety_level",
        "weather_risk",
        "risk_zone"
    ]

    return pd.DataFrame(rows, columns=columns)


def train_model(incidents):

    df = create_training_data(incidents)

    if len(df) < 10:

        print(
            "\n⚠️ Not enough incident reports "
            "to train the AI model."
        )

        return None

    print("\nAI TRAINING DATA")
    print("Records:", len(df))
    print("\nRisk distribution:")
    print(df["risk_zone"].value_counts())

    X = df[
        [
            "incident_risk",
            "traffic_risk",
            "time_risk",
            "population_risk",
            "safety_level",
            "weather_risk"
        ]
    ]

    y = df["risk_zone"]

    # A stratified split is only possible when
    # every class has enough examples.
    try:

        X_train, X_test, y_train, y_test = train_test_split(
            X,
            y,
            test_size=0.25,
            random_state=42,
            stratify=y
        )

    except ValueError:

        X_train, X_test, y_train, y_test = train_test_split(
            X,
            y,
            test_size=0.25,
            random_state=42
        )

    model = RandomForestClassifier(
        n_estimators=200,
        max_depth=10,
        random_state=42
    )

    model.fit(X_train, y_train)

    predictions = model.predict(X_test)

    accuracy = accuracy_score(
        y_test,
        predictions
    )

    print("\nAI MODEL RESULT")
    print(
        "Accuracy:",
        round(accuracy * 100, 2),
        "%"
    )

    print(
        classification_report(
            y_test,
            predictions,
            zero_division=0
        )
    )

    joblib.dump(
        model,
        MODEL_FILE
    )

    print(
        "Model saved:",
        MODEL_FILE
    )

    return model
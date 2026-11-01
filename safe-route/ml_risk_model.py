import os
import joblib
import random

import numpy as np
import pandas as pd

from sklearn.ensemble import RandomForestClassifier


# =====================================================
# MODEL CONFIGURATION
# =====================================================

MODEL_FILE = "risk_model.pkl"


FEATURE_COLUMNS = [
    "traffic_risk",
    "time_risk",
    "safety_level",
    "weather_risk"
]


# =====================================================
# TRAINING DATA
# =====================================================

def create_training_data():

    random.seed(
        42
    )


    rows = []


    for _ in range(5000):

        traffic_risk = random.uniform(
            0,
            1
        )


        time_risk = random.uniform(
            0,
            1
        )


        safety_level = random.uniform(
            0,
            1
        )


        weather_risk = random.uniform(
            0,
            1
        )


        # =============================================
        # TRANSPARENT RISK FORMULA
        # =============================================

        score = (

            traffic_risk * 30

            +

            time_risk * 25

            +

            (1 - safety_level) * 30

            +

            weather_risk * 15

        )


        # =============================================
        # RISK CLASS
        # =============================================

        if score >= 75:

            risk_zone = (
                "CRIME_HOTSPOT"
            )

        elif score >= 55:

            risk_zone = (
                "HIGH_RISK"
            )

        elif score >= 30:

            risk_zone = (
                "MODERATE_RISK"
            )

        else:

            risk_zone = (
                "SAFE_ZONE"
            )


        rows.append(
            [
                traffic_risk,
                time_risk,
                safety_level,
                weather_risk,
                risk_zone
            ]
        )


    return pd.DataFrame(
        rows,
        columns=[
            "traffic_risk",
            "time_risk",
            "safety_level",
            "weather_risk",
            "risk_zone"
        ]
    )


# =====================================================
# TRAIN MODEL
# =====================================================

def train_model():

    print(
        "\n==================================="
    )

    print(
        "TRAINING AI RISK MODEL"
    )

    print(
        "==================================="
    )


    df = create_training_data()


    print(
        "Training records:",
        len(df)
    )


    print(
        "\nRisk distribution:"
    )


    print(
        df["risk_zone"].value_counts()
    )


    X = df[
        FEATURE_COLUMNS
    ]


    y = df[
        "risk_zone"
    ]


    model = RandomForestClassifier(

        n_estimators=250,

        max_depth=12,

        min_samples_leaf=8,

        random_state=42,

        class_weight="balanced",

        n_jobs=-1

    )


    model.fit(
        X,
        y
    )


    joblib.dump(
        model,
        MODEL_FILE
    )


    print(
        "\n✅ AI model trained successfully."
    )


    print(
        "Risk classes:",
        list(
            model.classes_
        )
    )


    print(
        "Model saved:",
        MODEL_FILE
    )


    return model


# =====================================================
# LOAD MODEL
# =====================================================

def load_model():

    if not os.path.exists(
        MODEL_FILE
    ):

        return train_model()


    try:

        model = joblib.load(
            MODEL_FILE
        )


        print(
            "✅ AI risk model loaded."
        )


        return model


    except Exception as error:

        print(
            "❌ Unable to load AI model:"
        )


        print(
            error
        )


        return train_model()


# =====================================================
# CALCULATE TRANSPARENT RISK SCORE
# =====================================================

def calculate_risk_score(
    traffic_risk,
    time_risk,
    safety_level,
    weather_risk
):

    score = (

        traffic_risk * 30

        +

        time_risk * 25

        +

        (1 - safety_level) * 30

        +

        weather_risk * 15

    )


    score = max(
        0,
        min(
            score,
            100
        )
    )


    return float(
        score
    )


# =====================================================
# CLASSIFY RISK SCORE
# =====================================================

def classify_risk_score(
    risk_score
):

    if risk_score >= 75:

        return (
            "CRIME_HOTSPOT"
        )


    if risk_score >= 55:

        return (
            "HIGH_RISK"
        )


    if risk_score >= 30:

        return (
            "MODERATE_RISK"
        )


    return (
        "SAFE_ZONE"
    )


# =====================================================
# PREDICT SINGLE ROAD SEGMENT
# =====================================================

def predict_segment_risk(
    model,
    traffic_risk,
    time_risk,
    safety_level,
    weather_risk
):

    if model is None:

        return (
            "UNKNOWN",
            0,
            0
        )


    features = pd.DataFrame(
        [[
            traffic_risk,
            time_risk,
            safety_level,
            weather_risk
        ]],
        columns=FEATURE_COLUMNS
    )


    prediction = model.predict(
        features
    )[0]


    probabilities = model.predict_proba(
        features
    )[0]


    confidence = float(
        max(
            probabilities
        )
    )


    risk_score = calculate_risk_score(
        traffic_risk,
        time_risk,
        safety_level,
        weather_risk
    )


    # Use transparent score classification
    prediction = classify_risk_score(
        risk_score
    )


    return (
        prediction,
        risk_score,
        confidence
    )


# =====================================================
# APPLY AI TO ENTIRE ROAD NETWORK
# =====================================================

def apply_ml_risk_to_graph(
    graph,
    model,
    time_risk,
    weather_risk
):

    if model is None:

        print(
            "⚠️ AI model unavailable."
        )

        return graph


    print(
        "\nApplying AI risk prediction "
        "to road segments..."
    )


    # =============================================
    # COLLECT EDGES
    # =============================================

    edges = list(
        graph.edges(
            keys=True,
            data=True
        )
    )


    edge_count = len(
        edges
    )


    print(
        "AI prediction edges:",
        edge_count
    )


    if edge_count == 0:

        return graph


    # =============================================
    # BUILD FEATURE MATRIX
    # =============================================

    features = np.empty(
        (
            edge_count,
            4
        ),
        dtype=np.float32
    )


    # Keep edge references
    edge_references = []


    for index, (
        u,
        v,
        key,
        data
    ) in enumerate(edges):


        # =========================================
        # TRAFFIC RISK
        # =========================================

        traffic_penalty = float(
            data.get(
                "traffic_penalty",
                0
            ) or 0
        )


        traffic_risk = min(
            traffic_penalty,
            1
        )


        # =========================================
        # SAFETY LEVEL
        # =========================================

        safety_score = float(
            data.get(
                "safety_score",
                0
            ) or 0
        )


        safety_level = min(
            safety_score / 3,
            1
        )


        # =========================================
        # FEATURE ROW
        # =========================================

        features[
            index,
            0
        ] = traffic_risk


        features[
            index,
            1
        ] = time_risk


        features[
            index,
            2
        ] = safety_level


        features[
            index,
            3
        ] = weather_risk


        edge_references.append(
            (
                u,
                v,
                key,
                traffic_risk,
                safety_level
            )
        )


    # =============================================
    # CREATE ONE DATAFRAME
    # =============================================

    feature_dataframe = pd.DataFrame(
        features,
        columns=FEATURE_COLUMNS
    )


    print(
        "Running batch AI prediction..."
    )


    # =============================================
    # ONE BATCH PREDICTION
    # =============================================

    predictions = model.predict(
        feature_dataframe
    )


    # =============================================
    # ONE BATCH PROBABILITY PREDICTION
    # =============================================

    probabilities = model.predict_proba(
        feature_dataframe
    )


    confidence_values = np.max(
        probabilities,
        axis=1
    )


    # =============================================
    # CALCULATE TRANSPARENT RISK SCORES
    # =============================================
    traffic_values = (
        feature_dataframe[
            "traffic_risk"
        ].to_numpy()
    )


    time_values = (
        feature_dataframe[
            "time_risk"
        ].to_numpy()
    )


    safety_values = (
        feature_dataframe[
            "safety_level"
        ].to_numpy()
    )


    weather_values = (
        feature_dataframe[
            "weather_risk"
        ].to_numpy()
    )


    risk_scores = (

        traffic_values * 30

        +

        time_values * 25

        +

        (1 - safety_values) * 30

        +

        weather_values * 15

    )


    risk_scores = np.clip(
        risk_scores,
        0,
        100
    )


    # =============================================
    # USE TRANSPARENT RISK CLASSIFICATION
    # =============================================

    risk_zones = np.select(

        [
            risk_scores >= 75,

            risk_scores >= 55,

            risk_scores >= 30
        ],

        [
            "CRIME_HOTSPOT",

            "HIGH_RISK",

            "MODERATE_RISK"
        ],

        default="SAFE_ZONE"
    )


    # =============================================
    # WRITE RESULTS BACK TO GRAPH
    # =============================================

    for index, (
        u,
        v,
        key,
        traffic_risk,
        safety_level
    ) in enumerate(
        edge_references
    ):


        data = graph[
            u
        ][
            v
        ][
            key
        ]


        data[
            "ml_risk_zone"
        ] = str(
            risk_zones[
                index
            ]
        )


        data[
            "ml_risk_score"
        ] = float(
            risk_scores[
                index
            ]
        )


        data[
            "ml_confidence"
        ] = float(
            confidence_values[
                index
            ]
        )


    print(
        "✅ AI risk prediction applied."
    )


    print(
        "AI predictions completed:",
        edge_count
    )


    return graph
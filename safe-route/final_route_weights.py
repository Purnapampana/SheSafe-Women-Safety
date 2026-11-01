# ============================================================
# FINAL ROUTE WEIGHTS
# ============================================================

def apply_final_weights(graph):

    for u, v, key, data in graph.edges(
        keys=True,
        data=True
    ):

        # ----------------------------------------------------
        # Distance
        # ----------------------------------------------------

        distance = data.get(
            "length",
            1
        )

        try:
            distance = float(
                distance
            )
        except (
            TypeError,
            ValueError
        ):
            distance = 1.0

        # ----------------------------------------------------
        # Traffic
        # ----------------------------------------------------

        traffic_penalty = data.get(
            "traffic_penalty",
            0
        )

        try:
            traffic_penalty = float(
                traffic_penalty
            )
        except (
            TypeError,
            ValueError
        ):
            traffic_penalty = 0.0

        # ----------------------------------------------------
        # OSM incident risk
        # ----------------------------------------------------

        incident_risk = data.get(
            "incident_risk",
            0
        )

        try:
            incident_risk = float(
                incident_risk
            )
        except (
            TypeError,
            ValueError
        ):
            incident_risk = 0.0

        # ----------------------------------------------------
        # OSM safety score
        # ----------------------------------------------------

        safety_score = data.get(
            "safety_score",
            0
        )

        try:
            safety_score = float(
                safety_score
            )
        except (
            TypeError,
            ValueError
        ):
            safety_score = 0.0

        # ----------------------------------------------------
        # ML risk
        # ----------------------------------------------------

        ml_risk_score = data.get(
            "ml_risk_score",
            0
        )

        try:
            ml_risk_score = float(
                ml_risk_score
            )
        except (
            TypeError,
            ValueError
        ):
            ml_risk_score = 0.0

        ml_risk = min(
            max(
                ml_risk_score / 100.0,
                0.0
            ),
            1.0
        )

        # ----------------------------------------------------
        # Historical crime risk
        # ----------------------------------------------------

        crime_risk = data.get(
            "crime_risk",
            0
        )

        try:
            crime_risk = float(
                crime_risk
            )
        except (
            TypeError,
            ValueError
        ):
            crime_risk = 0.0

        crime_risk = min(
            max(
                crime_risk,
                0.0
            ),
            1.0
        )

        # ----------------------------------------------------
        # Block extremely dangerous traffic edges
        # ----------------------------------------------------

        if traffic_penalty >= 999:

            data[
                "final_routing_weight"
            ] = float("inf")

            continue

        # ----------------------------------------------------
        # Traffic factor
        # ----------------------------------------------------

        traffic_factor = (
            1 +
            traffic_penalty
        )

        # ----------------------------------------------------
        # Incident factor
        # ----------------------------------------------------

        incident_factor = (
            1 +
            incident_risk
        )

        # ----------------------------------------------------
        # ML factor
        # ----------------------------------------------------

        ml_risk_factor = (
            1 +
            (
                ml_risk *
                1.5
            )
        )

        # ----------------------------------------------------
        # Crime factor
        # ----------------------------------------------------

        crime_risk_factor = (
            1 +
            (
                crime_risk *
                1.5
            )
        )

        # ----------------------------------------------------
        # Safety bonus
        # ----------------------------------------------------

        safety_bonus = min(
            safety_score * 0.15,
            0.45
        )

        safety_factor = max(
            1 - safety_bonus,
            0.55
        )

        # ----------------------------------------------------
        # FINAL WEIGHT
        # ----------------------------------------------------

        final_weight = (

            distance

            * traffic_factor

            * incident_factor

            * ml_risk_factor

            * crime_risk_factor

            * safety_factor
        )

        data[
            "final_routing_weight"
        ] = final_weight

    return graph
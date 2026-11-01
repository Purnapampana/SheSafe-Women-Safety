def calculate_safety_score(
    category
):

    # Higher = safer
    scores = {

        "STREETLIGHT": 0.8,

        "POLICE": 1.0,

        "HOSPITAL": 0.7,

        "BUS_STATION": 0.6,

        "PUBLIC_TRANSPORT": 0.6,

        "SCHOOL": 0.5,

        "MARKET": 0.7,

        "RESTAURANT": 0.5,

        "HOTEL": 0.4

    }

    return scores.get(
        category,
        0.0
    )


# =====================================================
# APPLY SAFETY WEIGHTS
# =====================================================

def apply_safety_weights(
    graph,
    matched_objects
):

    safety_scores = {}

    for obj in matched_objects:

        edge_key = (
            obj["u"],
            obj["v"],
            obj["key"]
        )

        score = calculate_safety_score(
            obj["category"]
        )

        if edge_key not in safety_scores:

            safety_scores[
                edge_key
            ] = score

        else:

            safety_scores[
                edge_key
            ] += score


    # Apply scores to roads

    for edge_key, safety_score in (
        safety_scores.items()
    ):

        u, v, key = edge_key

        if not graph.has_edge(
            u,
            v,
            key
        ):
            continue

        edge_data = graph[
            u
        ][
            v
        ][
            key
        ]

        # Keep score reasonable
        safety_score = min(
            safety_score,
            3.0
        )

        edge_data[
            "safety_score"
        ] = safety_score


    # Roads with no safety objects

    for u, v, key, data in graph.edges(
        keys=True,
        data=True
    ):

        if "safety_score" not in data:

            data[
                "safety_score"
            ] = 0.0


    return graph
import math


def apply_traffic_weights(
    graph,
    matched_edges
):

    # Store traffic penalty for every affected edge
    traffic_penalties = {}

    for incident in matched_edges:

        edge_key = (
            incident["u"],
            incident["v"],
            incident["key"]
        )

        penalty = incident.get(
            "traffic_penalty",
            0
        )

        # If multiple incidents affect
        # the same road, keep the highest penalty
        if edge_key not in traffic_penalties:

            traffic_penalties[edge_key] = penalty

        else:

            traffic_penalties[edge_key] = max(
                traffic_penalties[edge_key],
                penalty
            )

    # Apply penalties to OSM edges
    for edge_key, penalty in traffic_penalties.items():

        u, v, key = edge_key

        if not graph.has_edge(u, v, key):

            continue

        edge_data = graph[u][v][key]

        original_length = edge_data.get(
            "length",
            1
        )

        # Closed road
        if penalty >= 999:

            edge_data["traffic_penalty"] = 999

            edge_data["routing_weight"] = math.inf

        else:

            edge_data["traffic_penalty"] = penalty

            # Traffic affects the routing cost
            edge_data["routing_weight"] = (
                original_length
                * (1 + penalty)
            )

    # Give unaffected roads their normal weight
    for u, v, key, data in graph.edges(
        keys=True,
        data=True
    ):

        if "routing_weight" not in data:

            data["traffic_penalty"] = 0

            data["routing_weight"] = data.get(
                "length",
                1
            )

    return graph
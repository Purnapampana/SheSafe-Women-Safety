import math
import osmnx as ox


def calculate_distance(
    lat1,
    lon1,
    lat2,
    lon2
):
    """
    Approximate distance between two coordinates in meters.
    """

    earth_radius = 6371000

    lat1 = math.radians(lat1)
    lat2 = math.radians(lat2)

    delta_lat = math.radians(lat2 - lat1)
    delta_lon = math.radians(lon2 - lon1)

    a = (
        math.sin(delta_lat / 2) ** 2
        +
        math.cos(lat1)
        * math.cos(lat2)
        * math.sin(delta_lon / 2) ** 2
    )

    c = 2 * math.atan2(
        math.sqrt(a),
        math.sqrt(1 - a)
    )

    return earth_radius * c


def apply_incident_weights(
    graph,
    matched_incidents
):

    # --------------------------------
    # Convert incident data
    # --------------------------------

    incidents = []

    for incident in matched_incidents:

        latitude = incident.get("latitude")
        longitude = incident.get("longitude")
        risk = incident.get("risk", 0.5)

        if latitude is None or longitude is None:
            continue

        incidents.append({
            "latitude": float(latitude),
            "longitude": float(longitude),
            "risk": float(risk)
        })

    print(
        "Applying proximity-based incident risk..."
    )

    # --------------------------------
    # Calculate risk for every road
    # --------------------------------

    for u, v, key, data in graph.edges(
        keys=True,
        data=True
    ):

        # Get road midpoint
        geometry = data.get("geometry")

        if geometry is not None:

            midpoint = geometry.interpolate(
                geometry.length / 2
            )

            road_lon = midpoint.x
            road_lat = midpoint.y

        else:

            node_u = graph.nodes[u]
            node_v = graph.nodes[v]

            road_lat = (
                node_u["y"]
                + node_v["y"]
            ) / 2

            road_lon = (
                node_u["x"]
                + node_v["x"]
            ) / 2

        total_risk = 0

        # --------------------------------
        # Check nearby incidents
        # --------------------------------

        for incident in incidents:

            distance = calculate_distance(
                road_lat,
                road_lon,
                incident["latitude"],
                incident["longitude"]
            )

            # Incident influence radius
            if distance <= 500:

                # Closer = stronger risk
                influence = (
                    1
                    -
                    distance / 500
                )

                risk = (
                    incident["risk"]
                    * influence
                )

                total_risk += risk

        # --------------------------------
        # Limit maximum risk
        # --------------------------------

        total_risk = min(
            total_risk,
            1.0
        )

        data["incident_risk"] = total_risk

    return graph
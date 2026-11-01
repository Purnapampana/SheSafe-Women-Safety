import osmnx as ox


def extract_coordinates(geometry):

    if not geometry:
        return None

    coordinates = geometry.get("coordinates")

    if not coordinates:
        return None

    # [longitude, latitude]
    if (
        isinstance(coordinates, list)
        and len(coordinates) >= 2
        and isinstance(coordinates[0], (int, float))
        and isinstance(coordinates[1], (int, float))
    ):
        return (
            float(coordinates[0]),
            float(coordinates[1])
        )

    # [[longitude, latitude], ...]
    if (
        isinstance(coordinates, list)
        and len(coordinates) > 0
        and isinstance(coordinates[0], list)
    ):

        point = coordinates[0]

        if (
            len(point) >= 2
            and isinstance(point[0], (int, float))
            and isinstance(point[1], (int, float))
        ):
            return (
                float(point[0]),
                float(point[1])
            )

    return None


def match_traffic_to_edges(
    graph,
    traffic_incidents
):

    matched_edges = []

    for incident in traffic_incidents:

        coordinates = extract_coordinates(
            incident.get("geometry")
        )

        if coordinates is None:

            print(
                "⚠️ Could not extract coordinates:",
                incident.get("id")
            )

            continue

        longitude, latitude = coordinates

        try:

            # Find nearest OSM road edge
            edge = ox.distance.nearest_edges(
                graph,
                X=longitude,
                Y=latitude
            )

            u, v, key = edge

            matched_edges.append({

                "incident_id":
                    incident.get("id"),

                "latitude":
                    latitude,

                "longitude":
                    longitude,

                "u":
                    u,

                "v":
                    v,

                "key":
                    key,

                "delay":
                    incident.get("delay"),

                "magnitude":
                    incident.get("magnitude"),

                "icon_category":
                    incident.get(
                        "icon_category"
                    ),

                "events":
                    incident.get(
                        "events",
                        []
                    )
            })

        except Exception as error:

            print(
                "Unable to match traffic to edge:",
                error
            )

    return matched_edges


def calculate_traffic_penalty(
    incident
):

    magnitude = incident.get(
        "magnitude",
        0
    )

    events = incident.get(
        "events",
        []
    )

    descriptions = [
        event.get(
            "description",
            ""
        ).lower()
        for event in events
    ]

    # Completely closed road
    if any(
        "closed" in description
        for description in descriptions
    ):
        return 999

    # Heavy traffic
    if magnitude >= 4:
        return 0.9

    # Stationary / heavy traffic
    if magnitude == 3:
        return 0.7

    # Moderate traffic
    if magnitude == 2:
        return 0.5

    # Slow traffic
    if magnitude == 1:
        return 0.2

    # Roadworks
    if any(
        "roadworks" in description
        for description in descriptions
    ):
        return 0.3

    return 0.0


if __name__ == "__main__":

    from route_network import get_road_network

    from tomtom_traffic import (
        get_traffic,
        analyse_traffic
    )

    # Test route
    start_lat = 16.5062
    start_lon = 80.6480

    destination_lat = 16.5193
    destination_lon = 80.6305

    print("\n==============================")
    print("TRAFFIC → OSM EDGE TEST")
    print("==============================")

    # --------------------------------
    # 1. Download OSM road network
    # --------------------------------

    print(
        "\n1. Downloading road network..."
    )

    graph = get_road_network(
        start_lat,
        start_lon,
        distance=1500,
        travel_mode="drive"
    )

    print(
        "OSM road network ready."
    )

    # --------------------------------
    # 2. Get TomTom traffic
    # --------------------------------

    print(
        "\n2. Getting TomTom traffic..."
    )

    traffic_data = get_traffic(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )

    if traffic_data is None:

        print(
            "❌ Unable to get TomTom traffic."
        )

        exit()

    traffic_incidents = analyse_traffic(
        traffic_data
    )

    print(
        "TomTom incidents:",
        len(traffic_incidents)
    )

    # --------------------------------
    # 3. Match traffic to OSM edges
    # --------------------------------

    print(
        "\n3. Matching traffic to road edges..."
    )

    matched_edges = match_traffic_to_edges(
        graph,
        traffic_incidents
    )

    print(
        "Matched road edges:",
        len(matched_edges)
    )

    # --------------------------------
    # 4. Display results
    # --------------------------------

    print(
        "\n4. Traffic analysis..."
    )

    for road in matched_edges:

        penalty = calculate_traffic_penalty(
            road
        )
        road["traffic_penalty"] = penalty

        print(
            "\nIncident:",
            road["incident_id"]
        )

        print(
            "Traffic location:",
            road["latitude"],
            road["longitude"]
        )

        print(
            "OSM edge:"
        )

        print(
            "  From:",
            road["u"]
        )

        print(
            "  To:",
            road["v"]
        )

        print(
            "  Key:",
            road["key"]
        )

        print(
            "Magnitude:",
            road["magnitude"]
        )

        print(
            "Delay:",
            road["delay"]
        )

        print(
            "Events:",
            road["events"]
        )

        print(
            "Traffic penalty:",
            penalty
        )

    print(
        "\n✅ Traffic → OSM edge matching completed."
    )
import osmnx as ox


def match_safety_to_edges(graph, safety_objects):

    matched_objects = []

    valid_objects = []

    for obj in safety_objects:

        latitude = obj.get("latitude")
        longitude = obj.get("longitude")

        if latitude is None or longitude is None:
            continue

        valid_objects.append(obj)

    if not valid_objects:
        return matched_objects

    print(
        "Matching",
        len(valid_objects),
        "safety objects to road network..."
    )

    # Extract coordinates
    X = [
        float(obj["longitude"])
        for obj in valid_objects
    ]

    Y = [
        float(obj["latitude"])
        for obj in valid_objects
    ]

    try:

        # Match all objects at once
        edges = ox.distance.nearest_edges(
            graph,
            X=X,
            Y=Y
        )

    except Exception as error:

        print(
            "❌ Unable to match safety objects:",
            error
        )

        return matched_objects

    # Build matched objects
    for obj, edge in zip(
        valid_objects,
        edges
    ):

        try:

            u, v, key = edge

            matched_objects.append({
                "osm_id": obj.get("osm_id"),
                "category": obj.get("category"),
                "name": obj.get(
                    "name",
                    "Unknown"
                ),
                "latitude": float(
                    obj["latitude"]
                ),
                "longitude": float(
                    obj["longitude"]
                ),
                "u": u,
                "v": v,
                "key": key
            })

        except Exception as error:

            print(
                "⚠️ Unable to process safety object:",
                error
            )

    return matched_objects
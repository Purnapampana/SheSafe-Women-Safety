import osmnx as ox


def match_incidents_to_edges(graph, incidents):

    matched_incidents = []

    for incident in incidents:

        latitude = incident.get("latitude")
        longitude = incident.get("longitude")

        if latitude is None or longitude is None:
            continue

        try:
            edge = ox.distance.nearest_edges(
                graph,
                X=longitude,
                Y=latitude
            )

            u, v, key = edge

            matched_incidents.append({
                "incident_id": incident.get("id"),
                "incident_type": incident.get("incident_type"),
                "risk": incident.get("risk", 0.5),

                "latitude": latitude,
                "longitude": longitude,

                "u": u,
                "v": v,
                "key": key
            })

        except Exception as error:

            print(
                "Unable to match incident to road:",
                error
            )

    return matched_incidents
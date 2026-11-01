import requests


TOMTOM_API_KEY = "BLMGvuvF7320vw1Ois2mZGNEpZtRXHOG"


def get_route_bbox(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon,
    padding=0.05
):
    """
    Create a bounding box covering the complete route area.
    """

    min_lat = min(start_lat, destination_lat) - padding
    max_lat = max(start_lat, destination_lat) + padding

    min_lon = min(start_lon, destination_lon) - padding
    max_lon = max(start_lon, destination_lon) + padding

    return min_lat, min_lon, max_lat, max_lon


def get_traffic(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon
):

    min_lat, min_lon, max_lat, max_lon = get_route_bbox(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )

    bbox = (
        f"{min_lon},{min_lat},"
        f"{max_lon},{max_lat}"
    )

    url = (
        "https://api.tomtom.com/"
        "traffic/services/5/incidentDetails"
    )

    params = {
        "key": TOMTOM_API_KEY,
        "bbox": bbox,
        "fields": "{incidents{"
                  "type,"
                  "geometry{coordinates},"
                  "properties{"
                  "id,"
                  "iconCategory,"
                  "magnitudeOfDelay,"
                  "delay,"
                  "events{description,code}"
                  "}"
                  "}}"
    }

    response = requests.get(
        url,
        params=params,
        timeout=15
    )

    if response.status_code != 200:
        print(
            "TomTom API error:",
            response.status_code
        )
        print(response.text)
        return None

    return response.json()


def analyse_traffic(data):

    if not data:
        return []

    traffic_data = []

    for incident in data.get("incidents", []):

        properties = incident.get(
            "properties",
            {}
        )

        traffic_data.append({

            "id": properties.get("id"),

            "delay": properties.get(
                "delay",
                0
            ),

            "magnitude": properties.get(
                "magnitudeOfDelay",
                0
            ),

            "icon_category": properties.get(
                "iconCategory"
            ),

            "events": properties.get(
                "events",
                []
            ),

            "geometry": incident.get(
                "geometry"
            )
        })

    return traffic_data
if __name__ == "__main__":

    print("Testing TomTom traffic...")

    # Example route only
    # These coordinates will later come from the frontend.

    start_lat = 16.5062
    start_lon = 80.6480

    destination_lat = 16.5193
    destination_lon = 80.6305

    data = get_traffic(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )

    if data is None:
        print("❌ Failed to get traffic data.")
    else:

        traffic = analyse_traffic(data)

        print(
            "✅ TomTom traffic request successful."
        )

        print(
            "Traffic incidents found:",
            len(traffic)
        )

        for incident in traffic:

            print("----------------------------")

            print(
                "Incident ID:",
                incident["id"]
            )

            print(
                "Delay:",
                incident["delay"],
                "seconds"
            )

            print(
                "Magnitude:",
                incident["magnitude"]
            )

            print(
                "Category:",
                incident["icon_category"]
            )

            print(
                "Events:",
                incident["events"]
            )
def create_route_request(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon,
    travel_mode="drive"
):

    if travel_mode not in ["walk", "drive", "bike"]:
        raise ValueError(
            "Travel mode must be walk, drive, or bike."
        )

    return {
        "start": {
            "latitude": start_lat,
            "longitude": start_lon
        },

        "destination": {
            "latitude": destination_lat,
            "longitude": destination_lon
        },

        "travel_mode": travel_mode
    }


if __name__ == "__main__":

    # Example only.
    # These values will later come from the frontend.

    request = create_route_request(
        start_lat=16.5062,
        start_lon=80.6480,

        destination_lat=16.5193,
        destination_lon=80.6305,

        travel_mode="drive"
    )

    print(request)
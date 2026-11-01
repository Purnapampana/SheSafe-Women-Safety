import os
import osmnx as ox


# =====================================================
# OSM SETTINGS
# =====================================================

ox.settings.requests_timeout = 180
ox.settings.requests_retries = 3


# =====================================================
# CACHE DIRECTORY
# =====================================================

GRAPH_CACHE_DIR = os.path.join(
    "data",
    "road_graph_cache"
)


# =====================================================
# CREATE CACHE DIRECTORY
# =====================================================

def ensure_cache_directory():

    os.makedirs(
        GRAPH_CACHE_DIR,
        exist_ok=True
    )


# =====================================================
# CREATE ROUTE BOUNDING BOX
# =====================================================

def get_route_bbox(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon,
    padding=0.02
):

    south = min(
        start_lat,
        destination_lat
    ) - padding

    north = max(
        start_lat,
        destination_lat
    ) + padding

    west = min(
        start_lon,
        destination_lon
    ) - padding

    east = max(
        start_lon,
        destination_lon
    ) + padding

    return (
        south,
        west,
        north,
        east
    )


# =====================================================
# CREATE CACHE FILE NAME
# =====================================================

def get_graph_cache_file(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon,
    travel_mode
):

    ensure_cache_directory()


    south, west, north, east = get_route_bbox(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )


    # Round so tiny GPS variations can reuse cache
    south_key = round(
        south,
        2
    )

    west_key = round(
        west,
        2
    )

    north_key = round(
        north,
        2
    )

    east_key = round(
        east,
        2
    )


    filename = (
        f"road_"
        f"{south_key}_"
        f"{west_key}_"
        f"{north_key}_"
        f"{east_key}_"
        f"{travel_mode}.graphml"
    )


    return os.path.join(
        GRAPH_CACHE_DIR,
        filename
    )


# =====================================================
# VALIDATE TRAVEL MODE
# =====================================================

def get_network_type(
    travel_mode
):

    if travel_mode == "walk":

        return "walk"


    if travel_mode == "drive":

        return "drive"


    if travel_mode == "bike":

        return "bike"


    raise ValueError(
        "Travel mode must be walk, drive, or bike."
    )


# =====================================================
# GET ROAD NETWORK
# =====================================================

def get_road_network(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon,
    travel_mode="drive"
):

    network_type = get_network_type(
        travel_mode
    )


    # =============================================
    # CACHE FILE
    # =============================================

    cache_file = get_graph_cache_file(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon,
        travel_mode
    )


    # =============================================
    # CHECK CACHE
    # =============================================

    if os.path.exists(
        cache_file
    ):

        print(
            "\n✅ Using cached OSM road network."
        )

        print(
            "Cached graph:",
            cache_file
        )


        try:

            graph = ox.load_graphml(
                filepath=cache_file
            )


            print(
                "Road network loaded from cache."
            )

            print(
                "Travel mode:",
                travel_mode
            )

            print(
                "Nodes:",
                len(graph.nodes)
            )

            print(
                "Road segments:",
                len(graph.edges)
            )


            return graph


        except Exception as error:

            print(
                "⚠️ Cached graph could not be loaded."
            )

            print(
                "Reason:",
                error
            )

            print(
                "Downloading a fresh graph..."
            )


    # =============================================
    # DOWNLOAD
    # =============================================

    print(
        "\nDownloading OpenStreetMap road network..."
    )


    # =============================================
    # CREATE BBOX
    # =============================================

    south, west, north, east = get_route_bbox(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )


    # =============================================
    # AREA CHECK
    # =============================================

    latitude_difference = (
        north - south
    )

    longitude_difference = (
        east - west
    )


    area_km2 = (
        latitude_difference
        * 111
        *
        longitude_difference
        * 111
    )


    print(
        "Approximate route area:",
        round(
            area_km2,
            2
        ),
        "km²"
    )


    # =============================================
    # PREVENT HUGE DOWNLOADS
    # =============================================

    if area_km2 > 5000:

        raise ValueError(
            "\n❌ Start and destination are too far apart "
            "for the current local route engine.\n"
            "Please use locations within approximately "
            "the same region for this version."
        )


    # =============================================
    # DOWNLOAD GRAPH
    # =============================================

    graph = ox.graph_from_bbox(
        bbox=(
            west,
            south,
            east,
            north
        ),
        network_type=network_type,
        simplify=True
    )


    print(
        "Road network downloaded."
    )


    print(
        "Travel mode:",
        travel_mode
    )


    print(
        "Nodes:",
        len(graph.nodes)
    )


    print(
        "Road segments:",
        len(graph.edges)
    )


    # =============================================
    # ADD SPEED
    # =============================================

    graph = ox.add_edge_speeds(
        graph
    )


    # =============================================
    # ADD TRAVEL TIME
    # =============================================

    graph = ox.add_edge_travel_times(
        graph
    )


    # =============================================
    # SAVE CACHE
    # =============================================

    try:

        ox.save_graphml(
            graph,
            filepath=cache_file
        )


        print(
            "💾 Road network cached:"
        )

        print(
            cache_file
        )


    except Exception as error:

        print(
            "⚠️ Unable to save road graph cache:"
        )

        print(
            error
        )


    return graph


# =====================================================
# SAVE NETWORK MANUALLY
# =====================================================

def save_network(
    graph,
    travel_mode
):

    ensure_cache_directory()


    filename = (
        f"safe_route_{travel_mode}.graphml"
    )


    filepath = os.path.join(
        GRAPH_CACHE_DIR,
        filename
    )


    ox.save_graphml(
        graph,
        filepath=filepath
    )


    print(
        "Road network saved as:",
        filepath
    )
import time

from flask import Flask, request, jsonify
from flask_cors import CORS

from route_network import get_road_network

from tomtom_traffic import (
    get_traffic,
    analyse_traffic
)

from traffic_road_matcher import (
    match_traffic_to_edges,
    calculate_traffic_penalty
)

from traffic_weighted_graph import (
    apply_traffic_weights
)

from safety_places import (
    get_safety_data,
    process_safety_data
)

from safety_road_matcher import (
    match_safety_to_edges
)

from safety_weighted_graph import (
    apply_safety_weights
)

from weather_data import (
    get_weather
)

from risk_zone import (
    get_time_risk,
    get_weather_risk
)

from ml_risk_model import (
    load_model,
    apply_ml_risk_to_graph
)

from final_route_weights import (
    apply_final_weights
)

from route_optimizer import (
    find_route,
    find_alternative_route,
    calculate_route_distance
)


# =====================================================
# FLASK APPLICATION
# =====================================================

app = Flask(__name__)

CORS(app)


# =====================================================
# HEALTH CHECK
# =====================================================

@app.route("/health", methods=["GET"])
def health():

    return jsonify({
        "status": "UP",
        "service": "SheSafe Safe Route AI"
    })


# =====================================================
# ROUTE TRAFFIC SCORE
# =====================================================

def calculate_route_traffic_score(
        graph,
        route):

    penalties = []

    for u, v in zip(
            route[:-1],
            route[1:]):

        edge_data = graph.get_edge_data(
            u,
            v
        )

        if not edge_data:
            continue

        edge = min(
            edge_data.values(),
            key=lambda x:
                x.get(
                    "final_routing_weight",
                    x.get(
                        "length",
                        0
                    )
                )
        )

        penalty = edge.get(
            "traffic_penalty",
            0
        )

        if penalty >= 999:
            penalty = 1.0

        penalties.append(
            min(
                float(penalty),
                1.0
            )
        )

    if not penalties:
        return 0.0

    return round(
        (
            sum(penalties)
            /
            len(penalties)
        ) * 100,
        1
    )


# =====================================================
# GET ROUTE EDGE KEYS
# =====================================================

def get_route_edge_keys(
        graph,
        route):

    edge_keys = set()

    for u, v in zip(
            route[:-1],
            route[1:]):

        edge_data = graph.get_edge_data(
            u,
            v
        )

        if not edge_data:
            continue

        for key in edge_data.keys():

            edge_keys.add(
                (u, v, key)
            )

    return edge_keys


# =====================================================
# COLLECT SAFETY FEATURES
# =====================================================
# =====================================================
# COLLECT OSM MAP FEATURES
# =====================================================

def collect_route_safety_features(
        matched_safety,
        route,
        graph):

    route_edges = get_route_edge_keys(
        graph,
        route
    )

    map_features = []

    for obj in matched_safety:

        edge_key = (
            obj.get("u"),
            obj.get("v"),
            obj.get("key")
        )

        if edge_key not in route_edges:
            continue

        latitude = obj.get(
            "latitude"
        )

        longitude = obj.get(
            "longitude"
        )

        if latitude is None or \
                longitude is None:

            continue

        category = obj.get(
            "category",
            "OTHER"
        )

        name = obj.get(
            "name",
            "Unknown"
        )

        map_features.append({

            "osmId":
                obj.get("osm_id"),

            "category":
                category,

            "name":
                name,

            "latitude":
                float(latitude),

            "longitude":
                float(longitude)
        })

    return map_features

# =====================================================
# CONVERT ROUTE NODES TO COORDINATES
# =====================================================

def convert_route_to_coordinates(
        graph,
        route):

    coordinates = []

    if not route:
        return coordinates

    for node in route:

        node_data = graph.nodes[node]

        latitude = node_data.get("y")
        longitude = node_data.get("x")

        if latitude is None or \
                longitude is None:

            continue

        coordinates.append([
            float(latitude),
            float(longitude)
        ])

    return coordinates


# =====================================================
# ROUTE AI RISK
# =====================================================

def calculate_route_ai_risk(
        graph,
        route):

    risk_scores = []

    if not route:
        return 0.0, "SAFE_ZONE", []

    for u, v in zip(
            route[:-1],
            route[1:]):

        edge_data = graph.get_edge_data(
            u,
            v
        )

        if not edge_data:
            continue

        edge = min(
            edge_data.values(),
            key=lambda x:
                x.get(
                    "final_routing_weight",
                    x.get(
                        "length",
                        0
                    )
                )
        )

        risk = edge.get(
            "ml_risk_score",
            0
        )

        try:
            risk = float(risk)
        except (
                TypeError,
                ValueError):

            risk = 0.0

        risk_scores.append(
            risk
        )

    if not risk_scores:

        return (
            0.0,
            "SAFE_ZONE",
            []
        )

    average_risk = (
        sum(risk_scores)
        /
        len(risk_scores)
    )

    average_risk = round(
        average_risk,
        2
    )

    if average_risk >= 75:

        risk_zone = "CRIME_HOTSPOT"

    elif average_risk >= 50:

        risk_zone = "HIGH_RISK"

    elif average_risk >= 25:

        risk_zone = "MODERATE_RISK"

    else:

        risk_zone = "SAFE_ZONE"

    return (
        average_risk,
        risk_zone,
        risk_scores
    )


# =====================================================
# BUILD ROUTE RESPONSE OBJECT
# =====================================================

# =====================================================
# BUILD ROUTE RESPONSE OBJECT
# =====================================================

def build_route_response(
        graph,
        route,
        route_name,
        matched_safety):

    if not route:
        return None

    # ---------------------------------------------
    # DISTANCE / DURATION
    # ---------------------------------------------

    distance, duration = (
        calculate_route_distance(
            graph,
            route
        )
    )

    # ---------------------------------------------
    # COORDINATES
    # ---------------------------------------------

    coordinates = (
        convert_route_to_coordinates(
            graph,
            route
        )
    )

    # ---------------------------------------------
    # AI RISK
    # ---------------------------------------------

    risk_score, risk_zone, risk_points = (
        calculate_route_ai_risk(
            graph,
            route
        )
    )

    # ---------------------------------------------
    # TRAFFIC
    # ---------------------------------------------

    traffic_score = (
        calculate_route_traffic_score(
            graph,
            route
        )
    )

    # ---------------------------------------------
    # TRAFFIC LEVEL
    # ---------------------------------------------

    if traffic_score < 30:

        traffic_level = "LOW"

    elif traffic_score < 60:

        traffic_level = "MEDIUM"

    else:

        traffic_level = "HIGH"

    # ---------------------------------------------
    # OSM MAP FEATURES
    # ---------------------------------------------
    #
    # Hotels, restaurants, hospitals,
    # police, schools, markets,
    # street lights, bus stations, etc.
    #
    # These are displayed as icons on the map.
    #
    # They are NOT displayed as numbers.
    #
    # ---------------------------------------------

    map_features = (
        collect_route_safety_features(
            matched_safety,
            route,
            graph
        )
    )

    # ---------------------------------------------
    # RESPONSE
    # ---------------------------------------------

    return {

        "routeName":
            route_name,

        "coordinates":
            coordinates,

        "distanceKm":
            round(
                distance / 1000,
                2
            ),

        "durationMinutes":
            round(
                duration / 60,
                1
            ),

        "riskScore":
            risk_score,

        "riskZone":
            risk_zone,

        "riskPoints":
            risk_points,

        "trafficScore":
            traffic_score,

        "trafficLevel":
            traffic_level,

        "mapFeatures":
            map_features
    }
    


# =====================================================
# SAFE ROUTE API
# =====================================================

@app.route(
    "/safe-route",
    methods=["POST"]
)
def safe_route():
    overall_start = time.time()

    try:

        data = request.get_json()

        if not data:

            return jsonify({
                "status": "error",
                "message":
                    "Request body is required."
            }), 400


        # =================================================
        # READ COORDINATES
        # =================================================

        if (
            data.get("startLatitude")
            is None
            or
            data.get("startLongitude")
            is None
            or
            data.get("destinationLatitude")
            is None
            or
            data.get("destinationLongitude")
            is None
        ):

            return jsonify({

                "status": "error",

                "message":
                    "Start and destination coordinates are required."
            }), 400


        start_lat = float(
            data["startLatitude"]
        )

        start_lon = float(
            data["startLongitude"]
        )

        destination_lat = float(
            data["destinationLatitude"]
        )

        destination_lon = float(
            data["destinationLongitude"]
        )


        print()
        print(
            "==================================="
        )
        print(
            "SHESAFE SAFE ROUTE API"
        )
        print(
            "==================================="
        )

        print(
            "Start:",
            start_lat,
            start_lon
        )

        print(
            "Destination:",
            destination_lat,
            destination_lon
        )


        # =================================================
        # 1. ROAD NETWORK
        # =================================================

        print()
        print(
            "[1] Loading road network..."
        )

        graph = get_road_network(

            start_lat,
            start_lon,

            destination_lat,
            destination_lon,

            travel_mode="drive"
        )
        print(
    f"⏱️ Road graph completed in "
    f"{time.time() - overall_start:.2f} seconds"
)


        # =================================================
        # 2. TRAFFIC
        # =================================================

        print()
        print(
            "[2] Getting traffic..."
        )

        traffic_data = get_traffic(

            start_lat,
            start_lon,

            destination_lat,
            destination_lon
        )

        traffic_incidents = (
            analyse_traffic(
                traffic_data
            )
        )

        print(
            "Traffic incidents:",
            len(traffic_incidents)
        )


        # =================================================
        # 3. MATCH TRAFFIC
        # =================================================

        print()
        print(
            "[3] Matching traffic..."
        )

        matched_traffic = (
            match_traffic_to_edges(

                graph,

                traffic_incidents
            )
        )

        print(
            "Traffic road matches:",
            len(matched_traffic)
        )


        # =================================================
        # 4. TRAFFIC WEIGHTS
        # =================================================

        print()
        print(
            "[4] Applying traffic weights..."
        )

        for incident in matched_traffic:

            incident[
                "traffic_penalty"
            ] = calculate_traffic_penalty(
                incident
            )


        graph = apply_traffic_weights(

            graph,

            matched_traffic
        )
        print(
    f"⏱️ Traffic processing completed in "
    f"{time.time() - overall_start:.2f} seconds"
)


        # =================================================
        # 5. OSM SAFETY DATA
        # =================================================

        print()
        print(
            "[5] Getting OSM safety data..."
        )

        safety_elements = (
            get_safety_data(

                start_lat,
                start_lon,

                destination_lat,
                destination_lon
            )
        )


        if not safety_elements:

            return jsonify({

                "status": "error",

                "message":
                    "Safety data unavailable."
            }), 503


        safety_objects = (
            process_safety_data(
                safety_elements
            )
        )


        print(
            "Safety objects:",
            len(safety_objects)
        )


        # =================================================
        # 6. MATCH SAFETY DATA
        # =================================================

        print()
        print(
            "[6] Matching safety features..."
        )

        matched_safety = (
            match_safety_to_edges(

                graph,

                safety_objects
            )
        )

        print(
            "Safety road matches:",
            len(matched_safety)
        )


        # =================================================
        # 7. SAFETY WEIGHTS
        # =================================================

        print()
        print(
            "[7] Applying safety weights..."
        )

        graph = apply_safety_weights(

            graph,

            matched_safety
        )
        print(
    f"⏱️ OSM safety processing completed in "
    f"{time.time() - overall_start:.2f} seconds"
)


        # =================================================
        # 8. WEATHER
        # =================================================

        print()
        print(
            "[8] Getting live weather..."
        )

        weather = get_weather(

            start_lat,
            start_lon
        )


        weather_condition = (
            weather.get(
                "condition",
                "Unknown"
            )
        )


        weather_risk = (
            get_weather_risk(
                weather_condition
            )
        )


        print(
            "Weather:",
            weather_condition
        )

        print(
            "Temperature:",
            weather.get(
                "temperature"
            )
        )

        print(
            "Precipitation:",
            weather.get(
                "precipitation"
            )
        )

        print(
            "Weather risk:",
            weather_risk
        )


        # =================================================
        # 9. TIME RISK
        # =================================================

        print()
        print(
            "[9] Calculating time risk..."
        )

        time_risk = get_time_risk()

        print(
            "Time risk:",
            time_risk
        )


        # =================================================
        # 10. AI MODEL
        # =================================================

        print()
        print(
            "[10] Applying AI risk model..."
        )

        ml_model = load_model()


        graph = apply_ml_risk_to_graph(

            graph,

            ml_model,

            time_risk,

            weather_risk
        )
        print(
    f"⏱️ OSM safety processing completed in "
    f"{time.time() - overall_start:.2f} seconds"
)


        # =================================================
        # 11. FINAL ROUTE WEIGHTS
        # =================================================

        print()
        print(
            "[11] Applying final route weights..."
        )

        graph = apply_final_weights(
            graph
        )
        print(
    f"⏱️ TOTAL SAFE ROUTE TIME: "
    f"{time.time() - overall_start:.2f} seconds"
)


        # =================================================
        # 12. SAFEST ROUTE
        # =================================================

        print()
        print(
            "[12] Calculating safest route..."
        )

        safest_route = find_route(

            graph,

            start_lat,
            start_lon,

            destination_lat,
            destination_lon
        )


        # =================================================
        # 13. ALTERNATIVE ROUTE
        # =================================================

        print()
        print(
            "[13] Calculating alternative route..."
        )

        alternative_route = (
            find_alternative_route(

                graph,

                start_lat,
                start_lon,

                destination_lat,
                destination_lon,

                safest_route
            )
        )


        # =================================================
        # 14. BUILD ROUTE RESPONSES
        # =================================================

        print()
        print(
            "[14] Preparing route results..."
        )

        safest_route_data = (
            build_route_response(

                graph,

                safest_route,

                "Safest Route",

                matched_safety
            )
        )


        alternative_route_data = None


        if alternative_route:

            alternative_route_data = (
                build_route_response(

                    graph,

                    alternative_route,

                    "Alternative Route",

                    matched_safety
                )
            )


        # =================================================
        # 15. OVERALL AI RISK
        # =================================================

        overall_risk = (
            safest_route_data[
                "riskScore"
            ]
        )

        overall_zone = (
            safest_route_data[
                "riskZone"
            ]
        )


        # =================================================
        # 16. FINAL RESPONSE
        # =================================================

        response = {

            "status":
                "success",


            # ---------------------------------------------
            # START
            # ---------------------------------------------

            "start": {

                "latitude":
                    start_lat,

                "longitude":
                    start_lon
            },


            # ---------------------------------------------
            # DESTINATION
            # ---------------------------------------------

            "destination": {

                "latitude":
                    destination_lat,

                "longitude":
                    destination_lon
            },


            # ---------------------------------------------
            # WEATHER
            # ---------------------------------------------

            "weather": {

                "condition":
                    weather.get(
                        "condition"
                    ),

                "temperature":
                    weather.get(
                        "temperature"
                    ),

                "precipitation":
                    weather.get(
                        "precipitation"
                    )
            },


            # ---------------------------------------------
            # AI RISK
            # ---------------------------------------------

            "aiRisk": {

                "score":
                    overall_risk,

                "zone":
                    overall_zone
            },


            # ---------------------------------------------
            # SAFEST ROUTE
            # ---------------------------------------------

            "safestRoute":
                safest_route_data,


            # ---------------------------------------------
            # ALTERNATIVE ROUTE
            # ---------------------------------------------

            "alternativeRoute":
    alternative_route_data
    if alternative_route_data
    else {

        "routeName":
            "Alternative Route",

        "coordinates":
            [],

        "distanceKm":
            0,

        "durationMinutes":
            0,

        "riskScore":
            0,

        "riskZone":
            "UNKNOWN",

        "riskPoints":
            [],

        "trafficScore":
            0,

        "trafficLevel":
            "LOW",

        "mapFeatures":
            []
    }
        }


        print()
        print(
            "==================================="
        )
        print(
            "SAFE ROUTE CALCULATION COMPLETE"
        )
        print(
            "==================================="
        )

        print(
            "Safest distance:",
            safest_route_data[
                "distanceKm"
            ],
            "km"
        )

        print(
            "Safest duration:",
            safest_route_data[
                "durationMinutes"
            ],
            "minutes"
        )

        print(
            "Safest AI risk:",
            safest_route_data[
                "riskScore"
            ]
        )

        print(
            "Safest risk zone:",
            safest_route_data[
                "riskZone"
            ]
        )


        if alternative_route_data:

            print(
                "Alternative distance:",
                alternative_route_data[
                    "distanceKm"
                ],
                "km"
            )

            print(
                "Alternative duration:",
                alternative_route_data[
                    "durationMinutes"
                ],
                "minutes"
            )


        return jsonify(
            response
        )


    except Exception as error:

        print()
        print(
            "❌ SAFE ROUTE API ERROR:"
        )

        print(error)

        return jsonify({

            "status":
                "error",

            "message":
                str(error)
        }), 500


# =====================================================
# START SERVER
# =====================================================

if __name__ == "__main__":

    print()
    print(
        "🚀 SheSafe Safe Route API started"
    )

    print(
        "http://localhost:5000"
    )

    app.run(

        host="0.0.0.0",

        port=5000,

        debug=False
    )
import os
import sys
import osmnx as ox

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



from ml_risk_model import (
     load_model,
    apply_ml_risk_to_graph
)



from route_optimizer import (
    find_route,
    find_alternative_route,
    calculate_route_distance
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

from final_route_weights import (
    apply_final_weights
)


from risk_zone import (
    apply_risk_zones,
    get_time_risk,
    get_weather_risk
)
from weather_data import get_weather



# =====================================================
# GET START AND DESTINATION DYNAMICALLY
# =====================================================
def classify_route_risk(score):

    if score >= 75:
        return "CRIME_HOTSPOT"

    elif score >= 50:
        return "HIGH_RISK"

    elif score >= 25:
        return "MODERATE_RISK"

    else:
        return "SAFE_ZONE"
def get_coordinates():

    print("\n===================================")
    print("SHESAFE SAFE ROUTE")
    print("===================================")

    print("\nEnter START location coordinates")

    start_lat = float(
        input("Start latitude: ")
    )

    start_lon = float(
        input("Start longitude: ")
    )

    print("\nEnter DESTINATION location coordinates")

    destination_lat = float(
        input("Destination latitude: ")
    )

    destination_lon = float(
        input("Destination longitude: ")
    )

    return (
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )


# =====================================================
# MAIN
# =====================================================

(
    START_LAT,
    START_LON,
    DESTINATION_LAT,
    DESTINATION_LON
) = get_coordinates()


# =====================================================
# JWT
# =====================================================

TOKEN = os.getenv("SHESAFE_JWT")

if not TOKEN:

    print("\n❌ SHESAFE_JWT not found.")

    print(
        "Set your JWT in PowerShell first:"
    )

    print(
        '$env:SHESAFE_JWT="YOUR_FULL_TOKEN"'
    )

    sys.exit()


# =====================================================
# 1. OSM ROAD NETWORK
# =====================================================

print("\n[1] Loading road network...")

graph = get_road_network(
    START_LAT,
    START_LON,
    DESTINATION_LAT,
    DESTINATION_LON,
    travel_mode="drive"
)

print("✅ Road network ready.")


# =====================================================
# 2. TOMTOM TRAFFIC
# =====================================================

print("\n[2] Getting live traffic...")

traffic_data = get_traffic(
    START_LAT,
    START_LON,
    DESTINATION_LAT,
    DESTINATION_LON
)

traffic_incidents = analyse_traffic(
    traffic_data
)

print(
    "✅ Traffic incidents:",
    len(traffic_incidents)
)


# =====================================================
# 3. MATCH TRAFFIC TO ROADS
# =====================================================

print("\n[3] Matching traffic to roads...")

matched_traffic = match_traffic_to_edges(
    graph,
    traffic_incidents
)

for incident in matched_traffic:

    incident["traffic_penalty"] = (
        calculate_traffic_penalty(
            incident
        )
    )

print(
    "✅ Traffic road matches:",
    len(matched_traffic)
)


# =====================================================
# 4. APPLY TRAFFIC WEIGHTS
# =====================================================

print("\n[4] Applying traffic weights...")

graph = apply_traffic_weights(
    graph,
    matched_traffic
)

print("✅ Traffic weights applied.")


# =====================================================
# 8. GET OSM SAFETY DATA
# =====================================================

print("\n[8] Getting OSM safety data...")

safety_elements = get_safety_data(
    START_LAT,
    START_LON,
    DESTINATION_LAT,
    DESTINATION_LON
)

if not safety_elements:

    print(
        "\n❌ Safety data could not be loaded."
    )

    print(
        "Safe route calculation stopped."
    )

    print(
        "Please run again when the Overpass service is available."
    )

    sys.exit()


safety_objects = process_safety_data(
    safety_elements
)

print(
    "✅ Safety objects:",
    len(safety_objects)
)


# =====================================================
# 9. MATCH SAFETY OBJECTS TO ROADS
# =====================================================

print(
    "\n[9] Matching safety objects to roads..."
)

matched_safety = match_safety_to_edges(
    graph,
    safety_objects
)

print(
    "✅ Safety road matches:",
    len(matched_safety)
)


# =====================================================
# 10. APPLY SAFETY WEIGHTS
# =====================================================

print("\n[10] Applying safety weights...")

graph = apply_safety_weights(
    graph,
    matched_safety
)

print(
    "✅ Safety weights applied."
)
# =====================================================
# 11. CALCULATE RISK ZONES
# =====================================================

print("\n[11] Calculating risk zones...")

# ============================================================
# RISK ZONE PREDICTION
# ============================================================


# ============================================================
# LIVE WEATHER
# ============================================================

weather = get_weather(
    START_LAT,
    START_LON
)

WEATHER_CONDITION = weather["condition"]

print("\n===================================")
print("LIVE WEATHER")
print("===================================")

print(
    "Condition:",
    WEATHER_CONDITION
)

print(
    "Temperature:",
    weather["temperature"],
    "°C"
)

print(
    "Precipitation:",
    weather["precipitation"],
    "mm"
)

graph = apply_risk_zones(
    graph,
    weather_condition=WEATHER_CONDITION
)
# ============================================================
# AI / ML RISK PREDICTION
print("\n==============================")
print("STEP 12: AI RISK PREDICTION")
print("==============================")

from risk_zone import (
    get_time_risk,
    get_weather_risk
)

time_risk = get_time_risk()

weather_risk = get_weather_risk(
    WEATHER_CONDITION
)

print(
    "Time risk:",
    round(time_risk, 2)
)

print(
    "Weather risk:",
    round(weather_risk, 2)
)

ml_model = load_model()

if ml_model is None:

    print(
        "\n⚠️ AI model not found."
    )

    print(
        "Run train_model() once to create it."
    )

else:

    graph = apply_ml_risk_to_graph(
        graph,
        ml_model,
        time_risk,
        weather_risk
    )
# ============================================================
# AI RISK SUMMARY
# ============================================================

ml_risk_counts = {
    "SAFE_ZONE": 0,
    "MODERATE_RISK": 0,
    "HIGH_RISK": 0,
    "CRIME_HOTSPOT": 0
}

for u, v, key, data in graph.edges(
    keys=True,
    data=True
):

    zone = data.get(
        "ml_risk_zone"
    )

    if zone in ml_risk_counts:

        ml_risk_counts[zone] += 1


print("\n===================================")
print("AI RISK ZONE SUMMARY")
print("===================================")

for zone, count in ml_risk_counts.items():

    print(
        zone,
        ":",
        count,
        "road segments"
    )

print(
    "✅ Risk zones calculated."
)
risk_counts = {
    "SAFE_ZONE": 0,
    "MODERATE_RISK": 0,
    "HIGH_RISK": 0,
    "CRIME_HOTSPOT": 0
}

for u, v, key, data in graph.edges(
    keys=True,
    data=True
):

    zone = data.get(
        "risk_zone"
    )

    if zone in risk_counts:

        risk_counts[zone] += 1


print("\n===================================")
print("RISK ZONE SUMMARY")
print("===================================")

for zone, count in risk_counts.items():

    print(
        zone,
        ":",
        count,
        "road segments"
    )



# =====================================================
# 11. APPLY FINAL WEIGHTS
# =====================================================

print("\n[12] Calculating final route weights...")

graph = apply_final_weights(
    graph
)

print(
    "✅ Final route weights calculated."
)


# =====================================================
# 8. CALCULATE SAFEST ROUTE
# =====================================================

print("\n[13] Calculating safest route...")

route = find_route(
    graph,
    START_LAT,
    START_LON,
    DESTINATION_LAT,
    DESTINATION_LON
)
route_risk_scores = []
route_risk_zones = []

for u, v in zip(
    route[:-1],
    route[1:]
):

    edge_data = graph.get_edge_data(
        u,
        v
    )

    if not edge_data:
        continue

    edge = min(
        edge_data.values(),
        key=lambda x: x.get(
            "final_routing_weight",
            x.get("length", 0)
        )
    )

    route_risk_scores.append(
        edge.get(
            "ml_risk_score",
            0
        )
    )

    route_risk_zones.append(
        edge.get(
            "ml_risk_zone",
            "SAFE_ZONE"
        )
    )


if route_risk_scores:

    average_route_risk = (
        sum(route_risk_scores)
        / len(route_risk_scores)
    )

else:

    average_route_risk = 0


print("\n===================================")
print("AI SELECTED ROUTE RISK")
print("===================================")

print(
    "Average AI risk score:",
    round(
        average_route_risk,
        2
    )
)

print(
    "AI risk zone:",
    classify_route_risk(
        average_route_risk
    )
)
alternative_route = find_alternative_route(
    graph,
    START_LAT,
    START_LON,
    DESTINATION_LAT,
    DESTINATION_LON,
    route
)
if alternative_route:

    alternative_distance, alternative_duration = (
        calculate_route_distance(
            graph,
            alternative_route
        )
    )

    print("\n===================================")
    print("ALTERNATIVE SAFE ROUTE")
    print("===================================")

    print(
        "Route distance:",
        round(
            alternative_distance / 1000,
            2
        ),
        "km"
    )

    print(
        "Estimated duration:",
        round(
            alternative_duration / 60,
            1
        ),
        "minutes"
    )

    print(
        "Alternative route nodes:",
        len(alternative_route)
    )

else:

    print(
        "\n⚠️ Alternative route unavailable."
    )


# =====================================================
# 9. RESULT
# =====================================================

distance, duration = calculate_route_distance(
    graph,
    route
)

print("\n===================================")
print("FINAL SAFE ROUTE")
print("===================================")

print(
    "Start:",
    START_LAT,
    START_LON
)

print(
    "Destination:",
    DESTINATION_LAT,
    DESTINATION_LON
)

print(
    "Route nodes:",
    len(route)
)

print(
    "Route distance:",
    round(distance / 1000, 2),
    "km"
)

print(
    "Estimated duration:",
    round(duration / 60, 1),
    "minutes"
)

print(
    "\n✅ Traffic-aware + incident-aware + safety-aware route calculated."
)

print("\nRoute:")

for node in route:

    print(
        " →",
        node
    )
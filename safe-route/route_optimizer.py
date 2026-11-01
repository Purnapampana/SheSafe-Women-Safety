import osmnx as ox
import networkx as nx


def find_route(
    graph,
    start_lat,
    start_lon,
    destination_lat,
    destination_lon
):

    start_node = ox.distance.nearest_nodes(
        graph,
        X=start_lon,
        Y=start_lat
    )

    destination_node = ox.distance.nearest_nodes(
        graph,
        X=destination_lon,
        Y=destination_lat
    )

    print("\nStart node:", start_node)
    print("Destination node:", destination_node)

    route = nx.shortest_path(
        graph,
        start_node,
        destination_node,
        weight="final_routing_weight"
    )

    return route


def find_alternative_route(
    graph,
    start_lat,
    start_lon,
    destination_lat,
    destination_lon,
    safest_route
):

    start_node = ox.distance.nearest_nodes(
        graph,
        X=start_lon,
        Y=start_lat
    )

    destination_node = ox.distance.nearest_nodes(
        graph,
        X=destination_lon,
        Y=destination_lat
    )

    # Create a temporary copy
    alternative_graph = graph.copy()

    # Penalize roads used by safest route
    for u, v in zip(
        safest_route[:-1],
        safest_route[1:]
    ):

        if not alternative_graph.has_edge(u, v):
            continue

        edge_data = alternative_graph.get_edge_data(
            u,
            v
        )

        for key, data in edge_data.items():

            current_weight = data.get(
                "final_routing_weight",
                data.get("length", 1)
            )

            if current_weight != float("inf"):

                data["alternative_weight"] = (
                    current_weight * 3
                )

    # Use normal weight for roads not in safest route
    for u, v, key, data in alternative_graph.edges(
        keys=True,
        data=True
    ):

        if "alternative_weight" not in data:

            data["alternative_weight"] = data.get(
                "final_routing_weight",
                data.get("length", 1)
            )

    try:

        alternative_route = nx.shortest_path(
            alternative_graph,
            start_node,
            destination_node,
            weight="alternative_weight"
        )

        return alternative_route

    except nx.NetworkXNoPath:

        print(
            "⚠️ No alternative route found."
        )

        return None


def calculate_route_distance(
    graph,
    route
):

    total_distance = 0
    total_time = 0

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

        total_distance += edge.get(
            "length",
            0
        )

        total_time += edge.get(
            "travel_time",
            0
        )

    return total_distance, total_time
import os
import json
import time
import requests
import math


# =====================================================
# CACHE CONFIGURATION
# =====================================================

CACHE_DIR = os.path.join(
    "data",
    "safety_cache"
)

CACHE_VERSION = "v1"


# Multiple Overpass servers
OVERPASS_SERVERS = [
    "https://overpass.kumi.systems/api/interpreter",
    "https://overpass-api.de/api/interpreter",
    "https://overpass.private.coffee/api/interpreter"
]


# =====================================================
# CREATE CACHE DIRECTORY
# =====================================================

def create_cache_directory():

    os.makedirs(
        CACHE_DIR,
        exist_ok=True
    )


# =====================================================
# CREATE REGION CACHE KEY
# =====================================================

def get_cache_file(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon
):

    # Round coordinates to approximately
    # 0.1 degree geographic regions.

    south = math.floor(
        min(
            start_lat,
            destination_lat
        ) * 10
    ) / 10

    north = math.ceil(
        max(
            start_lat,
            destination_lat
        ) * 10
    ) / 10

    west = math.floor(
        min(
            start_lon,
            destination_lon
        ) * 10
    ) / 10

    east = math.ceil(
        max(
            start_lon,
            destination_lon
        ) * 10
    ) / 10

    filename = (
        f"{CACHE_VERSION}_"
        f"{south:.1f}_"
        f"{west:.1f}_"
        f"{north:.1f}_"
        f"{east:.1f}.json"
    )

    return os.path.join(
        CACHE_DIR,
        filename
    )


# =====================================================
# CREATE OVERPASS QUERY
# =====================================================

def create_overpass_query(
    south,
    west,
    north,
    east
):

    query = f"""
    [out:json][timeout:60];

    (
      node["highway"="street_lamp"]({south},{west},{north},{east});

      node["amenity"="police"]({south},{west},{north},{east});
      way["amenity"="police"]({south},{west},{north},{east});

      node["amenity"="hospital"]({south},{west},{north},{east});
      way["amenity"="hospital"]({south},{west},{north},{east});

      node["amenity"="school"]({south},{west},{north},{east});
      way["amenity"="school"]({south},{west},{north},{east});

      node["amenity"="marketplace"]({south},{west},{north},{east});
      way["amenity"="marketplace"]({south},{west},{north},{east});

      node["public_transport"]({south},{west},{north},{east});
      way["public_transport"]({south},{west},{north},{east});

      node["amenity"="bus_station"]({south},{west},{north},{east});

      node["amenity"="restaurant"]({south},{west},{north},{east});
      way["amenity"="restaurant"]({south},{west},{north},{east});

      node["tourism"="hotel"]({south},{west},{north},{east});
      way["tourism"="hotel"]({south},{west},{north},{east});
    );

    out body;
    >;
    out skel qt;
    """

    return query


# =====================================================
# DOWNLOAD FROM OVERPASS
# =====================================================

def download_from_overpass(
    south,
    west,
    north,
    east
):

    query = create_overpass_query(
        south,
        west,
        north,
        east
    )

    for server in OVERPASS_SERVERS:

        print(
            "\nTrying Overpass server:"
        )

        print(server)

        try:

            response = requests.post(
                server,
                data={
                    "data": query
                },
                headers={
                    "User-Agent":
                        "SheSafe/1.0",
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                timeout=90
            )

            if response.status_code == 200:

                data = response.json()

                elements = data.get(
                    "elements",
                    []
                )

                if elements:

                    print(
                        "✅ Overpass data received:",
                        len(elements)
                    )

                    return data

                print(
                    "⚠️ Overpass returned no objects."
                )

            else:

                print(
                    "⚠️ Overpass HTTP error:",
                    response.status_code
                )

        except requests.exceptions.Timeout:

            print(
                "⚠️ Overpass request timed out."
            )

        except requests.exceptions.RequestException as error:

            print(
                "⚠️ Overpass connection error:"
            )

            print(error)

        except ValueError:

            print(
                "⚠️ Invalid JSON returned by Overpass."
            )

        # Give the next server a moment
        time.sleep(2)

    return None


# =====================================================
# SAVE CACHE
# =====================================================

def save_cache(
    cache_file,
    data,
    south,
    west,
    north,
    east
):

    create_cache_directory()

    cache_data = {

        "version":
            CACHE_VERSION,

        "bbox": {

            "south": south,
            "west": west,
            "north": north,
            "east": east
        },

        "saved_at":
            time.time(),

        "data":
            data
    }

    try:

        with open(
            cache_file,
            "w",
            encoding="utf-8"
        ) as file:

            json.dump(
                cache_data,
                file
            )

        print(
            "💾 Safety data cached:"
        )

        print(
            cache_file
        )

    except Exception as error:

        print(
            "⚠️ Unable to save safety cache:"
        )

        print(error)


# =====================================================
# LOAD CACHE
# =====================================================

def load_cache(
    cache_file
):

    if not os.path.exists(
        cache_file
    ):

        return None

    try:

        with open(
            cache_file,
            "r",
            encoding="utf-8"
        ) as file:

            cache_data = json.load(
                file
            )

        data = cache_data.get(
            "data"
        )

        if not data:

            return None

        elements = data.get(
            "elements",
            []
        )

        print(
            "\n✅ Using cached OSM safety data."
        )

        print(
            "Cached objects:",
            len(elements)
        )

        return data

    except Exception as error:

        print(
            "⚠️ Unable to read safety cache:"
        )

        print(error)

        return None


# =====================================================
# MAIN SAFETY DATA FUNCTION
# =====================================================

def get_safety_data(
    start_lat,
    start_lon,
    destination_lat,
    destination_lon
):

    print(
        "\nGetting safety data..."
    )

    create_cache_directory()

    # -----------------------------------------------
    # Determine geographic region
    # -----------------------------------------------

    south = math.floor(
        min(
            start_lat,
            destination_lat
        ) * 10
    ) / 10

    north = math.ceil(
        max(
            start_lat,
            destination_lat
        ) * 10
    ) / 10

    west = math.floor(
        min(
            start_lon,
            destination_lon
        ) * 10
    ) / 10

    east = math.ceil(
        max(
            start_lon,
            destination_lon
        ) * 10
    ) / 10

    cache_file = get_cache_file(
        start_lat,
        start_lon,
        destination_lat,
        destination_lon
    )

    # -----------------------------------------------
    # 1. CHECK CACHE
    # -----------------------------------------------

    cached_data = load_cache(
        cache_file
    )

    if cached_data is not None:

        return cached_data.get(
            "elements",
            []
        )

    # -----------------------------------------------
    # 2. CACHE DOES NOT EXIST
    # -----------------------------------------------

    print(
        "ℹ️ No cached safety data for this region."
    )

    print(
        "Downloading from OpenStreetMap..."
    )

    # -----------------------------------------------
    # 3. TRY OVERPASS SERVERS
    # -----------------------------------------------

    data = download_from_overpass(
        south,
        west,
        north,
        east
    )

    # -----------------------------------------------
    # 4. SUCCESS
    # -----------------------------------------------

    if data is not None:

        save_cache(
            cache_file,
            data,
            south,
            west,
            north,
            east
        )

        return data.get(
            "elements",
            []
        )

    # -----------------------------------------------
    # 5. ALL SERVERS FAILED
    # -----------------------------------------------

    print(
        "\n❌ All Overpass servers failed."
    )

    print(
        "No safety data available for this region."
    )

    return []


# =====================================================
# PROCESS SAFETY DATA
# =====================================================

def process_safety_data(
    elements
):

    safety_objects = []

    for element in elements:

        latitude = None
        longitude = None

        # -------------------------------------------
        # NODE
        # -------------------------------------------

        if element.get("type") == "node":

            latitude = element.get(
                "lat"
            )

            longitude = element.get(
                "lon"
            )

        # -------------------------------------------
        # WAY
        # -------------------------------------------

        elif element.get("type") == "way":

            center = element.get(
                "center"
            )

            if center:

                latitude = center.get(
                    "lat"
                )

                longitude = center.get(
                    "lon"
                )

        if (
            latitude is None
            or longitude is None
        ):

            continue

        tags = element.get(
            "tags",
            {}
        )

        category = "OTHER"

        # -------------------------------------------
        # SAFETY CATEGORIES
        # -------------------------------------------

        if tags.get(
            "highway"
        ) == "street_lamp":

            category = "STREETLIGHT"

        elif tags.get(
            "amenity"
        ) == "police":

            category = "POLICE"

        elif tags.get(
            "amenity"
        ) == "hospital":

            category = "HOSPITAL"

        elif tags.get(
            "amenity"
        ) == "school":

            category = "SCHOOL"

        elif tags.get(
            "amenity"
        ) == "marketplace":

            category = "MARKET"

        elif tags.get(
            "amenity"
        ) == "bus_station":

            category = "BUS_STATION"

        elif tags.get(
            "public_transport"
        ):

            category = "PUBLIC_TRANSPORT"

        elif tags.get(
            "amenity"
        ) == "restaurant":

            category = "RESTAURANT"

        elif tags.get(
            "tourism"
        ) == "hotel":

            category = "HOTEL"

        safety_objects.append({

            "osm_id":
                element.get("id"),

            "category":
                category,

            "name":
                tags.get(
                    "name",
                    "Unknown"
                ),

            "latitude":
                float(latitude),

            "longitude":
                float(longitude)
        })

    return safety_objects
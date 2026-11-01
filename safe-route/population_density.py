import os
import rasterio

POPULATION_FILE = os.path.join(
    "data",
    "population",
    "india_population.tif"
)


def get_population_density(latitude, longitude):

    if not os.path.exists(POPULATION_FILE):
        print("⚠️ Population dataset not found.")
        print("Expected file:", POPULATION_FILE)

        return None

    try:
        with rasterio.open(POPULATION_FILE) as dataset:

            value = list(
                dataset.sample([(longitude, latitude)])
            )[0][0]

            if value is None:
                return None

            population_density = float(value)

            if population_density < 0:
                return None

            return population_density

    except Exception as error:

        print("⚠️ Unable to read population data:")
        print(error)

        return None
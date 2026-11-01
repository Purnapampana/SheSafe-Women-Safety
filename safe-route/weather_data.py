import requests


def get_weather(
    latitude,
    longitude
):

    url = "https://api.open-meteo.com/v1/forecast"

    params = {
        "latitude": latitude,
        "longitude": longitude,
        "current": "temperature_2m,precipitation,weather_code",
        "timezone": "auto"
    }

    try:

        response = requests.get(
            url,
            params=params,
            timeout=10
        )

        if response.status_code != 200:

            print(
                "⚠️ Weather API error:",
                response.status_code
            )

            return {
                "condition": "Unknown",
                "temperature": None,
                "precipitation": 0
            }

        data = response.json()

        current = data.get(
            "current",
            {}
        )

        weather_code = current.get(
            "weather_code",
            0
        )

        precipitation = current.get(
            "precipitation",
            0
        )

        condition = weather_code_to_condition(
            weather_code
        )

        return {
            "condition": condition,
            "temperature": current.get(
                "temperature_2m"
            ),
            "precipitation": precipitation
        }

    except requests.exceptions.RequestException as error:

        print(
            "⚠️ Unable to get weather:",
            error
        )

        return {
            "condition": "Unknown",
            "temperature": None,
            "precipitation": 0
        }


def weather_code_to_condition(
    code
):

    if code == 0:
        return "Clear"

    elif code in [1, 2, 3]:
        return "Cloudy"

    elif code in [
        51, 53, 55,
        56, 57,
        61, 63, 65,
        66, 67,
        80, 81, 82
    ]:
        return "Rain"

    elif code in [
        95, 96, 99
    ]:
        return "Storm"

    elif code in [
        45, 48
    ]:
        return "Fog"

    return "Unknown"
"""Provides a Planet class for using planets from the Trident API.

"""
import requests

class Planet:
    """Represents a planet.

    Arguments:
    system_id - ID number denoting the solar system.
    star_id - ID number denoting the star.
    planet_id - ID number denoting the planet.
    server_url - Trident API server URL. Defaults to http://trident.senorpez.com/
    """
    def __init__(self, system_id, star_id, planet_id, server_url="http://trident.senorpez.com/"):
        pass

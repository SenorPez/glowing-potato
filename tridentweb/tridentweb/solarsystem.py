"""Provides a SolarSystem class for using solar systems from the Trident API.

"""
from tridentweb.api import get_system


class SolarSystem:
    """Represents a solar system

    :param system_id: Solar system ID, for use with the Trident API
    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    """
    def __init__(self, system_id, server_url="https://www.trident.senorpez.com/"):
        system_response = get_system(system_id, server_url)
        self.id = system_response.json()['id']
        self.name = system_response.json()['name']


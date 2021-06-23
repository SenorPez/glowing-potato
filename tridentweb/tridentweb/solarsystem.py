"""Provides a SolarSystem class for using solar systems from the Trident API.

"""
from tridentweb.api import get_system


class SolarSystem:
    """Represents a solar system

    Arguments:
        system_id: Solar system ID, for use with the Trident API
        server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    """
    def __init__(self, system_id, server_url="https://www.trident.senorpez.com/"):
        req = get_system(system_id, server_url)
        self.id = req.json()['id']
        self.name = req.json()['name']


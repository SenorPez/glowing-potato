"""Provides a SolarSystem class for using solar systems from the Trident API.

"""
import requests


class SolarSystem:
    """Represents a solar system.

    Arguments:
        system_id: Solar system ID, for use with the Trident API
        server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    """

    def __init__(self, system_id, server_url="https://www.trident.senorpez.com/"):
        req = requests.get(server_url)
        req.raise_for_status()
        systems_url = req.json()['_links']['trident-api:systems']['href']

        req = requests.get(systems_url)
        req.raise_for_status()

        system = next(x for x in req.json()['_embedded']['trident-api:system'] if x['id'] == system_id)
        system_url = system['_links']['self']['href']

        req = requests.get(system_url)
        req.raise_for_status()

        self.id = req.json()['id']
        self.name = req.json()['name']

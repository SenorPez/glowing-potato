"""Provides a Star class for using stars from the Trident API.

"""
import requests

class Star:
    """Represents a planet.

    Arguments:
    system_id - ID number denoting the solar system.
    star_id - ID number denoting the star.
    server_url - Trident API server URL. Defaults to http://trident.senorpez.com/
    """
    def __init__(self, system_id, star_id, server_url="http://trident.senorpez.com/"):
        req = requests.get(server_url)
        req.raise_for_status()
        systems_url = req.json()['_links']['trident-api:systems']['href']

        req = requests.get(systems_url)
        req.raise_for_status()
        system_url = None
        for entry in req.json()['_embedded']['trident-api:system']:
            if entry['id'] == system_id:
                system_url = entry['_links']['self']['href']

        req = requests.get(system_url)
        req.raise_for_status()
        stars_url = req.json()['_links']['trident-api:stars']['href']

        req = requests.get(stars_url)
        req.raise_for_status()
        star_url = None
        for entry in req.json()['_embedded']['trident-api:star']:
            if entry['id'] == star_id:
                star_url = entry['_links']['self']['href']

        req = requests.get(star_url)
        req.raise_for_status()

        self.id = req.json()['id']


"""Provides a Planet class for using planets from the Trident API.

"""
from pykep import epoch, AU
from pykep.planet import keplerian

import requests
from tridentweb.constant import Constant
from tridentweb.star import Star
from tridentweb.pykep_addons import mean_from_true

class Planet:
    """Represents a planet.

    Arguments:
    system_id - ID number denoting the solar system.
    star_id - ID number denoting the star.
    planet_id - ID number denoting the planet.
    server_url - Trident API server URL. Defaults to http://trident.senorpez.com/
    """
    planet_mass = None
    planet_radius = None
    grav = None
    pykep_planet = None

    def __init__(self, system_id, star_id, planet_id, server_url="http://trident.senorpez.com/"):
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
        planets_url = req.json()['_links']['trident-api:planets']['href']

        req = requests.get(planets_url)
        req.raise_for_status()
        planet_url = None
        for entry in req.json()['_embedded']['trident-api:planet']:
            if entry['id'] == planet_id:
                planet_url = entry['_links']['self']['href']

        req = requests.get(planet_url)
        req.raise_for_status()

        self.id = req.json()['id']
        self.name = req.json()['name']
        self.mass = req.json()['mass']
        self.radius = req.json()['radius']
        self.semimajor_axis = req.json()['semimajorAxis']
        self.eccentricity = req.json()['eccentricity']
        self.inclination = req.json()['inclination']
        self.longitude_of_ascending_node = req.json()['longitudeOfAscendingNode']
        self.argument_of_periapsis = req.json()['argumentOfPeriapsis']
        self.true_anomaly_at_epoch = req.json()['trueAnomalyAtEpoch']

        self._star = Star(system_id, star_id)

    @property
    def gm(self):
        """Standard gravitational parameter of the Planet."""
        if self.planet_mass is None:
            planet_mass_constant = Constant("Mpln")
            self.planet_mass = planet_mass_constant.value
        if self.grav is None:
            grav_constant = Constant("G")
            self.grav = grav_constant.value

        return self.mass * self.planet_mass * self.grav

    @property
    def planet(self):
        """PyKep object (pykep.planet.keplerian) representation of Planet."""
        if self.pykep_planet is None:
            if self.planet_radius is None:
                planet_radius_constant = Constant("Rpln")
                self.planet_radius = planet_radius_constant.value

            self.pykep_planet = keplerian(
                epoch(0),
                (
                    self.semimajor_axis * AU,
                    self.eccentricity,
                    self.inclination,
                    self.longitude_of_ascending_node,
                    self.argument_of_periapsis,
                    mean_from_true(self.eccentricity, self.true_anomaly_at_epoch)),
                self._star.gm,
                self.gm,
                self.radius * self.planet_radius,
                self.radius * self.planet_radius,
                self.name)
        return self.pykep_planet

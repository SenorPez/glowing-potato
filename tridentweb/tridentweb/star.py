"""Provides a Star class for using stars from the Trident API.

"""
from pykep import epoch, AU
from pykep.planet import keplerian

import requests
from tridentweb.constant import Constant

class Star:
    """Represents a star.

    Arguments:
    system_id - ID number denoting the solar system.
    star_id - ID number denoting the star.
    server_url - Trident API server URL. Defaults to http://trident.senorpez.com/
    """
    solar_mass = None
    grav = None
    pykep_planet = None

    def __init__(self, system_id, star_id, primary=None, server_url="http://trident.senorpez.com/"):
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

        self._primary = primary
        self.id = req.json()['id']
        self.name = req.json()['name']
        self.mass = req.json()['mass']

        self.semimajor_axis = None
        self.eccentricity = None
        self.inclination = None
        self.longitude_of_ascending_node = None
        self.argument_of_periapsis = None
        self.true_anomaly_at_epoch = None

        if self._primary is not None:
            self.semimajor_axis = req.json()['semimajorAxis']
            self.eccentricity = req.json()['eccentricity']
            self.inclination = req.json()['inclination']
            self.longitude_of_ascending_node = req.json()['longitudeOfAscendingNode']
            self.argument_of_periapsis = req.json()['argumentOfPeriapsis']
            self.true_anomaly_at_epoch = req.json()['trueAnomalyAtEpoch']

    @property
    def gm(self):
        """Standard gravitational parameter of the Planet."""
        if self.solar_mass is None:
            solar_mass_constant = Constant("Msol")
            self.solar_mass = solar_mass_constant.value
        if self.grav is None:
            grav_constant = Constant("G")
            self.grav = grav_constant.value

        return self.mass * self.solar_mass * self.grav

    @property
    def planet(self):
        """PyKep object (pykep.planet.keplerian) representation of Planet."""
        if self._primary is None:
            raise ValueError

        if self.pykep_planet is None:
            self.pykep_planet = keplerian(
                epoch(0),
                (
                    self.semimajor_axis * AU,
                    self.eccentricity,
                    self.inclination,
                    self.longitude_of_ascending_node,
                    self.argument_of_periapsis,
                    self.true_anomaly_at_epoch),
                self._primary.gm,
                self.gm,
                1000,
                1000,
                self.name)
        return self.pykep_planet

"""Provides a Planet class for using planets from the Trident API.

"""
import jsonpickle
from pykep import epoch, AU
from pykep.planet import keplerian

from tridentweb.api import get_planet
from tridentweb.constant import Constant
from tridentweb.pykep_addons import mean_from_true


class Planet:
    """Represents a planet.

    Arguments:
    system_id - ID number denoting the solar system.
    star_id - ID number denoting the star.
    planet_id - ID number denoting the planet.
    server_url - Trident API server URL. Defaults to https://www.trident.senorpez.com/
    """
    def __init__(self, system_id, star_id, planet_id, server_url="https://www.trident.senorpez.com/"):
        planet_response, self._star_gm = get_planet(system_id, star_id, planet_id, server_url)
        self.id = planet_response.json()['id']
        self.name = planet_response.json()['name']
        self.mass = planet_response.json()['mass']
        self.radius = planet_response.json()['radius']

        self.semimajor_axis = planet_response.json()['semimajorAxis']
        self.eccentricity = planet_response.json()['eccentricity']
        self.inclination = planet_response.json()['inclination']
        self.longitude_of_ascending_node = planet_response.json()['longitudeOfAscendingNode']
        self.argument_of_periapsis = planet_response.json()['argumentOfPeriapsis']
        self.true_anomaly_at_epoch = planet_response.json()['trueAnomalyAtEpoch']

        self._planet_mass = None
        self._planet_radius = None
        self._grav = None
        self._pykep_planet = None

    def to_json(self):
        # Make sure planet has been defined (to get all API calls settled),
        # then clear planet since it can't be serialized.
        _ = self.planet
        self._pykep_planet = None
        return jsonpickle.encode(self)

    @staticmethod
    def from_json(pickled):
        return jsonpickle.decode(pickled)

    @property
    def gm(self):
        """Standard gravitational parameter of the Planet."""
        if self._planet_mass is None:
            planet_mass_constant = Constant("Mpln")
            self._planet_mass = planet_mass_constant.value
        if self._grav is None:
            grav_constant = Constant("G")
            self._grav = grav_constant.value

        return self.mass * self._planet_mass * self._grav

    @property
    def planet(self):
        """PyKep object (pykep.planet.keplerian) representation of Planet."""
        if self._pykep_planet is None:
            if self._planet_radius is None:
                planet_radius_constant = Constant("Rpln")
                self._planet_radius = planet_radius_constant.value

            self._pykep_planet = keplerian(
                epoch(0),
                (
                    self.semimajor_axis * AU,
                    self.eccentricity,
                    self.inclination,
                    self.longitude_of_ascending_node,
                    self.argument_of_periapsis,
                    mean_from_true(self.eccentricity, self.true_anomaly_at_epoch)),
                self._star_gm,
                self.gm,
                self.radius * self._planet_radius,
                self.radius * self._planet_radius,
                self.name)
        return self._pykep_planet

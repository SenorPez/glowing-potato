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
        planet_response, self._star_mass = get_planet(system_id, star_id, planet_id, server_url)
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

        self._const_Mpln = None
        self._const_Rpln = None
        self._const_Msol = None
        self._const_G = None
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
        if self._const_Mpln is None:
            planet_mass_constant = Constant("Mpln")
            self._const_Mpln = planet_mass_constant.value
        if self._const_G is None:
            grav_constant = Constant("G")
            self._const_G = grav_constant.value

        return self.mass * self._const_Mpln * self._const_G

    @property
    def star_gm(self):
        """Standard graviational parameter of the star."""
        if self._const_Msol is None:
            star_mass_constant = Constant("Msol")
            self._const_Msol = star_mass_constant.value
        if self._const_G is None:
            grav_constant = Constant("G")
            self._const_G = grav_constant.value

        return self._star_mass * self._const_Msol * self._const_G

    @property
    def planet(self):
        """PyKep object (pykep.planet.keplerian) representation of Planet."""
        if self._pykep_planet is None:
            if self._const_Rpln is None:
                planet_radius_constant = Constant("Rpln")
                self._const_Rpln = planet_radius_constant.value

            self._pykep_planet = keplerian(
                epoch(0),
                (
                    self.semimajor_axis * AU,
                    self.eccentricity,
                    self.inclination,
                    self.longitude_of_ascending_node,
                    self.argument_of_periapsis,
                    mean_from_true(self.eccentricity, self.true_anomaly_at_epoch)),
                self.star_gm,
                self.gm,
                self.radius * self._const_Rpln,
                self.radius * self._const_Rpln,
                self.name)
        return self._pykep_planet

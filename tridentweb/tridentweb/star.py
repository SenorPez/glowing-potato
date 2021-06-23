"""Provides a Star class for using stars from the Trident API.

"""
from pykep import epoch, AU
from pykep.planet import keplerian

from tridentweb.api import get_star
from tridentweb.constant import Constant


class Star:
    """Represents a star

    :param system_id: Solar system ID, for use with the Trident API
    :param star_id: Star ID, for use with the Trident API
    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    """
    def __init__(self, system_id, star_id, server_url="https://www.trident.senorpez.com/"):
        star_response, self._primary_GM = get_star(system_id, star_id, server_url=server_url)
        self.id = star_response.json()['id']
        self.name = star_response.json()['name']
        self.mass = star_response.json()['mass']

        self.semimajor_axis = star_response.json()['semimajorAxis']
        self.eccentricity = star_response.json()['eccentricity']
        self.inclination = star_response.json()['inclination']
        self.longitude_of_ascending_node = star_response.json()['longitudeOfAscendingNode']
        self.argument_of_periapsis = star_response.json()['argumentOfPeriapsis']
        self.true_anomaly_at_epoch = star_response.json()['trueAnomalyAtEpoch']

        self._solar_mass = None
        self._grav = None
        self._pykep_planet = None

    @property
    def gm(self):
        """Standard gravitational parameter of the object."""
        if self._solar_mass is None:
            solar_mass_constant = Constant("Msol")
            self._solar_mass = solar_mass_constant.value
        if self._grav is None:
            grav_constant = Constant("G")
            self._grav = grav_constant.value

        return self.mass * self._solar_mass * self._grav

    @property
    def planet(self):
        """PyKep object (pykep.planet.keplerian) representation of Planet."""
        if self._primary_GM is None:
            raise ValueError

        if self._pykep_planet is None:
            self._pykep_planet = keplerian(
                epoch(0),
                (
                    self.semimajor_axis * AU,
                    self.eccentricity,
                    self.inclination,
                    self.longitude_of_ascending_node,
                    self.argument_of_periapsis,
                    self.true_anomaly_at_epoch),
                self._primary_GM,
                self.gm,
                1000,
                1000,
                self.name)
        return self._pykep_planet

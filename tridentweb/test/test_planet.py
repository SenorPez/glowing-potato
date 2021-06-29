"""Tests Planet.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel, Mock

import pykep
from pykep.planet import keplerian

from tridentweb.planet import Planet


def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """

        def __init__(self, *, json_text=None):
            self.json_data = json.loads(json_text)

        def json(self):
            """Returns JSON data of response"""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response"""
            return None

    return MockResponse(*args, **kwargs)


def mock_api_planet(planet_id=0, name="", mass=1, radius=1, semimajor_axis=None, eccentricity=None,
                    inclination=None, longitude_of_ascending_node=None,
                    argument_of_periapsis=None, true_anomaly_at_epoch=None):
    return {"id": planet_id,
            "name": "{0}".format(name),
            "mass": mass,
            "radius": radius,
            "semimajorAxis": semimajor_axis,
            "eccentricity": eccentricity,
            "inclination": inclination,
            "longitudeOfAscendingNode": longitude_of_ascending_node,
            "argumentOfPeriapsis": argument_of_periapsis,
            "trueAnomalyAtEpoch": true_anomaly_at_epoch}


class TestPlanet(unittest.TestCase):
    """Unit tests against the Planet object"""

    @mock.patch('tridentweb.planet.get_planet')
    def test_init(self, mock_get):
        """Test Planet init"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.starid),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = Planet
        self.assertIsInstance(instance, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_id(self, mock_get):
        """Test id property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.planet_id)
        self.assertEqual(instance.id, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_name(self, mock_get):
        """Test name property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_mass(self, mock_get):
        """Test mass property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_radius(self, mock_get):
        """Test radius property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.radius)
        self.assertEqual(instance.radius, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_semimajor_axis(self, mock_get):
        """Test semimajor axis property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.sma)
        self.assertEqual(instance.semimajor_axis, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_eccentricity(self, mock_get):
        """Test eccentricity property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.ecc)
        self.assertEqual(instance.eccentricity, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_inclination(self, mock_get):
        """Test inclination property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.inc)
        self.assertEqual(instance.inclination, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_longitude_of_ascending_node(self, mock_get):
        """Test longitude of ascending node property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.loan)
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_argument_of_periapsis(self, mock_get):
        """Test argument of periapsis property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.aop)
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_true_anomaly_at_epoch(self, mock_get):
        """Test true anomaly at epoch property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.taoe)
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_gm(self, mock_get):
        """Test GM property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     id(sentinel.ecc),
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = id(sentinel.mass) * 6.67408e-11 * 5.9722e+24
        self.assertAlmostEqual(instance.gm, expected_result, delta=1.0e+14)

    @mock.patch('tridentweb.planet.get_planet')
    def test_property_planet(self, mock_get):
        """Test planet property of Planet"""
        attrs = {'json.return_value':
                     mock_api_planet(id(sentinel.planet_id),
                                     str(id(sentinel.name)),
                                     id(sentinel.mass),
                                     id(sentinel.radius),
                                     id(sentinel.sma),
                                     0.5,
                                     id(sentinel.inc),
                                     id(sentinel.loan),
                                     id(sentinel.aop),
                                     id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Planet(id(sentinel.system_id), id(sentinel.star_id), id(sentinel.planet_id), "https://api/")
        expected_result = pykep.planet.keplerian
        self.assertIsInstance(instance.planet, expected_result)


class IntegrationPlanet(unittest.TestCase):
    """Integration tests against reference implementation of Trident API"""

    def test_init_planet(self):
        """Test 1 Omega Hydri 3 init"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = Planet
        self.assertIsInstance(instance, expected_result)

    def test_property_id(self):
        """Test 1 Omega Hydri 3 id"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 159569841
        self.assertEqual(instance.id, expected_result)

    def test_property_name(self):
        """Test 1 Omega Hydri 3 name"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = "1 Omega Hydri 3"
        self.assertEqual(instance.name, expected_result)

    def test_property_mass(self):
        """Test 1 Omega Hydri 3 mass"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 0.71235156
        self.assertEqual(instance.mass, expected_result)

    def test_property_radius(self):
        """Test 1 Omega Hydri 3 radius"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 0.9022098
        self.assertEqual(instance.radius, expected_result)

    def test_property_semimajor_axis(self):
        """Test 1 Omega Hydri 3 semimajor axis"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 0.93247896
        self.assertEqual(instance.semimajor_axis, expected_result)

    def test_property_eccentricity(self):
        """Test 1 Omega Hydri 3 eccentricity"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 0.115
        self.assertEqual(instance.eccentricity, expected_result)

    def test_property_inclination(self):
        """Test 1 Omega Hydri 3 inclination"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 0
        self.assertEqual(instance.inclination, expected_result)

    def test_property_longitude_of_ascending_node(self):
        """Test 1 Omega Hydri 3 longitude of ascending node"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 0
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    def test_property_argument_of_periapsis(self):
        """Test 1 Omega Hydri 3 argument of periapsis"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 3.1809726
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    def test_property_true_anomaly_at_epoch(self):
        """Test 1 Omega Hydri 3 true anomaly at epoch"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 4.653676
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

    def test_property_gm(self):
        """Test 1 Omega Hydri 3 GM"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = 283935784992609
        self.assertAlmostEqual(instance.gm, expected_result)

    def test_property_planet(self):
        """Test 1 Omega Hydri 3 planet"""
        instance = Planet(1621827699, -1826843336, 159569841)
        expected_result = pykep.planet.keplerian
        self.assertIsInstance(instance.planet, expected_result)

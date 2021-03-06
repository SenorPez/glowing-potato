"""Tests Planet.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel, MagicMock, PropertyMock

from pykep.planet import keplerian
from requests.exceptions import HTTPError
from tridentweb.planet import Planet

def mocked_requests_get(*args, **kwargs):
    """Defines a response suitable for mocking requests responses."""
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_string=None, idnum=0, name="", mass=0, radius=0,
                     semimajor_axis=0, eccentricity=0, inclination=0,
                     longitude_of_ascending_node=0, argument_of_periapsis=0,
                     true_anomaly_at_epoch=0):
            if json_string is None:
                json_string = ("{{"
                               "\"id\": {0},"
                               "\"name\": \"{1}\","
                               "\"mass\": {2},"
                               "\"radius\": {3},"
                               "\"semimajorAxis\": {4},"
                               "\"eccentricity\": {5},"
                               "\"inclination\": {6},"
                               "\"longitudeOfAscendingNode\": {7},"
                               "\"argumentOfPeriapsis\": {8},"
                               "\"trueAnomalyAtEpoch\": {9}}}"
                               ).format(idnum, name, mass, radius, semimajor_axis, eccentricity,
                                        inclination, longitude_of_ascending_node,
                                        argument_of_periapsis, true_anomaly_at_epoch)
            self.json_data = json.loads(json_string)

        def json(self):
            """Returns json data of response."""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response."""
            return None

    return MockResponse(*args, **kwargs)

class TestPlanet(unittest.TestCase):
    """Unit tests against the Planet object."""
    api_traversal = [
        mocked_requests_get(json_string=( \
            "{\"_links\": {\"trident-api:systems\":"
            "{\"href\": \"http://trident.senorpez.com/systems\"}}}")),
        mocked_requests_get(json_string=( \
            "{\"_embedded\": {\"trident-api:system\":"
            "[{\"id\": 1,"
            "\"_links\": {\"self\":"
            "{\"href\": \"http://trident.senorpez.com/systems/1\"}}}]}}")),
        mocked_requests_get(json_string=( \
            "{\"_links\": {\"trident-api:stars\":"
            "{\"href\": \"http://trident.senorpez.com/stars\"}}}")),
        mocked_requests_get(json_string=( \
            "{\"_embedded\": {\"trident-api:star\":"
            "[{\"id\": 1,"
            "\"_links\": {\"self\":"
            "{\"href\": \"http://trident.senorpez.com/stars/1\"}}}]}}")),
        mocked_requests_get(json_string=( \
            "{\"_links\": {\"trident-api:planets\":"
            "{\"href\": \"http://trident.senorpez.com/planets\"}}}")),
        mocked_requests_get(json_string=( \
            "{\"_embedded\": {\"trident-api:planet\":"
            "[{\"id\": 1,"
            "\"_links\": {\"self\":"
            "{\"href\": \"http://trident.senorpez.com/planets/1\"}}}]}}"))]

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_init(self, mock_get, _):
        """Test Planet init."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Planet(1, 1, 1)
        expected_result = Planet
        self.assertIsInstance(instance, expected_result)

    @mock.patch('requests.get')
    def test_init_index_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API index."""
        mock_get.side_effect = HTTPError("Error", None)
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_index_KeyError(self, mock_get):
        """Test Planet init with KeyError on API index."""
        mock_get.side_effect = KeyError()
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_systems_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API systems."""
        mock_get.side_effect = self.api_traversal[0:1] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_systems_KeyError(self, mock_get):
        """Test Planet init with KeyError on API systems."""
        mock_get.side_effect = self.api_traversal[0:1] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_system_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API system."""
        mock_get.side_effect = self.api_traversal[0:2] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_system_KeyError(self, mock_get):
        """Test Planet init with KeyError on API system."""
        mock_get.side_effect = self.api_traversal[0:2] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_stars_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API stars."""
        mock_get.side_effect = self.api_traversal[0:3] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_stars_KeyError(self, mock_get):
        """Test Planet init with KeyError on API stars."""
        mock_get.side_effect = self.api_traversal[0:3] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_star_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API star."""
        mock_get.side_effect = self.api_traversal[0:4] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_star_KeyError(self, mock_get):
        """Test Planet init with KeyError on API star."""
        mock_get.side_effect = self.api_traversal[0:4] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_planets_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API planets."""
        mock_get.side_effect = self.api_traversal[0:5] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_planets_KeyError(self, mock_get):
        """Test Planet init with KeyError on API planets."""
        mock_get.side_effect = self.api_traversal[0:5] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_planet_HTTPError(self, mock_get):
        """Test Planet init with HTTPError on API planet."""
        mock_get.side_effect = self.api_traversal + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Planet(1, 1, 1)

    @mock.patch('requests.get')
    def test_init_planet_KeyError(self, mock_get):
        """Test Planet init with KeyError on API planet."""
        mock_get.side_effect = self.api_traversal + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Planet(1, 1, 1)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_id(self, mock_get, _):
        """Test id property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(idnum=1)]

        instance = Planet(1, 1, 1)
        expected_result = 1
        self.assertEqual(instance.id, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_name(self, mock_get, _):
        """Test name property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(name=id(sentinel.name))]

        instance = Planet(1, 1, 1)
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_mass(self, mock_get, _):
        """Test mass property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=id(sentinel.mass))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_radius(self, mock_get, _):
        """Test radius property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(radius=id(sentinel.radius))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.radius)
        self.assertEqual(instance.radius, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_semimajor_axis(self, mock_get, _):
        """Test semimajor axis property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(semimajor_axis=id(sentinel.semimajor_axis))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.semimajor_axis)
        self.assertEqual(instance.semimajor_axis, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_eccentricity(self, mock_get, _):
        """Test eccentricity property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(eccentricity=id(sentinel.eccentricity))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.eccentricity)
        self.assertEqual(instance.eccentricity, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_inclination(self, mock_get, _):
        """Test inclination property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(inclination=id(sentinel.inclination))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.inclination)
        self.assertEqual(instance.inclination, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_longitude_of_ascending_node(self, mock_get, _):
        """Test longitude of ascending node property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(longitude_of_ascending_node=id(sentinel.longitude))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.longitude)
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_argument_of_periapsis(self, mock_get, _):
        """Test argument of periapsis property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(argument_of_periapsis=id(sentinel.argument))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.argument)
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('requests.get')
    def test_property_true_anomaly_at_epoch(self, mock_get, _):
        """Test true anomaly at epoch property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(true_anomaly_at_epoch=id(sentinel.true))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.true)
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('tridentweb.planet.Constant')
    @mock.patch('requests.get')
    def test_property_gm(self, mock_get, mock_constant, _):
        """Test gm property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=0.75)]

        mock_grav = MagicMock()
        type(mock_grav).value = PropertyMock(return_value=6.67408e-11)

        mock_planet_mass = MagicMock()
        type(mock_planet_mass).value = PropertyMock(return_value=5.9722e24)

        mock_constant.side_effect = [mock_planet_mass, mock_grav]

        instance = Planet(1, 1, 1)
        expected_result = 0.75 * 5.9722e24 * 6.67408e-11
        self.assertEqual(instance.gm, expected_result)

    @mock.patch('tridentweb.planet.Star')
    @mock.patch('tridentweb.planet.Constant')
    @mock.patch('requests.get')
    def test_property_planet_pykep(self, mock_get, mock_constant, mock_star):
        """Test planet pykep property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(
                    semimajor_axis=1,
                    mass=0.75)]

        mock_grav = MagicMock()
        type(mock_grav).value = PropertyMock(return_value=6.67408e-11)

        mock_planet_mass = MagicMock()
        type(mock_planet_mass).value = PropertyMock(return_value=5.9722e24)

        mock_planet_radius = MagicMock()
        type(mock_planet_radius).value = PropertyMock(return_value=6378136.6)

        mock_constant.side_effect = [mock_planet_radius, mock_planet_mass, mock_grav]

        mock_star_gm = MagicMock()
        type(mock_star_gm).gm = PropertyMock(return_value=0.75 * 1.9884e30 * 6.67408e-11)

        mock_star.return_value = mock_star_gm

        instance = Planet(1, 1, 1)
        expected_result = keplerian
        self.assertIsInstance(instance.planet, expected_result)


class IntegrationPlanet(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init_planet(self):
        """Test 1 Eta Veneris 3 init."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = Planet
        self.assertIsInstance(instance, expected_result)

    def test_property_id(self):
        """Test 1 Eta Veneris 3 id."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = -455609026
        self.assertEqual(instance.id, expected_result)

    def test_property_name(self):
        """Test 1 Eta Veneris 3 name."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = "1 Eta Veneris 3"
        self.assertEqual(instance.name, expected_result)

    def test_property_mass(self):
        """Test 1 Eta Veneris 3 mass."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 0.7156807
        self.assertEqual(instance.mass, expected_result)

    def test_property_radius(self):
        """Test 1 Eta Veneris 3 radius."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 0.8665139
        self.assertEqual(instance.radius, expected_result)

    def test_property_semimajor_axis(self):
        """Test 1 Eta Veneris 3 semimajor axis."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 0.50302297
        self.assertEqual(instance.semimajor_axis, expected_result)

    def test_property_eccentricity(self):
        """Test 1 Eta Veneris 3 eccentricity."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 0.05
        self.assertEqual(instance.eccentricity, expected_result)

    def test_property_inclination(self):
        """Test 1 Eta Veneris 3 inclination."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 0
        self.assertEqual(instance.inclination, expected_result)

    def test_property_longitude_of_ascending_node(self):
        """Test 1 Eta Veneris 3 longitude of ascending node."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 0
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    def test_property_argument_of_periapsis(self):
        """Test 1 Eta Veneris 3 argument of periapsis."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 3.7920682
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    def test_property_true_anomaly_at_epoch(self):
        """Test 1 Eta Veneris 3 true anomaly at epoch."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = 3.8604798
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

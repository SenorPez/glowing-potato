"""Tests Planet.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel

from requests.exceptions import HTTPError
from tridentweb.Planet import Planet

def mocked_requests_get(*args, **kwargs):
    """Defines a response suitable for mocking requests responses."""
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_string=None, idnum=0, name="", mass=0, radius=0,
                     semimajor_axis=0, eccentricity=0, inclination=0,
                     longitude_of_ascending_node=0):
            if json_string is None:
                json_string = ("{{"
                               "\"id\": {0},"
                               "\"name\": \"{1}\","
                               "\"mass\": {2},"
                               "\"radius\": {3},"
                               "\"semimajorAxis\": {4},"
                               "\"eccentricity\": {5},"
                               "\"inclination\": {6},"
                               "\"longitudeOfAscendingNode\": {7}}}"
                               ).format(idnum, name, mass, radius, semimajor_axis, eccentricity,
                                        inclination, longitude_of_ascending_node)
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

    @mock.patch('requests.get')
    def test_init(self, mock_get):
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

    @mock.patch('requests.get')
    def test_property_id(self, mock_get):
        """Test id property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(idnum=1)]

        instance = Planet(1, 1, 1)
        expected_result = 1
        self.assertEqual(instance.id, expected_result)

    @mock.patch('requests.get')
    def test_property_name(self, mock_get):
        """Test name property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(name=id(sentinel.name))]

        instance = Planet(1, 1, 1)
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('requests.get')
    def test_property_mass(self, mock_get):
        """Test mass property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=id(sentinel.mass))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

    @mock.patch('requests.get')
    def test_property_radius(self, mock_get):
        """Test radius property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(radius=id(sentinel.radius))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.radius)
        self.assertEqual(instance.radius, expected_result)

    @mock.patch('requests.get')
    def test_property_semimajor_axis(self, mock_get):
        """Test semimajor axis property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(semimajor_axis=id(sentinel.semimajor_axis))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.semimajor_axis)
        self.assertEqual(instance.semimajor_axis, expected_result)

    @mock.patch('requests.get')
    def test_property_eccentricity(self, mock_get):
        """Test eccentricity property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(eccentricity=id(sentinel.eccentricity))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.eccentricity)
        self.assertEqual(instance.eccentricity, expected_result)

    @mock.patch('requests.get')
    def test_property_inclination(self, mock_get):
        """Test inclination property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(inclination=id(sentinel.inclination))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.inclination)
        self.assertEqual(instance.inclination, expected_result)

    @mock.patch('requests.get')
    def test_property_longitude_of_ascending_node(self, mock_get):
        """Test longitude of ascending node property of Planet."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(longitude_of_ascending_node=id(sentinel.longitude))]

        instance = Planet(1, 1, 1)
        expected_result = id(sentinel.longitude)
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)


class IntegrationPlanet(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init_planet(self):
        """Test 1 Eta Veneris 3 init."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = Planet
        self.assertIsInstance(instance, expected_result)

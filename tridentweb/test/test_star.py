"""Tests star.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel

from requests.exceptions import HTTPError
from tridentweb.star import Star

def mocked_requests_get(*args, **kwargs):
    """Defines a response suitable for mocking requests responses."""
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_string=None, idnum=0, name="", mass=0):
            if json_string is None:
                json_string = ("{{\"id\": {0},"
                               "\"name\": \"{1}\","
                               "\"mass\": {2}}}"
                              ).format(idnum, name, mass)
            self.json_data = json.loads(json_string)

        def json(self):
            """Returns json data of response."""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response."""
            return None

    return MockResponse(*args, **kwargs)

class TestStar(unittest.TestCase):
    """Unit tests against the Star object."""
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
            "{\"href\": \"http://trident.senorpez.com/stars/1\"}}}]}}"))]

    @mock.patch('requests.get')
    def test_init(self, mock_get):
        """Test Star init."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    @mock.patch('requests.get')
    def test_init_index_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API index."""
        mock_get.side_effect = HTTPError("Error", None)
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_index_KeyError(self, mock_get):
        """Test Star init with KeyError on API index."""
        mock_get.side_effect = KeyError()
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_systems_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API systems."""
        mock_get.side_effect = self.api_traversal[0:1] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_systems_KeyError(self, mock_get):
        """Test Star init with KeyError on API systems."""
        mock_get.side_effect = self.api_traversal[0:1] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_system_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API system."""
        mock_get.side_effect = self.api_traversal[0:2] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_system_KeyError(self, mock_get):
        """Test Star init with KeyError on API system."""
        mock_get.side_effect = self.api_traversal[0:2] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_stars_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API stars."""
        mock_get.side_effect = self.api_traversal[0:3] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_stars_KeyError(self, mock_get):
        """Test Star init with KeyError on API stars."""
        mock_get.side_effect = self.api_traversal[0:3] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_star_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API star."""
        mock_get.side_effect = self.api_traversal + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_star_KeyError(self, mock_get):
        """Test Star init with KeyError on API star."""
        mock_get.side_effect = self.api_traversal + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_property_id(self, mock_get):
        """Test id property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(idnum=1)]

        instance = Star(1, 1)
        expected_result = 1
        self.assertEqual(instance.id, expected_result)

    @mock.patch('requests.get')
    def test_property_name(self, mock_get):
        """Test name property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(name=id(sentinel.name))]

        instance = Star(1, 1)
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('requests.get')
    def test_property_mass(self, mock_get):
        """Test mass property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=id(sentinel.mass))]

        instance = Star(1, 1)
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

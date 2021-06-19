"""Tests solarsystem.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel

from tridentweb.solarsystem import SolarSystem


def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_text=None):
            self.json_data = json.loads(json_text)

        def json(self):
            """Returns JSON data of response."""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response."""
            return None

    return MockResponse(*args, **kwargs)


def mock_api_index():
    json_string = ("{\"_links\":"
                   "{\"trident-api:systems\":"
                   "{\"href\": \"https://api/systems\"}}}")
    return mocked_requests_get(json_text=json_string)


def mock_api_systems(idnum=0):
    id_string = "\"id\": {0}".format(idnum)
    json_string = ("{\"_embedded\":"
                   "{\"trident-api:system\":"
                   "[{" + id_string + ", \"_links\":"
                   "{\"self\":"
                   "{\"href\": \"https//api/systems/1\"}}}]}}")
    return mocked_requests_get(json_text=json_string)


def mock_api_system(idnum=0, name=""):
    json_string = ("{{\"id\": {0},"
                   "\"name\": \"{1}\"}}").format(idnum, name)
    return mocked_requests_get(json_text=json_string)


class TestSolarSystem(unittest.TestCase):
    """Unit tests against the Solar System object"""
    api_traversal = [
        mock_api_index(),
        mock_api_systems(idnum=id(sentinel.id)),
        mock_api_system(idnum=id(sentinel.id), name=str(id(sentinel.name)))
    ]

    @mock.patch('requests.get')
    def test_init(self, mock_get):
        """Test SolarSystem init"""
        mock_get.side_effect = self.api_traversal
        instance = SolarSystem(id(sentinel.id), "http://api/")
        expected_result = SolarSystem
        self.assertIsInstance(instance, expected_result)

    @mock.patch('requests.get')
    def test_property_id(self, mock_get):
        """Test ID property of Solar System"""
        mock_get.side_effect = self.api_traversal
        instance = SolarSystem(id(sentinel.id), "http://api/")
        expected_result = id(sentinel.id)
        self.assertEqual(instance.id, expected_result)

    @mock.patch('requests.get')
    def test_property_name(self, mock_get):
        """Test name property of Solar System"""
        mock_get.side_effect = self.api_traversal
        instance = SolarSystem(id(sentinel.id), "http://api/")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)


class IntegrationSolarSystem(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init(self):
        """Test Omega Hydri init"""
        instance = SolarSystem(1621827699)
        expected_result = SolarSystem
        self.assertIsInstance(instance, expected_result)

    def test_property_id(self):
        """Test Omega Hydri id"""
        instance = SolarSystem(1621827699)
        expected_result = 1621827699
        self.assertEqual(instance.id, expected_result)

    def test_property_name(self):
        """Test Omega Hydri name"""
        instance = SolarSystem(1621827699)
        expected_result = "Omega Hydri"
        self.assertEqual(instance.name, expected_result)

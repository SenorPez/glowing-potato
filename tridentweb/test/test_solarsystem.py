"""Tests solarsystem.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel, Mock

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


def mock_api_system(systemid=0, name=""):
    return {"id": systemid,
            "name": "{0}".format(name)}


class TestSolarSystem(unittest.TestCase):
    """Unit tests against the Solar System object"""
    @mock.patch('tridentweb.solarsystem.get_system')
    def test_init(self, _):
        """Test SolarSystem init"""
        instance = SolarSystem(id(sentinel.id), "https://api/")
        expected_result = SolarSystem
        self.assertIsInstance(instance, expected_result)

    @mock.patch('tridentweb.solarsystem.get_system')
    def test_property_id(self, mock_get):
        """Test ID property of Solar System"""
        attrs = {'json.return_value': mock_api_system(systemid=id(sentinel.id), name=str(id(sentinel.name)))}
        mock_get.return_value = Mock(**attrs)
        instance = SolarSystem(id(sentinel.id), "https://api/")
        expected_result = id(sentinel.id)
        self.assertEqual(instance.id, expected_result)

    @mock.patch('tridentweb.solarsystem.get_system')
    def test_property_name(self, mock_get):
        """Test name property of Solar System"""
        attrs = {'json.return_value': mock_api_system(systemid=id(sentinel.id), name=str(id(sentinel.name)))}
        mock_get.return_value = Mock(**attrs)
        instance = SolarSystem(id(sentinel.id), "https://api/")
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

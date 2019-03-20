"""Tests Planet.py

"""
import json
import unittest
from unittest import mock

from tridentweb.Planet import Planet

def mocked_requests_get(*args, **kwargs):
    """Defines a response suitable for mocking requests responses."""
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_string=None):
            if json_string is None:
                json_string = ("{}")
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


class IntegrationPlanet(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init_planet(self):
        """Test 1 Eta Veneris 3 init."""
        instance = Planet(1817514095, 1905216634, -455609026)
        expected_result = Planet
        self.assertIsInstance(instance, expected_result)

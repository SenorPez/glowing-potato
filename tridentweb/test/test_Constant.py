"""Tests Constant.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel

from tridentweb.Constant import Constant

def mocked_requests_get(*args, **kwargs):
    """Defines a response suitable for mocking requests responses."""
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_string=None, name="", value=0, units="", symbol=""):
            if json_string is None:
                json_string = ("{{"
                               "\"name\": \"{0}\","
                               "\"value\": {1},"
                               "\"units\": \"{2}\","
                               "\"symbol\": \"{3}\"}}").format(name, value, units, symbol)
            self.json_data = json.loads(json_string)

        def json(self):
            """Returns json data of response."""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response."""
            return None

    return MockResponse(*args, **kwargs)

class TestConstant(unittest.TestCase):
    """Unit tests against the Constant object."""
    api_traversal = [
        mocked_requests_get(json_string=( \
            "{\"_links\": {\"trident-api:constants\":"
            "{\"href\": \"http://trident.senorpez.com/constants\"}}}")),
        mocked_requests_get(json_string=( \
            "{\"_embedded\": {\"trident-api:constant\":"
            "[{\"symbol\": \"MC\","
            "\"_links\": {\"self\":"
            "{\"href\": \"http://trident.senorpez.com/constants/MC\"}}}]}}"))]

    @mock.patch('requests.get')
    def test_init(self, mock_get):
        """Test Constant init."""
        mock_get.side_effect = self.api_traversal \
            + [mocked_requests_get()]

        instance = Constant("MC")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)

    @mock.patch('requests.get')
    def test_property_name(self, mock_get):
        """Test name property of Constant."""
        mock_get.side_effect = self.api_traversal \
            + [mocked_requests_get(name=id(sentinel.name))]

        instance = Constant("MC")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('requests.get')
    def test_property_value(self, mock_get):
        """Test value property of Constant."""
        mock_get.side_effect = self.api_traversal \
            + [mocked_requests_get(value=id(sentinel.value))]

        instance = Constant("MC")
        expected_result = id(sentinel.value)
        self.assertEqual(instance.value, expected_result)

    @mock.patch('requests.get')
    def test_property_units(self, mock_get):
        """Test units property of Constant."""
        mock_get.side_effect = self.api_traversal \
            + [mocked_requests_get(units=id(sentinel.units))]

        instance = Constant("MC")
        expected_result = str(id(sentinel.units))
        self.assertEqual(instance.units, expected_result)

    @mock.patch('requests.get')
    def test_property_symbol(self, mock_get):
        """Test symbol property of Constant."""
        mock_get.side_effect = self.api_traversal \
            + [mocked_requests_get(symbol=id(sentinel.symbol))]

        instance = Constant("MC")
        expected_result = str(id(sentinel.symbol))
        self.assertEqual(instance.symbol, expected_result)

"""Tests Constant.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel

from requests.exceptions import HTTPError
from tridentweb.constant import Constant


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
        mocked_requests_get(json_string=(
            "{\"_links\": {\"trident-api:constants\":"
            "{\"href\": \"https://www.trident.senorpez.com/constants\"}}}")),
        mocked_requests_get(json_string=(
            "{\"_embedded\": {\"trident-api:constant\":"
            "[{\"symbol\": \"MC\","
            "\"_links\": {\"self\":"
            "{\"href\": \"https://www.trident.senorpez.com/constants/MC\"}}}]}}"))]

    @mock.patch('requests.get')
    def test_init(self, mock_get):
        """Test Constant init."""
        mock_get.side_effect = self.api_traversal \
            + [mocked_requests_get()]

        instance = Constant("MC")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)

    @mock.patch('requests.get')
    def test_init_index_HTTPError(self, mock_get):
        """Test Constant init with HTTPError on API index."""
        mock_get.side_effect = HTTPError("Error", None)
        with self.assertRaises(HTTPError):
            _ = Constant("MC")

    @mock.patch('requests.get')
    def test_init_index_KeyError(self, mock_get):
        """Test Constant init with KeyError on API index."""
        mock_get.side_effect = KeyError()
        with self.assertRaises(KeyError):
            _ = Constant("MC")

    @mock.patch('requests.get')
    def test_init_constants_HTTPError(self, mock_get):
        """Test Constant init with HTTPError on API constants."""
        mock_get.side_effect = [self.api_traversal[0], HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Constant("MC")

    @mock.patch('requests.get')
    def test_init_constants_KeyError(self, mock_get):
        """Test Constant init with KeyError on API constants."""
        mock_get.side_effect = [self.api_traversal[0], KeyError()]
        with self.assertRaises(KeyError):
            _ = Constant("MC")

    @mock.patch('requests.get')
    def test_init_constant_HTTPError(self, mock_get):
        """Test Constant init with HTTPError on API constant."""
        mock_get.side_effect = self.api_traversal + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Constant("MC")

    @mock.patch('requests.get')
    def test_init_constant_KeyError(self, mock_get):
        """Test Constant init with KeyError on API constants."""
        mock_get.side_effect = self.api_traversal + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Constant("MC")

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


class IntegrationConstant(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init_G(self):
        """Test Constant init."""
        instance = Constant("G")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)

    def test_name_G(self):
        """Test G name."""
        instance = Constant("G")
        expected_result = "Newtonian constant of gravitation"
        self.assertEqual(instance.name, expected_result)

    def test_value_G(self):
        """Test G value."""
        instance = Constant("G")
        expected_result = 6.67408e-11
        self.assertEqual(instance.value, expected_result)

    def test_units_G(self):
        """Test G units."""
        instance = Constant("G")
        expected_result = "m^3*kg^-1*s^-2"
        self.assertEqual(instance.units, expected_result)

    def test_symbol_G(self):
        """Test G symbol."""
        instance = Constant("G")
        expected_result = "G"
        self.assertEqual(instance.symbol, expected_result)

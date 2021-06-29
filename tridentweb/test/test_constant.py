"""Tests Constant.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel, Mock

from tridentweb.constant import Constant


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


def mock_api_constant(constantid="0", name="", value=0, units=""):
    return {"name": "{0}".format(name),
            "symbol": "{0}".format(constantid),
            "value": value,
            "units": "{0}".format(units)}


class TestConstant(unittest.TestCase):
    """Unit tests against the Constant object"""
    @mock.patch('tridentweb.constant.get_constant')
    def test_init(self, _):
        """Test Constant init"""
        instance = Constant(str(id(sentinel.id)), "https://api/")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)

    @mock.patch('tridentweb.constant.get_constant')
    def test_property_symbol(self, mock_get):
        """Test symbol property of Constant"""
        attrs = {'json.return_value': mock_api_constant(constantid=str(id(sentinel.id)),
                                                        name=str(id(sentinel.name)),
                                                        value=id(sentinel.value),
                                                        units=str(id(sentinel.units)))}
        mock_get.return_value = Mock(**attrs)
        instance = Constant(str(id(sentinel.id)), "https://api/")
        expected_result = str(id(sentinel.id))
        self.assertEqual(instance.symbol, expected_result)

    @mock.patch('tridentweb.constant.get_constant')
    def test_property_name(self, mock_get):
        """Test name property of Constant"""
        attrs = {'json.return_value': mock_api_constant(constantid=str(id(sentinel.id)),
                                                        name=str(id(sentinel.name)),
                                                        value=id(sentinel.value),
                                                        units=str(id(sentinel.units)))}
        mock_get.return_value = Mock(**attrs)
        instance = Constant(str(id(sentinel.id)), "https://api/")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('tridentweb.constant.get_constant')
    def test_property_value(self, mock_get):
        """Test value property of Constant"""
        attrs = {'json.return_value': mock_api_constant(constantid=str(id(sentinel.id)),
                                                        name=str(id(sentinel.name)),
                                                        value=id(sentinel.value),
                                                        units=str(id(sentinel.units)))}
        mock_get.return_value = Mock(**attrs)
        instance = Constant(str(id(sentinel.id)), "https://api/")
        expected_result = id(sentinel.value)
        self.assertEqual(instance.value, expected_result)

    @mock.patch('tridentweb.constant.get_constant')
    def test_property_units(self, mock_get):
        """Test units property of Constant"""
        attrs = {'json.return_value': mock_api_constant(constantid=str(id(sentinel.id)),
                                                        name=str(id(sentinel.name)),
                                                        value=id(sentinel.value),
                                                        units=str(id(sentinel.units)))}
        mock_get.return_value = Mock(**attrs)
        instance = Constant(str(id(sentinel.id)), "https://api/")
        expected_result = str(id(sentinel.units))
        self.assertEqual(instance.units, expected_result)


class IntegrationConstant(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init(self):
        """Test G init."""
        instance = Constant("G")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)

    def test_name(self):
        """Test G name."""
        instance = Constant("G")
        expected_result = "Newtonian constant of gravitation"
        self.assertEqual(instance.name, expected_result)

    def test_value(self):
        """Test G value."""
        instance = Constant("G")
        expected_result = 6.67408e-11
        self.assertEqual(instance.value, expected_result)

    def test_units(self):
        """Test G units."""
        instance = Constant("G")
        expected_result = "m^3*kg^-1*s^-2"
        self.assertEqual(instance.units, expected_result)

    def test_symbol(self):
        """Test G symbol."""
        instance = Constant("G")
        expected_result = "G"
        self.assertEqual(instance.symbol, expected_result)

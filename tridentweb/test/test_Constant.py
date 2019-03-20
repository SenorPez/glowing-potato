"""Tests Constant.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel

from tridentweb.Constant import Constant

class TestConstant(unittest.TestCase):
    """Unit tests against the Constant object."""
    def mocked_requests_get(*args, **kwargs):
        """Defines a response suitable for mocking requests responses."""
        class MockResponse:
            """Solution cribbed from
            https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
            """
            def __init__(self, *, name="", value=0, units=""):
                json_string = ("{{"
                               "\"name\": \"{0}\","
                               "\"value\": {1},"
                               "\"units\": \"{2}\"}}").format(name, value, units)
                self.json_data = json.loads(json_string)

            def json(self):
                """Returns json data of response."""
                return self.json_data

            def raise_for_status(self):
                """Return raise for status of response."""
                return None

        yield MockResponse(*args, **kwargs)


    @mock.patch('requests.get', side_effect=mocked_requests_get())
    def test_init(self, _):
        """Test Constant init."""
        instance = Constant("MC")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)


    @mock.patch('requests.get', side_effect=mocked_requests_get(name=id(sentinel.name)))
    def test_property_name(self, _):
        """Test name property of Constant."""
        instance = Constant("MC")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('requests.get', side_effect=mocked_requests_get(value=id(sentinel.value)))
    def test_property_value(self, _):
        """Test value property of Constant."""
        instance = Constant("MC")
        expected_result = id(sentinel.value)
        self.assertEqual(instance.value, expected_result)

    @mock.patch('requests.get', side_effect=mocked_requests_get(units=id(sentinel.units)))
    def test_property_units(self, _):
        """Test units property of Constant."""
        instance = Constant("MC")
        expected_result = str(id(sentinel.units))
        self.assertEqual(instance.units, expected_result)

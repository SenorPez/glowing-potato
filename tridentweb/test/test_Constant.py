"""Tests Constant.py

"""
import json
import sys
import unittest
from unittest import mock
from unittest.mock import sentinel

from requests.exceptions import HTTPError
from tridentweb.Constant import Constant

class TestConstant(unittest.TestCase):
    """Unit tests against the Constant object."""
    def mocked_requests_get(*args, **kwargs):
        class MockResponse:
            """Solution cribbed from
            https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
            """
            def __init__(self, name=""):
                self._name = "test"
                json_string = "{{\"name\": \"{0}\"}}".format(id(sentinel.name))
                self.json_data = json.loads(json_string)

            def json(self):
                return self.json_data

            def raise_for_status(self):
                return None

        yield MockResponse(*args)


    @mock.patch('requests.get', side_effect=mocked_requests_get())
    def test_init(self, mock_get):
        instance = Constant("MC")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)


    @mock.patch('requests.get', side_effect=mocked_requests_get(sentinel.name))
    def test_property_name(self, mock_get):
        instance = Constant("MC")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

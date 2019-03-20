"""Tests Constant.py

"""
import sys
import unittest
from unittest import mock

from tridentweb.Constant import Constant

class TestConstant(unittest.TestCase):
    """Unit tests against the Constant object."""
    def mocked_requests_get(*args, **kwargs):
        class MockResponse:
            """Solution cribbed from
            https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
            """
            def raise_for_status(self):
                return None

        return MockResponse()


    @mock.patch('requests.get', side_effect=mocked_requests_get)
    def test_init(self, mock_get):
        instance = Constant("MC")
        expected_result = Constant
        self.assertIsInstance(instance, expected_result)

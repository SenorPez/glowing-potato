"""Tests Constant.py

"""
import sys
import unittest
from unittest.mock import patch

from tridentweb.Constant import Constant

class TestConstant(unittest.TestCase):
    """Unit tests against the Constant object."""
    mock_constant = '{"name": "Mock Constant"}'

    def test_init(self):
        with patch('tridentweb.Constant.requests.get') as mock_get:
            mock_get.return_value = self.mock_constant
            instance = Constant("MC")
            expected_result = Constant
            self.assertIsInstance(instance, expected_result)

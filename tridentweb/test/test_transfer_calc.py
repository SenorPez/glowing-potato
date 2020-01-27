"""Tests the transfer calc module.

"""
from datetime import datetime
import unittest
from unittest import mock
from unittest.mock import patch, mock_open

from tridentweb.transfer_calc import transfer_calc, transfer_delta_v

class TestTransferDeltaV(unittest.TestCase):
    """Tests the transfer_delta_v function."""
    def test_transfer_delta_v(self):
        """Test using numbers from http://www.braeunig.us/space/, Problem 5.6"""
        vp = [25876.6, 13759.5, 0]
        vs = [28996.2, 15232.7, 1289.2]
        mu = 3.986005e14
        orbit_radius = 6578140

        expected_value = 3824.1
        self.assertAlmostEqual(transfer_delta_v(vp, vs, mu, orbit_radius), expected_value, 1)

class TestTransferCalc(unittest.TestCase):
    """Test the transfer_calc function."""
    def test_transfer_calc(self):
        with patch('tridentweb.transfer_calc.gzip.open', mock_open()) as mocked_file:
            transfer_calc()
            mocked_file.assert_called_once_with(mock.ANY, 'wt')
            mocked_file().write.assert_called_once_with(mock.ANY)

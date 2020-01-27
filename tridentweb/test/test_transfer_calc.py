"""Tests the transfer calc module.

"""
import unittest

from tridentweb.transfer_calc import transfer_delta_v

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

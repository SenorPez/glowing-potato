"""Tests the epoch offset module."""
from math import pi
import unittest
from unittest import mock
from unittest.mock import sentinel

from tridentweb.epoch_offset import epoch_offset

class TestEpochOffset(unittest.TestCase):
    """Tests for the epoch_offset function."""
    def test_epoch_offset_angle_overflow(self):
        """Test epoch offset with angle over pi."""
        with self.assertRaises(ValueError):
            epoch_offset(sentinel.planet, target_angle=pi + 1)

    def test_epoch_offset_angle_lower_bound(self):
        """Test epoch offset with angle at lower bound."""
        with self.assertRaises(ValueError):
            epoch_offset(sentinel.planet, target_angle=-pi)

    def test_epoch_offset_bounds_reversed(self):
        """Test epoch offset with bounds reversed."""
        with self.assertRaises(ValueError):
            epoch_offset(sentinel.planet, low_bound=1, high_bound=-1)

    def test_epoch_offset_bounds_identical(self):
        """Test epcoh offset with identical bounds."""
        with self.assertRaises(ValueError):
            epoch_offset(sentinel.planet, low_bound=42, high_bound=42)

    @mock.patch('pykep.planet.keplerian')
    def test_epoch_offset_converge(self, mock_planet):
        """Test converging epoch offset."""
        mock_planet.eph.side_effect = [
            ((0, 1, 0), 0),
            ((1, 1, 0), 0),
            ((1, 1, 0), 0),
            ((0.92, 0.38, 0), 0),
            ((0.92, 0.38, 0), 0),
            ((0.92, 0.38, 0), 0)]
        offset, _ = epoch_offset(
            mock_planet,
            target_angle=pi / 4,
            low_bound=0,
            high_bound=4,
            iterations=3)
        self.assertEqual(offset, 0.5)

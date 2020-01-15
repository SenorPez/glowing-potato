"""Tests the pykep_addons module."""
from random import randrange
import unittest
from unittest import mock

from tridentweb.pykep_addons import orbit_positions

class TestOrbitPositions(unittest.TestCase):
    """Tests for the orbit_positions function."""
    @staticmethod
    def generate_test_data():
        """Generates ridicuous 'orbit' test data."""
        ephemerides = []
        expected_x = []
        expected_y = []
        expected_z = []
        for _ in range(8):
            value_x = randrange(10)
            expected_x.append(value_x)

            value_y = randrange(10)
            expected_y.append(value_y)

            value_z = randrange(10)
            expected_z.append(value_z)
            ephemerides.append([[value_x, value_y, value_z], None])

        return ephemerides, expected_x, expected_y, expected_z

    @mock.patch('pykep.planet._base')
    def test_orbit_positions_number(self, mock_planet):
        """Test orbit positions with a number passed to generate an epoch."""
        ephemerides, expected_x, expected_y, expected_z = self.generate_test_data()

        mock_planet.compute_period.return_value = 42
        mock_planet.eph.side_effect = ephemerides

        actual_x, actual_y, actual_z = orbit_positions(mock_planet, N=8)
        self.assertListEqual(actual_x.tolist(), expected_x)
        self.assertListEqual(actual_y.tolist(), expected_y)
        self.assertListEqual(actual_z.tolist(), expected_z)

    @mock.patch('pykep.epoch')
    @mock.patch('pykep.planet._base')
    def test_orbit_positions_epoch(self, mock_planet, mock_epoch):
        """Test orbit positions with an epoch object passed."""
        ephemerides, expected_x, expected_y, expected_z = self.generate_test_data()

        mock_planet.compute_period.return_value = 42
        mock_planet.eph.side_effect = ephemerides

        actual_x, actual_y, actual_z = orbit_positions(mock_planet, mock_epoch, N=8)
        self.assertListEqual(actual_x.tolist(), expected_x)
        self.assertListEqual(actual_y.tolist(), expected_y)
        self.assertListEqual(actual_z.tolist(), expected_z)

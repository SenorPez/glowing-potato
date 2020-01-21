"""Tests the pykep_addons module."""
from random import randrange
import unittest
from unittest import mock

from pykep import epoch
from tridentweb.pykep_addons import lambert_positions, orbit_positions, eccentric_from_true, \
    mean_from_true, mean_from_eccentric

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

class TestEccentricFromTrue(unittest.TestCase):
    """Tests for the eccentric_from_true function.
        Numbers taken from http://www.braeunig.us/space/"""
    def test_eccentric_from_true(self):
        """Test function."""
        eccentricity = 0.1
        true_anomaly = 0.523599
        expected_value = 0.4755680

        self.assertAlmostEqual(eccentric_from_true(eccentricity, true_anomaly), expected_value)

class TestMeanFromEccentric(unittest.TestCase):
    """Test for the mean_from_eccentric function.
        Numbers taken from http://www.braeunig.us/space/"""
    def test_mean_from_eccentric(self):
        """Test function."""
        eccentricity = 0.1
        eccentric_anomaly = 0.4755680
        expected_value = 0.4297837

        self.assertAlmostEqual(mean_from_eccentric(eccentricity, eccentric_anomaly), expected_value)

class TestMeanFromTrue(unittest.TestCase):
    """Test for the mean_from_true function.
        Numbers taken from http://www.braeunig.us/space/"""
    def test_mean_from_true(self):
        """Test function."""
        eccentricity = 0.1
        true_anomaly = 0.523599
        expected_value = 0.4297837

        self.assertAlmostEqual(mean_from_true(eccentricity, true_anomaly), expected_value)

class TestOrbitPositions(unittest.TestCase):
    """Tests for the orbit_positions function."""
    @mock.patch('pykep.planet._base')
    def test_orbit_positions_number(self, mock_planet):
        """Test orbit positions with a number passed to generate an epoch."""
        ephemerides, expected_x, expected_y, expected_z = generate_test_data()

        mock_planet.compute_period.return_value = 42
        mock_planet.eph.side_effect = ephemerides

        actual_x, actual_y, actual_z = orbit_positions(mock_planet, N=8)
        self.assertListEqual(actual_x.tolist(), expected_x)
        self.assertListEqual(actual_y.tolist(), expected_y)
        self.assertListEqual(actual_z.tolist(), expected_z)

    @mock.patch('pykep.planet._base')
    def test_orbit_positions_epoch(self, mock_planet):
        """Test orbit positions with an epoch object passed."""
        ephemerides, expected_x, expected_y, expected_z = generate_test_data()

        mock_planet.compute_period.return_value = 42
        mock_planet.eph.side_effect = ephemerides

        actual_x, actual_y, actual_z = orbit_positions(mock_planet, epoch(0), N=8)
        self.assertListEqual(actual_x.tolist(), expected_x)
        self.assertListEqual(actual_y.tolist(), expected_y)
        self.assertListEqual(actual_z.tolist(), expected_z)

class TestLambertPositions(unittest.TestCase):
    """Tests for the lambert_positions function."""
    @mock.patch('pykep.lambert_problem')
    @mock.patch('tridentweb.pykep_addons.propagate_lagrangian')
    def test_lambert_positions(self, mock_prop, mock_lambert):
        """Test lambert positions."""
        ephemerides, expected_x, expected_y, expected_z = generate_test_data()
        initial_r = ephemerides[0]
        mock_prop.side_effect = ephemerides[1:] + [(None, None)]

        mock_lambert.get_Nmax.return_value = 1
        mock_lambert.get_r1.return_value = [initial_r[0][0], initial_r[0][1], initial_r[0][2]]
        mock_lambert.get_v1.return_value = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
        mock_lambert.get_tof.return_value = 50
        mock_lambert.get_mu.return_value = 1

        actual_x, actual_y, actual_z = lambert_positions(mock_lambert, N=8)
        self.assertListEqual(actual_x.tolist(), expected_x)
        self.assertListEqual(actual_y.tolist(), expected_y)
        self.assertListEqual(actual_z.tolist(), expected_z)

    @mock.patch('pykep.lambert_problem')
    def test_lambert_positions_invalid_sol(self, mock_lambert):
        """Test lambert positions with an invalid solution number."""
        mock_lambert.get_Nmax.return_value = 1

        with self.assertRaises(ValueError):
            _ = lambert_positions(mock_lambert, sol=42)

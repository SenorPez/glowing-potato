"""Tests the plotting module."""
import unittest
from unittest import mock
from unittest.mock import MagicMock

import numpy as np
from pykep import AU

from tridentweb.plotting import plot_orbits, plot_transfer

class TestPlotOrbits(unittest.TestCase):
    """Tests for the plot_orbits function."""
    @mock.patch('tridentweb.plotting.orbit_positions')
    def test_plot_orbits(self, mock_orbit_positions):
        """Test plot_orbits."""
        green_planet = MagicMock()
        green_planet.id = -455609026
        green_planet.name = "Green"

        orange_planet = MagicMock()
        orange_planet.id = -1385166447
        orange_planet.name = "Orange"

        gray_planet = MagicMock()
        gray_planet.id = 8675309
        gray_planet.name = "Gray"

        mock_orbit_positions.return_value = np.array([[AU, AU, AU], [AU, AU, AU], [AU, AU, AU]])

        expected_x, expected_y, expected_z, expected_positions, expected_colors, expected_names\
            = plot_orbits([green_planet, orange_planet, gray_planet])

        self.assertEqual(
            expected_x,
            [[1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0]])
        self.assertEqual(
            expected_y,
            [[1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0]])
        self.assertEqual(
            expected_z,
            [[1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0]])
        self.assertEqual(
            expected_positions,
            [[1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0], [1.0, 1.0, 1.0]])
        # self.assertEqual(expected_colors, ["green", "orange", "gray", "blue"])
        self.assertEqual(expected_names, ["Green", "Orange", "Gray", "Earth"])

    @mock.patch('matplotlib.figure.Figure.savefig')
    def test_plot_transfer(self, mock_savefig):
        """Test plot_transfer."""
        flask_values = {'launch_time': 0, 'flight_time': 10}
        plot_transfer(flask_values)
        mock_savefig.assert_called()
        self.assertEqual(4, mock_savefig.call_count)

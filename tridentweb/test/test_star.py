"""Tests star.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel, Mock

import pykep

from tridentweb.star import Star


def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """

        def __init__(self, *, json_text=None):
            self.json_data = json.loads(json_text)

        def json(self):
            """Returns JSON data of response"""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response"""
            return None

    return MockResponse(*args, **kwargs)


def mock_api_star(star_id=0, name="", mass=None, semimajor_axis=None, eccentricity=None,
                  inclination=None, longitude_of_ascending_node=None,
                  argument_of_periapsis=None, true_anomaly_at_epoch=None):
    return {"id": star_id,
            "name": "{0}".format(name),
            "mass": mass,
            "semimajorAxis": semimajor_axis,
            "eccentricity": eccentricity,
            "inclination": inclination,
            "longitudeOfAscendingNode": longitude_of_ascending_node,
            "argumentOfPeriapsis": argument_of_periapsis,
            "trueAnomalyAtEpoch": true_anomaly_at_epoch}


class TestStar(unittest.TestCase):
    """Unit tests against the Star object"""
    @mock.patch('tridentweb.star.get_star')
    def test_init_primary(self, mock_get):
        """Test Star init as primary"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_id_primary(self, mock_get):
        """Test ID property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.starid)
        self.assertEqual(instance.id, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_name_primary(self, mock_get):
        """Test name property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_mass_primary(self, mock_get):
        """Test mass property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_semimajor_axis_primary(self, mock_get):
        """Test semimajor axis property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        self.assertIsNone(instance.semimajor_axis)

    @mock.patch('tridentweb.star.get_star')
    def test_property_eccentricity_primary(self, mock_get):
        """Test eccentricity property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        self.assertIsNone(instance.eccentricity)

    @mock.patch('tridentweb.star.get_star')
    def test_property_inclination_primary(self, mock_get):
        """Test inclination property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        self.assertIsNone(instance.inclination)

    @mock.patch('tridentweb.star.get_star')
    def test_property_longitude_of_ascending_node_primary(self, mock_get):
        """Test longitude of ascending node property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        self.assertIsNone(instance.longitude_of_ascending_node)

    @mock.patch('tridentweb.star.get_star')
    def test_property_argument_of_periapsis_primary(self, mock_get):
        """Test argument of periapsis property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        self.assertIsNone(instance.argument_of_periapsis)

    @mock.patch('tridentweb.star.get_star')
    def test_property_true_anomaly_at_epoch_primary(self, mock_get):
        """Test true anomaly at epoch property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        self.assertIsNone(instance.true_anomaly_at_epoch)

    @mock.patch('tridentweb.star.get_star')
    def test_property_gm_primary(self, mock_get):
        """Test GM property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.mass) * 6.67408e-11 * 1.9884e+30
        self.assertAlmostEqual(instance.gm, expected_result, delta=1.0e+17)

    @mock.patch('tridentweb.star.get_star')
    def test_property_planet_primary(self, mock_get):
        """Test planet property of primary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass))}
        mock_get.return_value = (Mock(**attrs), None)
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        with self.assertRaises(ValueError):
            _ = instance.planet

    @mock.patch('tridentweb.star.get_star')
    def test_init_secondary(self, mock_get):
        """Test Star init as secondary"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_id_secondary(self, mock_get):
        """Test ID property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.starid)
        self.assertEqual(instance.id, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_name_secondary(self, mock_get):
        """Test name property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_mass_secondary(self, mock_get):
        """Test mass property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_semimajor_axis_secondary(self, mock_get):
        """Test semimajor axis property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.sma)
        self.assertEqual(instance.semimajor_axis, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_eccentricity_secondary(self, mock_get):
        """Test eccentricity property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.ecc)
        self.assertEqual(instance.eccentricity, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_inclination_secondary(self, mock_get):
        """Test inclination property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.inc)
        self.assertEqual(instance.inclination, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_longitude_of_ascending_node_secondary(self, mock_get):
        """Test longitude of ascending node property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.loan)
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_argument_of_periapsis_secondary(self, mock_get):
        """Test argument of periapsis property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.aop)
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_true_anomaly_at_epoch_secondary(self, mock_get):
        """Test true anomaly at epoch property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.taoe)
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

    @mock.patch('tridentweb.star.get_star')
    def test_property_gm_secondary(self, mock_get):
        """Test GM property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   id(sentinel.ecc),
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = id(sentinel.mass) * 6.67408e-11 * 1.9884e+30
        self.assertAlmostEqual(instance.gm, expected_result, delta=1.0e+17)

    @mock.patch('tridentweb.star.get_star')
    def test_property_planet_secondary(self, mock_get):
        """Test planet property of secondary Star"""
        attrs = {'json.return_value':
                     mock_api_star(id(sentinel.starid),
                                   str(id(sentinel.name)),
                                   id(sentinel.mass),
                                   id(sentinel.sma),
                                   0.5,
                                   id(sentinel.inc),
                                   id(sentinel.loan),
                                   id(sentinel.aop),
                                   id(sentinel.taoe))}
        mock_get.return_value = (Mock(**attrs), id(sentinel.primarymass))
        instance = Star(id(sentinel.systemid), id(sentinel.starid), "https://api/")
        expected_result = pykep.planet.keplerian
        self.assertIsInstance(instance.planet, expected_result)


class IntegrationStar(unittest.TestCase):
    """Integration tests against reference implementation of Trident API"""

    def test_init_primary(self):
        """Test 1 Eta Veneris init"""
        instance = Star(1817514095, 1905216634)
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    def test_property_id_primary(self):
        """Test 1 Eta Veneris id"""
        instance = Star(1817514095, 1905216634)
        expected_result = 1905216634
        self.assertEqual(instance.id, expected_result)

    def test_property_name_primary(self):
        """Test 1 Eta Veneris name"""
        instance = Star(1817514095, 1905216634)
        expected_result = "1 Eta Veneris"
        self.assertEqual(instance.name, expected_result)

    def test_property_mass_primary(self):
        """Test 1 Eta Veneris mass"""
        instance = Star(1817514095, 1905216634)
        expected_result = 0.75
        self.assertEqual(instance.mass, expected_result)

    def test_property_semimajor_axis_primary(self):
        """Test 1 Eta Veneris semimajor axis"""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.semimajor_axis)

    def test_property_eccentricity_primary(self):
        """Test 1 Eta Veneris eccentricity"""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.eccentricity)

    def test_property_inclination_primary(self):
        """Test 1 Eta Veneris inclination"""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.inclination)

    def test_property_longitude_of_ascending_node_primary(self):
        """Test 1 Eta Veneris longitude of ascending node"""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.longitude_of_ascending_node)

    def test_property_argument_of_periapsis_primary(self):
        """Test 1 Eta Veneris argument of periapsis"""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.argument_of_periapsis)

    def test_property_true_anomaly_at_epoch_primary(self):
        """Test 1 Eta Veneris true anomaly at epoch"""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.true_anomaly_at_epoch)

    def test_property_gm_primary(self):
        """Test 1 Eta Veneris GM"""
        instance = Star(1817514095, 1905216634)
        expected_result = 9.953055504e+19
        self.assertAlmostEqual(instance.gm, expected_result)

    def test_property_planet_primary(self):
        """Test 1 Eta Veneris planet"""
        instance = Star(1817514095, 1905216634)
        with self.assertRaises(ValueError):
            _ = instance.planet

    def test_init_secondary(self):
        """Test 2 Eta Veneris init"""
        instance = Star(1817514095, -1385166447)
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    def test_property_id_secondary(self):
        """Test 2 Eta Veneris id"""
        instance = Star(1817514095, -1385166447)
        expected_result = -1385166447
        self.assertEqual(instance.id, expected_result)

    def test_property_name_secondary(self):
        """Test 2 Eta Veneris name"""
        instance = Star(1817514095, -1385166447)
        expected_result = "2 Eta Veneris"
        self.assertEqual(instance.name, expected_result)

    def test_property_mass_secondary(self):
        """Test 2 Eta Veneris mass"""
        instance = Star(1817514095, -1385166447)
        expected_result = 0.75
        self.assertEqual(instance.mass, expected_result)

    def test_property_semimajor_axis_secondary(self):
        """Test 2 Eta Veneris semimajor axis"""
        instance = Star(1817514095, -1385166447)
        expected_result = 70.0
        self.assertEqual(instance.semimajor_axis, expected_result)

    def test_property_eccentricity_secondary(self):
        """Test 2 Eta Veneris eccentricity"""
        instance = Star(1817514095, -1385166447)
        expected_result = 0.5
        self.assertEqual(instance.eccentricity, expected_result)

    def test_property_inclination_secondary(self):
        """Test 2 Eta Veneris inclination"""
        instance = Star(1817514095, -1385166447)
        expected_result = 0.006273935
        self.assertEqual(instance.inclination, expected_result)

    def test_property_longitude_of_ascending_node_secondary(self):
        """Test 2 Eta Veneris longitude of ascending node"""
        instance = Star(1817514095, -1385166447)
        expected_result = 4.8210096
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    def test_property_argument_of_periapsis_secondary(self):
        """Test 2 Eta Veneris argument of periapsis"""
        instance = Star(1817514095, -1385166447)
        expected_result = 2.9558303
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    def test_property_true_anomaly_at_epoch_secondary(self):
        """Test 2 Eta Veneris true anomaly at epoch"""
        instance = Star(1817514095, -1385166447)
        expected_result = 6.0167522
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

    def test_property_gm_secondary(self):
        """Test 2 Eta Veneris GM"""
        instance = Star(1817514095, -1385166447)
        expected_result = 9.953055504e+19
        self.assertAlmostEqual(instance.gm, expected_result)

    def test_property_planet_secondary(self):
        """Test 2 Eta Veneris planet"""
        instance = Star(1817514095, -1385166447)
        expected_result = pykep.planet.keplerian
        self.assertIsInstance(instance.planet, expected_result)

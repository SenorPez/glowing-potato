"""Tests star.py

"""
import json
import unittest
from unittest import mock
from unittest.mock import sentinel, MagicMock, PropertyMock

from pykep.planet import keplerian
from requests.exceptions import HTTPError
from tridentweb.star import Star


def mocked_requests_get(*args, **kwargs):
    """Defines a response suitable for mocking requests responses."""
    class MockResponse:
        """Solution cribbed from
        https://stackoverflow.com/questions/15753390/how-can-i-mock-requests-and-the-response/28507806#28507806
        """
        def __init__(self, *, json_string=None, idnum=0, name="", mass=0,
                     semimajor_axis=0, eccentricity=0, inclination=0,
                     longitude_of_ascending_node=0, argument_of_periapsis=0,
                     true_anomaly_at_epoch=0):
            if json_string is None:
                json_string = ("{{\"id\": {0},"
                               "\"name\": \"{1}\","
                               "\"mass\": {2},"
                               "\"semimajorAxis\": {3},"
                               "\"eccentricity\": {4},"
                               "\"inclination\": {5},"
                               "\"longitudeOfAscendingNode\": {6},"
                               "\"argumentOfPeriapsis\": {7},"
                               "\"trueAnomalyAtEpoch\": {8}}}"
                              ).format(idnum, name, mass, semimajor_axis,
                                       eccentricity, inclination, longitude_of_ascending_node,
                                       argument_of_periapsis, true_anomaly_at_epoch)
            self.json_data = json.loads(json_string)

        def json(self):
            """Returns json data of response."""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response."""
            return None

    return MockResponse(*args, **kwargs)


class TestStar(unittest.TestCase):
    """Unit tests against the Star object."""
    api_traversal = [
        mocked_requests_get(json_string=(
            "{\"_links\": {\"trident-api:systems\":"
            "{\"href\": \"https://www.trident.senorpez.com/systems\"}}}")),
        mocked_requests_get(json_string=(
            "{\"_embedded\": {\"trident-api:system\":"
            "[{\"id\": 1,"
            "\"_links\": {\"self\":"
            "{\"href\": \"https://www.trident.senorpez.com/systems/1\"}}}]}}")),
        mocked_requests_get(json_string=(
            "{\"_links\": {\"trident-api:stars\":"
            "{\"href\": \"https://www.trident.senorpez.com/stars\"}}}")),
        mocked_requests_get(json_string=(
            "{\"_embedded\": {\"trident-api:star\":"
            "[{\"id\": 1,"
            "\"_links\": {\"self\":"
            "{\"href\": \"https://www.trident.senorpez.com/stars/1\"}}}]}}"))]

    constant_api_traversal = [
        mocked_requests_get(json_string=(
            "{\"_links\": {\"trident-api:constants\":"
            "{\"href\": \"https://www.trident.senorpez.com/constants\"}}}")),
        mocked_requests_get(json_string=(
            "{\"_embedded\": {\"trident-api:constant\":"
            "[{\"symbol\": \"MC\","
            "\"_links\": {\"self\":"
            "{\"href\": \"https://www.trident.senorpez.com/constants/MC\"}}}]}}"))]

    @mock.patch('requests.get')
    def test_init(self, mock_get):
        """Test Star init."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    @mock.patch('requests.get')
    def test_init_index_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API index."""
        mock_get.side_effect = HTTPError("Error", None)
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_index_KeyError(self, mock_get):
        """Test Star init with KeyError on API index."""
        mock_get.side_effect = KeyError()
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_systems_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API systems."""
        mock_get.side_effect = self.api_traversal[0:1] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_systems_KeyError(self, mock_get):
        """Test Star init with KeyError on API systems."""
        mock_get.side_effect = self.api_traversal[0:1] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_system_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API system."""
        mock_get.side_effect = self.api_traversal[0:2] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_system_KeyError(self, mock_get):
        """Test Star init with KeyError on API system."""
        mock_get.side_effect = self.api_traversal[0:2] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_stars_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API stars."""
        mock_get.side_effect = self.api_traversal[0:3] + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_stars_KeyError(self, mock_get):
        """Test Star init with KeyError on API stars."""
        mock_get.side_effect = self.api_traversal[0:3] + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_star_HTTPError(self, mock_get):
        """Test Star init with HTTPError on API star."""
        mock_get.side_effect = self.api_traversal + [HTTPError("Error", None)]
        with self.assertRaises(HTTPError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_init_star_KeyError(self, mock_get):
        """Test Star init with KeyError on API star."""
        mock_get.side_effect = self.api_traversal + [KeyError()]
        with self.assertRaises(KeyError):
            _ = Star(1, 1)

    @mock.patch('requests.get')
    def test_property_id(self, mock_get):
        """Test id property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(idnum=1)]

        instance = Star(1, 1)
        expected_result = 1
        self.assertEqual(instance.id, expected_result)

    @mock.patch('requests.get')
    def test_property_name(self, mock_get):
        """Test name property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(name=id(sentinel.name))]

        instance = Star(1, 1)
        expected_result = str(id(sentinel.name))
        self.assertEqual(instance.name, expected_result)

    @mock.patch('requests.get')
    def test_property_mass(self, mock_get):
        """Test mass property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=id(sentinel.mass))]

        instance = Star(1, 1)
        expected_result = id(sentinel.mass)
        self.assertEqual(instance.mass, expected_result)

    @mock.patch('requests.get')
    def test_property_semimajor_axis_primary(self, mock_get):
        """Test semimajor axis property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        self.assertIsNone(instance.semimajor_axis)

    @mock.patch('requests.get')
    def test_property_semimajor_axis_secondary(self, mock_get):
        """Test semimajor axis property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(semimajor_axis=id(sentinel.semimajor_axis))]
        mock_star = MagicMock()

        instance = Star(1, 1, mock_star)
        expected_result = id(sentinel.semimajor_axis)
        self.assertEqual(instance.semimajor_axis, expected_result)

    @mock.patch('requests.get')
    def test_property_eccentricity_primary(self, mock_get):
        """Test eccentricity property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        self.assertIsNone(instance.eccentricity)

    @mock.patch('requests.get')
    def test_property_eccentricity_secondary(self, mock_get):
        """Test eccentricity property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(eccentricity=id(sentinel.eccentricity))]
        mock_star = MagicMock()

        instance = Star(1, 1, mock_star)
        expected_result = id(sentinel.eccentricity)
        self.assertEqual(instance.eccentricity, expected_result)

    @mock.patch('requests.get')
    def test_property_inclination_primary(self, mock_get):
        """Test inclination property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        self.assertIsNone(instance.inclination)

    @mock.patch('requests.get')
    def test_property_inclination_secondary(self, mock_get):
        """Test inclination property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(inclination=id(sentinel.eccentricity))]
        mock_star = MagicMock()

        instance = Star(1, 1, mock_star)
        expected_result = id(sentinel.eccentricity)
        self.assertEqual(instance.inclination, expected_result)

    @mock.patch('requests.get')
    def test_property_longitude_of_ascending_node_primary(self, mock_get):
        """Test longitude of ascending node property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        self.assertIsNone(instance.longitude_of_ascending_node)

    @mock.patch('requests.get')
    def test_property_longitude_of_ascending_node_secondary(self, mock_get):
        """Test longitude of ascending node property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(
                    longitude_of_ascending_node=id(sentinel.longitude_of_ascending_node))]
        mock_star = MagicMock()

        instance = Star(1, 1, mock_star)
        expected_result = id(sentinel.longitude_of_ascending_node)
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    @mock.patch('requests.get')
    def test_property_argument_of_periapsis_primary(self, mock_get):
        """Test argument of periapsis property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        self.assertIsNone(instance.argument_of_periapsis)

    @mock.patch('requests.get')
    def test_property_argument_of_periapsis_secondary(self, mock_get):
        """Test argument of periapsis property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(argument_of_periapsis=id(sentinel.argument_of_periapsis))]
        mock_star = MagicMock()

        instance = Star(1, 1, mock_star)
        expected_result = id(sentinel.argument_of_periapsis)
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    @mock.patch('requests.get')
    def test_property_true_anomaly_at_epoch_primary(self, mock_get):
        """Test true anomaly at epoch property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        self.assertIsNone(instance.true_anomaly_at_epoch)

    @mock.patch('requests.get')
    def test_property_true_anomaly_at_epoch_secondary(self, mock_get):
        """Test true anomaly at epoch property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(true_anomaly_at_epoch=id(sentinel.true_anomaly_at_epoch))]
        mock_star = MagicMock()

        instance = Star(1, 1, mock_star)
        expected_result = id(sentinel.true_anomaly_at_epoch)
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

    @mock.patch('tridentweb.star.Constant')
    @mock.patch('requests.get')
    def test_property_gm(self, mock_get, mock_constant):
        """Test gm property of Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=0.75)]

        mock_grav = MagicMock()
        type(mock_grav).value = PropertyMock(return_value=6.67408e-11)

        mock_solar_mass = MagicMock()
        type(mock_solar_mass).value = PropertyMock(return_value=1.9884e30)

        mock_constant.side_effect = [mock_solar_mass, mock_grav]

        instance = Star(1, 1)
        expected_result = 0.75 * 1.9884e30 * 6.67408e-11
        self.assertEqual(instance.gm, expected_result)

    @mock.patch('requests.get')
    def test_property_planet_primary(self, mock_get):
        """Test planet property of primary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get()]

        instance = Star(1, 1)
        with self.assertRaises(ValueError):
            _ = instance.planet

    @mock.patch('tridentweb.star.Constant')
    @mock.patch('requests.get')
    def test_property_planet_secondary(self, mock_get, mock_constant):
        """Test planet property of secondary Star."""
        mock_get.side_effect = self.api_traversal \
                + [mocked_requests_get(mass=0.75, semimajor_axis=1)]

        mock_star = MagicMock()
        type(mock_star).gm = mock.PropertyMock(return_value=0.75 * 1.9884e30 * 6.67408e-11)

        mock_grav = MagicMock()
        type(mock_grav).value = PropertyMock(return_value=6.67408e-11)

        mock_solar_mass = MagicMock()
        type(mock_solar_mass).value = PropertyMock(return_value=1.9884e30)

        mock_constant.side_effect = [mock_solar_mass, mock_grav]

        instance = Star(1, 1, mock_star)
        expected_result = keplerian
        self.assertIsInstance(instance.planet, expected_result)


class IntegrationStar(unittest.TestCase):
    """Integration tests against reference implementation of Trident API."""
    def test_init_star_primary(self):
        """Test 1 Eta Veneris init."""
        instance = Star(1817514095, 1905216634)
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    def test_property_id_primary(self):
        """Test 1 Eta Veneris id."""
        instance = Star(1817514095, 1905216634)
        expected_result = 1905216634
        self.assertEqual(instance.id, expected_result)

    def test_property_name_primary(self):
        """Test 1 Eta Veneris name."""
        instance = Star(1817514095, 1905216634)
        expected_result = "1 Eta Veneris"
        self.assertEqual(instance.name, expected_result)

    def test_property_mass_primary(self):
        """Test 1 Eta Veneris mass."""
        instance = Star(1817514095, 1905216634)
        expected_result = 0.75
        self.assertEqual(instance.mass, expected_result)

    def test_property_semimajor_axis_primary(self):
        """Test 1 Eta Veneris semimajor axis."""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.semimajor_axis)

    def test_property_eccentricity_primary(self):
        """Test 1 Eta Veneris eccentricity."""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.eccentricity)

    def test_property_inclination_primary(self):
        """Test 1 Eta Veneris inclination."""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.inclination)

    def test_property_longitude_of_ascending_node_primary(self):
        """Test 1 Eta Veneris longitude of ascending node."""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.longitude_of_ascending_node)

    def test_property_argument_of_periapsis_primary(self):
        """Test 1 Eta Veneris argument of periapsis."""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.argument_of_periapsis)

    def test_property_true_anomaly_at_epoch_primary(self):
        """Test 1 Eta Veneris true anomaly at epoch."""
        instance = Star(1817514095, 1905216634)
        self.assertIsNone(instance.true_anomaly_at_epoch)

    def test_init_star_secondary(self):
        """Test 2 Eta Veneris init."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = Star
        self.assertIsInstance(instance, expected_result)

    def test_property_id_secondary(self):
        """Test 2 Eta Veneris id."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = -1385166447
        self.assertEqual(instance.id, expected_result)

    def test_property_name_secondary(self):
        """Test 2 Eta Veneris name."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = "2 Eta Veneris"
        self.assertEqual(instance.name, expected_result)

    def test_property_mass_secondary(self):
        """Test 2 Eta Veneris mass."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 0.75
        self.assertEqual(instance.mass, expected_result)

    def test_property_semimajor_axis_secondary(self):
        """Test 2 Eta Veneris semimajor axis."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 70.0
        self.assertEqual(instance.semimajor_axis, expected_result)

    def test_property_eccentricity_secondary(self):
        """Test 2 Eta Veneris eccentricity."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 0.5
        self.assertEqual(instance.eccentricity, expected_result)

    def test_property_inclination_secondary(self):
        """Test 2 Eta Veneris inclination."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 0.006273935
        self.assertEqual(instance.inclination, expected_result)

    def test_property_longitude_of_ascending_node_secondary(self):
        """Test 2 Eta Veneris longitude of ascending node."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 4.8210096
        self.assertEqual(instance.longitude_of_ascending_node, expected_result)

    def test_property_argument_of_periapsis_secondary(self):
        """Test 2 Eta Veneris argument of periapsis."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 2.9558303
        self.assertEqual(instance.argument_of_periapsis, expected_result)

    def test_property_true_anomaly_at_epoch_secondary(self):
        """Test 2 Eta Veneris true anomaly at epoch."""
        instance = Star(1817514095, -1385166447, Star(1817514095, 1905216634))
        expected_result = 6.0167522
        self.assertEqual(instance.true_anomaly_at_epoch, expected_result)

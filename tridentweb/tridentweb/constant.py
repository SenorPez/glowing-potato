"""Provides a Constant class for using constants from the Trident API.

"""

from tridentweb.api import get_constant


class Constant:
    """Represents a constant

    :param constant_id: Constant ID, for use with the Trident API
    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    """
    def __init__(self, constant_id, server_url="https://www.trident.senorpez.com/"):
        constant_response = get_constant(constant_id, server_url)
        self.name = constant_response.json()['name']
        self.value = constant_response.json()['value']
        self.units = constant_response.json()['units']
        self.symbol = constant_response.json()['symbol']

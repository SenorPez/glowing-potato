"""Provides a Constant class for using constants from the Trident API.

"""
import requests

class Constant:
    """Represents a constant.

    Arguments:
    constant_symbol - Symbol denoting the constant.
    server_url - Trident API server URL. Defaults to http://trident.senorpez.com/constants/
    """
    def __init__(self, constant_symbol, server_url="http://trident.senorpez.com/constants/"):
        req = requests.get(server_url + str(constant_symbol))
        req.raise_for_status()

        self.name = req.json()['name']
        self.value = req.json()['value']
        self.units = req.json()['units']
        self.symbol = req.json()['symbol']

"""Provides a Constant class for using constants from the Trident API.

"""
import requests

class Constant:
    """Represents a constant.

    Arguments:
    constant_symbol - Symbol denoting the constant.
    server_url - Trident API server URL. Defaults to http://trident.senorpez.com/constants/
    """
    def __init__(self, constant_symbol, server_url="http://trident.senorpez.com/"):
        req = requests.get(server_url)
        req.raise_for_status()
        constants_url = req.json()['_links']['trident-api:constants']['href']

        req = requests.get(constants_url)
        req.raise_for_status()
        constant_url = None
        for entry in req.json()['_embedded']['trident-api:constant']:
            if entry['symbol'] == constant_symbol:
                constant_url = entry['_links']['self']['href']

        req = requests.get(constant_url)
        req.raise_for_status()

        self.name = req.json()['name']
        self.value = req.json()['value']
        self.units = req.json()['units']
        self.symbol = req.json()['symbol']

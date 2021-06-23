import requests


def get_constant(constant_id, server_url="https://www.trident.senorpez.com/"):
    """Gets a constant resource from the API

    :param constant_id: Constant ID, for use with the Trident API
    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    :return: requests.Response containing the constant resource
    """
    index_response = get_index(server_url=server_url)
    constant_url = index_response.json()['_links']['trident-api:constants']['href']

    constants_response = requests.get(constant_url)
    constants_response.raise_for_status()
    embedded_constant = next(x for x
                             in constants_response.json()['_embedded']['trident-api:constant']
                             if x['symbol'] == constant_id)
    constant_url = embedded_constant['_links']['self']['href']

    constant_response = requests.get(constant_url)
    constant_response.raise_for_status()
    return constant_response


def get_index(server_url="https://www.trident.senorpez.com"):
    """Gets the index resource from the API

    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    :return: responses.Response containing the index resource
    """
    index_response = requests.get(server_url)
    index_response.raise_for_status()
    return index_response


def get_system(system_id, server_url="https://www.trident.senorpez.com/"):
    """Gets a system resource from the API

    :param system_id: Solar system ID, for use with the Trident API
    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    :return: responses.Response containing the system resource
    """
    index_response = get_index(server_url=server_url)
    systems_url = index_response.json()['_links']['trident-api:systems']['href']

    systems_response = requests.get(systems_url)
    systems_response.raise_for_status()
    embedded_system = next(x for x
                           in systems_response.json()['_embedded']['trident-api:system']
                           if x['id'] == system_id)
    system_url = embedded_system['_links']['self']['href']

    system_response = requests.get(system_url)
    system_response.raise_for_status()
    return system_response


def get_star(star_id, req):
    """Gets the star object from the API

    Arguments:
        star_id: Star ID, for use with the Trident API
        system_url: Trident API server URL for the system object

    Returns:
        requests.Response containing the star object
    """
    stars_url = req.json()['_links']['trident-api:stars']['href']

    req = requests.get(stars_url)
    req.raise_for_status()
    star = next(x for x in req.json()['_embedded']['trident-api:star'] if x['id'] == star_id)
    star_url = star['_links']['self']['href']

    req = requests.get(star_url)
    req.raise_for_status()

    return req

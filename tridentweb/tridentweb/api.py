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


def get_star(system_id, star_id, server_url="https://www.trident.senorpez.com/"):
    """Gets a star resource from the API

    :param system_id: Solar system ID, for use with the Trident API
    :param star_id: Star ID, for use with the Trident API
    :param server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/
    :return: responses.Response containing the star resource
    :return: System primary GM; None if star is the primary
    """
    system_response = get_system(system_id, server_url=server_url)
    stars_url = system_response.json()['_links']['trident-api:stars']['href']

    stars_response = requests.get(stars_url)
    stars_response.raise_for_status()
    embedded_star = next(x for x
                         in stars_response.json()['_embedded']['trident-api:star']
                         if x['id'] == star_id)
    star_url = embedded_star['_links']['self']['href']

    star_response = requests.get(star_url)
    star_response.raise_for_status()

    if is_primary(star_response):
        return star_response, None
    else:
        constant_Msol = get_constant("Msol", server_url=server_url).json()['value']
        constant_G = get_constant("G", server_url=server_url).json()['value']
        embedded_hrefs = [x['_links']['self']['href'] for x in stars_response.json()['_embedded']['trident-api:star']]
        for href in embedded_hrefs:
            star = requests.get(href)
            star.raise_for_status()
            if is_primary(star):
                return star_response, star.json()['mass'] * constant_Msol * constant_G

    # Oops, this system doesn't have a primary.
    # TODO: Throw a better error.
    return SystemError


def is_primary(star):
    """Determines if a star is the primary star.

    :param star: responses.Response containing the star resource
    :return: True if star is the primary star
    """
    return all(v is None for v in [
        star.json()['semimajorAxis'],
        star.json()['eccentricity'],
        star.json()['inclination'],
        star.json()['longitudeOfAscendingNode'],
        star.json()['argumentOfPeriapsis'],
        star.json()['trueAnomalyAtEpoch']
    ])

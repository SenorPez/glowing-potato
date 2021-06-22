import requests


def get_system(system_id, server_url="https://www.trident.senorpez.com/"):
    """Gets the system object from the API

    Arguments:
        system_id: Solar system ID, for use with the Trident API
        server_url: Trident API server URL; defaults to https://www.trident.senorpez.com/

    Returns:
        requests.Response containing the system object
    """
    req = requests.get(server_url)
    req.raise_for_status()
    systems_url = req.json()['_links']['trident-api:systems']['href']

    req = requests.get(systems_url)
    req.raise_for_status()
    system = next(x for x in req.json()['_embedded']['trident-api:system'] if x['id'] == system_id)
    system_url = system['_links']['self']['href']

    req = requests.get(system_url)
    req.raise_for_status()

    return req


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

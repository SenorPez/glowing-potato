from datetime import datetime

import numpy as np
import requests
from flask import Flask, jsonify
from flask_cors import CORS
from pykep import AU, epoch_from_string, SEC2DAY, epoch
from pykep.planet import keplerian

APP = Flask(__name__)
CORS(APP, resources={r"/*": {"origins": "http://senorpez.com"}})


@APP.route("/orbit", methods=['GET'])
def test():
    # Get standard solar mass.
    req = requests.get("http://trident.senorpez.com/constants/Msol")
    req.raise_for_status()
    mass_solar = req.json()['value']

    # Get gravitational constant.
    req = requests.get("http://trident.senorpez.com/constants/G")
    req.raise_for_status()
    grav = req.json()['value']

    # Get solar mass and GM of 1 Eta Veneris
    system_link = find_link_by_name(
        "http://trident.senorpez.com/systems", 
        "trident-api:system",
        "Eta Veneris")
    star_link = find_link_by_name(
        system_link + "/stars",
        "trident-api:star",
        "1 Eta Veneris")

    req = requests.get(star_link)
    req.raise_for_status()

    mass_s1 = req.json()['mass'] * mass_solar
    gm_s1 = mass_s1 * grav

    planets = [
        create_planet(
            "1 Eta Veneris 1",
            "grey",
            star_link,
            gm_s1),
        create_planet(
            "1 Eta Veneris 2",
            "grey",
            star_link,
            gm_s1),
        create_planet(
            "1 Eta Veneris 3",
            "green",
            star_link,
            gm_s1),
        create_planet(
            "1 Eta Veneris 4",
            "grey",
            star_link,
            gm_s1)]

    # Create orbit data
    t0 = epoch_from_string(str(datetime.now()))
    x_val = list()
    y_val = list()
    z_val = list()
    planet_positions = list()
    planet_names = list()
    planet_colors = list()

    for planet in planets:
        orbit_period = planet.compute_period() * SEC2DAY
        orbit_when = np.linspace(0, orbit_period, 60)

        x = np.zeros(60)
        y = np.zeros(60)
        z = np.zeros(60)

        for i, day in enumerate(orbit_when):
            r, _ = planet.eph(epoch(t0.mjd2000 + day))
            x[i] = r[0] / AU
            y[i] = r[1] / AU
            z[i] = r[2] / AU

        x_val.append(x.tolist())
        y_val.append(y.tolist())
        z_val.append(z.tolist())
        planet_positions.append(list((x[0], y[0], z[0])))
        planet_colors.append(planet.color)
        planet_names.append(planet.name)

    return jsonify(
            success=True, 
            x=x_val, 
            y=y_val, 
            z=z_val,
            p=planet_positions,
            c=planet_colors,
            n=planet_names)


def find_link_by_name(api_link, collection_name, target_name):
    req = requests.get(api_link)
    req.raise_for_status()
    link = None
    for entry in req.json()['_embedded'][collection_name]:
        if entry['name'] == target_name:
            link = entry['_links']['self']['href']
    return link


def create_planet(planet_name, planet_color, star_link, gm_star):
    # Get gravitational constant.
    req = requests.get("http://trident.senorpez.com/constants/G")
    req.raise_for_status()
    grav = req.json()['value']

    # Get standard planetary mass.
    req = requests.get("http://trident.senorpez.com/constants/Mpln")
    req.raise_for_status()
    mass_planet = req.json()['value']

    # Get standard planetary equatorial radius.
    req = requests.get("http://trident.senorpez.com/constants/Rpln")
    req.raise_for_status()
    radius_planet = req.json()['value']

    # Get planetary mass, radius, GM, and planet object of 1 Eta Veneris 3
    planet_link = find_link_by_name(
        star_link + "/planets",
        "trident-api:planet",
        planet_name)

    req = requests.get(planet_link)
    req.raise_for_status()

    mass = req.json()['mass'] * mass_planet
    radius = req.json()['radius'] * radius_planet
    gm = mass * grav

    return_planet = keplerian(
        epoch(0),
        (
            req.json()['semimajorAxis'] * AU,
            req.json()['eccentricity'],
            req.json()['inclination'],
            req.json()['longitudeOfAscendingNode'],
            req.json()['argumentOfPeriapsis'],
            req.json()['trueAnomalyAtEpoch']),
        gm_star,
        gm,
        radius,
        radius,
        planet_name)
    return_planet.color = planet_color

    return return_planet


if __name__ == "__main__":
    APP.run("0.0.0.0", 5001)

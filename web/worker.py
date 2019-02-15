from datetime import datetime
from math import sqrt
import numpy as np
from flask import Flask, jsonify
from flask_cors import CORS
import requests

from pykep import AU, DAY2SEC, SEC2DAY, epoch, lambert_problem, epoch_from_string
from pykep.planet import keplerian as planet

def create_planet(planet_name, planet_color, star_link, gm_star):
    # Get planetary mass, radius, GM, and planet object of 1 Eta Veneris 3
    planet_link = find_link_by_name(
        star_link + "/planets",
        "trident-api:planet",
        planet_name)
    req = requests.get(planet_link)
    req.raise_for_status()

    mass = req.json()['mass'] * MASS_PLANET
    radius = req.json()['radius'] * RADIUS_PLANET
    gm = mass * GRAV

    return_planet = planet(
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

def get_constant(constant_name):
    req = requests.get("http://trident.senorpez.com/constants/" + str(constant_name))
    req.raise_for_status()
    return req.json()['value']

def find_link_by_name(api_link, collection_name, target_name):
    req = requests.get(api_link)
    req.raise_for_status()
    link = None
    for entry in req.json()['_embedded'][collection_name]:
        if entry['name'] == target_name:
            link = entry['_links']['self']['href']
    return link

def find_planet_by_id(planet_id):
    planet_data = requests.get(
        "http://trident.senorpez.com/systems/1817514095/stars/1905216634/planets/"
        + str(planet_id))
    planet_data.raise_for_status()
    return planet_data

def get_planet(planet_id, planet_color, gm_star):
    planet_data = find_planet_by_id(planet_id)

    mass = planet_data.json()['mass'] * MASS_PLANET
    radius = planet_data.json()['radius'] * RADIUS_PLANET
    gm = mass * GRAV

    return_planet = planet(
        epoch(0),
        (
            planet_data.json()['semimajorAxis'] * AU,
            planet_data.json()['eccentricity'],
            planet_data.json()['inclination'],
            planet_data.json()['longitudeOfAscendingNode'],
            planet_data.json()['argumentOfPeriapsis'],
            planet_data.json()['trueAnomalyAtEpoch']),
        gm_star,
        gm,
        radius,
        radius,
        planet_data.json()['name'])

    return_planet.color = planet_color

    return return_planet

def get_star(system_name, star_name):
    system_link = find_link_by_name(
        "http://trident.senorpez.com/systems",
        "trident-api:system",
        system_name)
    star_link = find_link_by_name(
        system_link + "/stars",
        "trident-api:star",
        star_name)

    req = requests.get(star_link)
    req.raise_for_status()

    mass = req.json()['solarMass'] * MASS_SOLAR
    gm = mass * GRAV

    return (star_link, gm)

# Get standard solar mass.
MASS_SOLAR = get_constant("Msol")

# Get GRAVitational constant.
GRAV = get_constant("G")

# Get standard planetary mass.
MASS_PLANET = get_constant("Mpln")

# Get standard planetary equatorial radius.
RADIUS_PLANET = get_constant("Rpln")

APP = Flask(__name__)
CORS(APP, resources={r"/*": {"origins": "http://senorpez.com"}})

@APP.route("/orbit", methods=['GET'])
def orbit():
    star_link, gm_s1 = get_star("Eta Veneris", "1 Eta Veneris")

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

    for orbiter in planets:
        orbit_period = orbiter.compute_period(epoch(0)) * SEC2DAY
        orbit_when = np.linspace(0, orbit_period, 60)

        x = np.zeros(60)
        y = np.zeros(60)
        z = np.zeros(60)

        for i, day in enumerate(orbit_when):
            r, _ = orbiter.eph(epoch(t0.mjd2000 + day))
            x[i] = r[0] / AU
            y[i] = r[1] / AU
            z[i] = r[2] / AU

        x_val.append(x.tolist())
        y_val.append(y.tolist())
        z_val.append(z.tolist())
        planet_positions.append(list((x[0], y[0], z[0])))
        planet_colors.append(orbiter.color)
        planet_names.append(orbiter.name)

    return jsonify(
        success=True,
        x=x_val,
        y=y_val,
        z=z_val,
        p=planet_positions,
        c=planet_colors,
        n=planet_names)

@APP.route("/transfer", methods=['GET'])
def transfer():
    star_link, gm_s1 = get_star("Eta Veneris", "1 Eta Veneris")
    origin = create_planet(
        "1 Eta Veneris 3",
        "green",
        star_link,
        gm_s1)
    target = create_planet(
        "1 Eta Veneris 4",
        "grey",
        star_link,
        gm_s1)

    origin_orbit_radius = origin.radius + 200000
    target_orbit_radius = target.radius + 200000

    t0 = epoch_from_string(str(datetime.now()))
    t0_number = int(t0.mjd2000)

    flight_times = np.array(range(50, 251))
    flight_time_offset = 50
    launch_times = np.array(range(t0_number, t0_number + 501))
    launch_time_offset = t0_number
    delta_v = np.empty((len(flight_times), len(launch_times)))

    for launch_time in launch_times:
        for flight_time in flight_times:
            launch_time = int(launch_time)
            flight_time = int(flight_time)

            t1 = epoch(launch_time)
            t2 = epoch(launch_time + flight_time)
            dt = (t2.mjd - t1.mjd)*DAY2SEC
            r1, v1 = origin.eph(t1)
            r2, v2 = target.eph(t2)
            l = lambert_problem(list(r1), list(r2), dt, gm_s1)

            delta_vs = list()

            for x in range(l.get_Nmax() + 1):
                vp_vec = np.array(v1)
                vs_vec = np.array(l.get_v1()[x])
                vsp_vec = vs_vec - vp_vec
                vsp = np.linalg.norm(vsp_vec)
                vo = sqrt(vsp*vsp + 2*origin.mu_self/origin_orbit_radius)
                inj_delta_v = vo - sqrt(origin.mu_self/origin_orbit_radius)

                vp_vec = np.array(v2)
                vs_vec = np.array(l.get_v2()[x])
                vsp_vec = vs_vec - vp_vec
                vsp = np.linalg.norm(vsp_vec)
                vo = sqrt(vsp*vsp + 2*target.mu_self/target_orbit_radius)
                ins_delta_v = vo - sqrt(target.mu_self/target_orbit_radius)

                delta_vs.append(inj_delta_v + ins_delta_v)

            delta_v[flight_time-flight_time_offset, launch_time-launch_time_offset] = min(delta_vs)

    return jsonify(success=True, delta_v=delta_v.tolist())

if __name__ == "__main__":
    APP.run(host="0.0.0.0", port=5001)

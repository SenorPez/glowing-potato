from datetime import datetime, timedelta
from math import ceil, floor, sqrt

from flask import Flask, jsonify, request as flask_request
from flask_cors import CORS
import matplotlib.pyplot as plt
import numpy as np
from pykep import AU, DAY2SEC, SEC2DAY, epoch, lambert_problem, epoch_from_string
from pykep.planet import keplerian as planet
from pykep.orbit_plots import plot_lambert, plot_planet
import requests


def calc_adjustment():
    a = -100.0
    b = 0.0
    winter = (0, -1, 0) / np.linalg.norm((0, -1, 0))
    angle_target = np.arccos(np.clip(np.dot((1, 0, 0), winter), -1.0, 1.0))

    _, gm_s1 = get_star("Eta Veneris", "1 Eta Veneris")
    taven = get_planet(-455609026, "green", gm_s1)

    for _ in range(1000):
        c = (a + b) / 2
        r_a, _ = taven.eph(epoch(a))
        r_b, _ = taven.eph(epoch(b))
        r_c, _ = taven.eph(epoch(c))

        u_a = r_a / np.linalg.norm(r_a)
        angle_a = np.arccos(np.clip(np.dot(u_a, winter), -1.0, 1.0))
        u_b = r_b / np.linalg.norm(r_b)
        angle_b = np.arccos(np.clip(np.dot(u_b, winter), -1.0, 1.0))
        u_c = r_c / np.linalg.norm(r_c)
        angle_c = np.arccos(np.clip(np.dot(u_c, winter), -1.0, 1.0))

        if angle_c - angle_target < 0:
            a = c
        else:
            b = c

    return c


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


def get_constant(constant_name):
    req = requests.get("http://trident.senorpez.com/constants/" + str(constant_name))
    req.raise_for_status()
    return req.json()['value']


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

    mass = req.json()['mass'] * MASS_SOLAR
    gm = mass * GRAV

    return star_link, gm


# Get Gravitational constant.
GRAV = get_constant("G")

# Get standard planetary mass.
MASS_PLANET = get_constant("Mpln")

# Get standard solar mass.
MASS_SOLAR = get_constant("Msol")

# Get standard planetary equatorial radius.
RADIUS_PLANET = get_constant("Rpln")

# Adjustment as calendar starts at Winter Solstice not Spring Equinox.
T_ADJ = None

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


@APP.route("/time", methods=['GET'])
def time():
    t_now = epoch_from_string(str(datetime.now()))
    t0 = epoch(t_now.mjd2000 - T_ADJ)
    d = timedelta(t0.mjd2000)

    # Standard time values
    hours = d.days * 24 + d.seconds / 3600

    # Standard hours per local day
    hours_per_local_day = 36.362486

    # Local days
    local_days = hours / hours_per_local_day

    # Local days per local year
    local_days_per_local_year = 99.3141

    # Local year, accounting for local calendar:
    # 2 years of 99 days followed by 1 year of 100 days.
    local_days_countdown = local_days
    year = 1

    while True:
        if year % 3:
            if (local_days_countdown > 99):
                year += 1
                local_days_countdown -= 99
            else:
                break
        else:
            if (local_days_countdown > 100):
                year += 1
                local_days_countdown -= 100
            else:
                break

    caste = 0
    festival_day = False

    while True:
        if local_days_countdown < 1:
            festival_day = True
            break
        if local_days_countdown < 20:
            caste = 1
            local_days_countdown -= 1
            break
        if local_days_countdown < 41:
            caste = 2
            local_days_coundown -= 20
            break
        if year % 3:
            if local_days_countdown < 61:
                caste = 3
                local_days_countdown -= 41
                break

        else:
            if local_days_countdown < 51:
                caste = 3
                local_days_countdown -= 41
                break
            if local_days_countdown < 52:
                caste = 3
                festival_day = True
                local_days_countdown -= 51
                break
            if local_days_countdown < 62:
                caste = 3
                local_days_countdown -= 52
                break
            local_days_countdown -= 1

        if local_days_countdown < 80:
            local_days_countdown -= 61
            caste = 4
            break

        if local_days_countdown < 99:
            local_days_countdown -= 80
            caste = 5
            break

        raise ValueError

    day = ceil(local_days_countdown)
    local_days_countdown = local_days_countdown % 1

    # Floor the countdown to 2 decimal places.
    floored = floor((1 + local_days_countdown / 0.25) * 100) / 100
    shift = "{:4.2f}".format(floored)
    rawshift = (1 + local_days_countdown / 0.25)

    return jsonify(
        success=True,
        year=year,
        caste=caste,
        day=day,
        shift=shift,
        rawshift=rawshift,
        days=local_days,
        t0=str(t0),
        festival_day=festival_day)


@APP.route("/transfer", methods=['POST'])
def transfer():
    _, gm_s1 = get_star("Eta Veneris", "1 Eta Veneris")
    origin = get_planet(flask_request.values['origin'], "green", gm_s1)
    target = get_planet(flask_request.values['target'], "grey", gm_s1)
    origin_orbit_radius = origin.radius + 200000
    target_orbit_radius = target.radius + 200000

    t0 = epoch_from_string(str(datetime.now()))
    t0_number = int(t0.mjd2000)
    launch_time_offset = t0_number
    flight_time_offset = 50

    fig = plt.figure(figsize=(4, 4))
    orbit_ax = fig.gca(projection='3d', proj_type='ortho')
    orbit_ax.scatter([0], [0], [0], color='orange')
    orbit_ax.set_aspect('equal')

    flight_times = np.array(range(50, 251))
    launch_times = np.array(range(t0_number, t0_number + 501))
    delta_v = np.empty((len(flight_times), len(launch_times)))

    if ('launch_time' not in flask_request.values) and ('flight_time' not in flask_request.values):
        for launch_time in launch_times:
            for flight_time in flight_times:
                launch_time = int(launch_time)
                flight_time = int(flight_time)

                t1 = epoch(launch_time)
                t2 = epoch(launch_time + flight_time)
                dt = (t2.mjd - t1.mjd) * DAY2SEC
                r1, v1 = origin.eph(t1)
                r2, v2 = target.eph(t2)
                lambert = lambert_problem(list(r1), list(r2), dt, gm_s1)

                delta_vs = list()

                for x in range(lambert.get_Nmax() + 1):
                    vp_vec = np.array(v1)
                    vs_vec = np.array(lambert.get_v1()[x])
                    vsp_vec = vs_vec - vp_vec
                    vsp = np.linalg.norm(vsp_vec)
                    vo = sqrt(vsp * vsp + 2 * origin.mu_self / origin_orbit_radius)
                    inj_delta_v = vo - sqrt(origin.mu_self / origin_orbit_radius)

                    vp_vec = np.array(v2)
                    vs_vec = np.array(lambert.get_v2()[x])
                    vsp_vec = vs_vec - vp_vec
                    vsp = np.linalg.norm(vsp_vec)
                    vo = sqrt(vsp * vsp + 2 * target.mu_self / target_orbit_radius)
                    ins_delta_v = vo - sqrt(target.mu_self / target_orbit_radius)

                    delta_vs.append(inj_delta_v + ins_delta_v)

                delta_v[flight_time - flight_time_offset, launch_time - launch_time_offset] = min(delta_vs)

        min_delta_v = np.min(delta_v)
        find_min = (delta_v == min_delta_v).nonzero()
        min_delta_v_flight_time = find_min[0][0]
        min_delta_v_launch_time = find_min[1][0]

        launch_time = int(min_delta_v_launch_time) + launch_time_offset
        flight_time = int(min_delta_v_flight_time) + flight_time_offset
    else:
        min_delta_v_flight_time = int(flask_request.values['flight_time']) - flight_time_offset
        min_delta_v_launch_time = int(flask_request.values['launch_time'])

        flight_time = min_delta_v_flight_time + flight_time_offset
        launch_time = min_delta_v_launch_time + launch_time_offset

        min_delta_v = flask_request.values['delta_v']

    t1 = epoch(int(launch_time))
    t2 = epoch(int(launch_time) + int(flight_time))
    plot_planet(origin, t0=t1, color=origin.color, legend=True, units=AU, ax=orbit_ax)
    plot_planet(target, t0=t2, color=target.color, legend=True, units=AU, ax=orbit_ax)

    max_value = max(
        max([abs(x) for x in orbit_ax.get_xlim()]),
        max([abs(y) for y in orbit_ax.get_ylim()]))
    max_z_value = max([abs(z) for z in orbit_ax.get_zlim()])

    dt = (t2.mjd - t1.mjd) * DAY2SEC
    r1, v1 = origin.eph(t1)
    r2, v2 = target.eph(t2)
    lambert = lambert_problem(list(r1), list(r2), dt, gm_s1)
    plot_lambert(lambert, color='purple', sol=0, legend=False, units=AU, ax=orbit_ax)

    orbit_ax.set_xlim(-max_value * 1.2, max_value * 1.2)
    orbit_ax.set_ylim(-max_value * 1.2, max_value * 1.2)
    orbit_ax.set_zlim(-max_z_value * 1.2, max_z_value * 1.2)

    plt.savefig('orbit')

    orbit_ax.view_init(0, 0)
    plt.savefig('orbit-x')

    orbit_ax.view_init(0, -90)
    plt.savefig('orbit-y')

    orbit_ax.view_init(90, 0)
    plt.savefig('orbit-z')

    plt.close(fig)

    return jsonify(
        success=True,
        delta_v=delta_v.tolist(),
        min_delta_v=min_delta_v,
        flight_time=int(min_delta_v_flight_time + flight_time_offset),
        launch_time=int(min_delta_v_launch_time))


if __name__ == "__main__":
    T_ADJ = calc_adjustment()
    APP.run(host="0.0.0.0", port=5001)


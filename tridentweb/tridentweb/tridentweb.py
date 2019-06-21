"""
Main execution script.
"""
from datetime import datetime
from math import sqrt

from flask import Flask, has_app_context, jsonify, request as flask_request
from flask_cors import CORS
import matplotlib.pyplot as plt
import numpy as np
from pykep import epoch, epoch_from_string, lambert_problem, AU, DAY2SEC, SEC2DAY
from pykep.orbit_plots import plot_lambert, plot_planet
from tridentweb.planet import Planet
from tridentweb.star import Star

APP = Flask(__name__)
CORS(APP, resources={r"/*": {"origins": "http://senorpez.com"}})

@APP.route("/epochoffset", methods=['GET'])
def epoch_offset():
    planet = Planet(1817514095, 1905216634, -455609026)

    winter = np.arctan2(-1, 0)

    a = -75.0
    b = 75.0

    for _ in range(1000):
        c = (a + b) / 2
        r_c, _ = planet.planet.eph(epoch(c))

        angle = np.arctan2(r_c[1], r_c[0])

        if angle - winter < 0:
            a = c
        else:
            b = c

    return jsonify(c) if has_app_context() else c

@APP.route("/orbit2", methods=['GET'])
def orbit2():
    planets = [
        Planet(1817514095, 1905216634, -1485460920),
        Planet(1817514095, 1905216634, -1722015868),
        Planet(1817514095, 1905216634, -455609026),
        Planet(1817514095, 1905216634, 272811578),
        Star(1817514095, -1385166447, Star(1817514095, 1905216634))]

    t0 = epoch_from_string(str(datetime.now()))
    x_val = list()
    y_val = list()
    z_val = list()
    planet_positions = list()
    planet_names = list()
    planet_colors = list()

    for orbiter in planets:
        orbit_period = orbiter.planet.compute_period(epoch(0)) * SEC2DAY
        orbit_when = np.linspace(0, orbit_period, 60)

        x = np.zeros(60)
        y = np.zeros(60)
        z = np.zeros(60)

        for i, day in enumerate(orbit_when):
            r, _ = orbiter.planet.eph(epoch(t0.mjd2000 + day))
            x[i] = r[0] / AU
            y[i] = r[1] / AU
            z[i] = r[2] / AU

        x_val.append(x.tolist())
        y_val.append(y.tolist())
        z_val.append(z.tolist())

        planet_positions.append(list((x[0], y[0], z[0])))
        if orbiter.id == -455609026:
            planet_colors.append("green")
        else:
            planet_colors.append("gray")
        planet_names.append(orbiter.name)

    return jsonify(
        success=True,
        x=x_val,
        y=y_val,
        z=z_val,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else x_val

@APP.route("/orbit", methods=['GET'])
def orbit():
    planets = [
        Planet(1817514095, 1905216634, -1485460920),
        Planet(1817514095, 1905216634, -1722015868),
        Planet(1817514095, 1905216634, -455609026),
        Planet(1817514095, 1905216634, 272811578)]

    t0 = epoch_from_string(str(datetime.now()))
    x_val = list()
    y_val = list()
    z_val = list()
    planet_positions = list()
    planet_names = list()
    planet_colors = list()

    for orbiter in planets:
        orbit_period = orbiter.planet.compute_period(epoch(0)) * SEC2DAY
        orbit_when = np.linspace(0, orbit_period, 60)

        x = np.zeros(60)
        y = np.zeros(60)
        z = np.zeros(60)

        for i, day in enumerate(orbit_when):
            r, _ = orbiter.planet.eph(epoch(t0.mjd2000 + day))
            x[i] = r[0] / AU
            y[i] = r[1] / AU
            z[i] = r[2] / AU

        x_val.append(x.tolist())
        y_val.append(y.tolist())
        z_val.append(z.tolist())

        planet_positions.append(list((x[0], y[0], z[0])))
        if orbiter.id == -455609026:
            planet_colors.append("green")
        else:
            planet_colors.append("gray")
        planet_names.append(orbiter.name)

    return jsonify(
        success=True,
        x=x_val,
        y=y_val,
        z=z_val,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else x_val

@APP.route("/plottransfer", methods=['POST'])
def plottransfer():
    star = Star(1817514095, 1905216634)
    origin = Planet(1817514095, 1905216634, -455609026)
    target = Planet(1817514095, 1905216634, 272811578)
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    flight_time = int(flask_request.values['flight_time'])

    t0 = epoch_from_string(str(datetime.now()))
    t0_number = int(t0.mjd2000)
    launch_time = int(flask_request.values['launch_time']) + t0_number

    t1 = epoch(int(launch_time))
    t2 = epoch(int(launch_time) + int(flight_time))

    fig = plt.figure(figsize=(4, 4))
    orbit_ax = fig.gca(projection='3d', proj_type='ortho')
    orbit_ax.scatter([0], [0], [0], color='orange')
    orbit_ax.set_aspect('equal')

    plot_planet(origin.planet, t0=t1, color='green', legend=True, units=AU, ax=orbit_ax)
    plot_planet(target.planet, t0=t2, color='gray', legend=True, units=AU, ax=orbit_ax)

    max_value = max(
        max([abs(x) for x in orbit_ax.get_xlim()]),
        max([abs(x) for x in orbit_ax.get_ylim()]))
    max_z_value = max([abs(z) for z in orbit_ax.get_zlim()])

    dt = (t2.mjd - t1.mjd) * DAY2SEC
    r1, v1 = origin.planet.eph(t1)
    r2, v2 = target.planet.eph(t2)
    lambert = lambert_problem(list(r1), list(r2), dt, star.gm)

    min_n = None
    min_delta_v = None
    for x in range(lambert.get_Nmax() + 1):
        vp_vec = np.array(v1)
        vs_vec = np.array(lambert.get_v1()[x])
        vsp_vec = vs_vec - vp_vec
        vsp = np.linalg.norm(vsp_vec)
        vo = sqrt(vsp * vsp + 2 * origin.planet.mu_self / origin_orbit_radius)
        inj_delta_v = vo - sqrt(origin.planet.mu_self / origin_orbit_radius)

        vp_vec = np.array(v2)
        vs_vec = np.array(lambert.get_v2()[x])
        vsp_vec = vs_vec - vp_vec
        vsp = np.linalg.norm(vsp_vec)
        vo = sqrt(vsp * vsp + 2 * target.planet.mu_self / target_orbit_radius)
        ins_delta_v = vo - sqrt(target.planet.mu_self / target_orbit_radius)

        if min_delta_v is None or inj_delta_v + ins_delta_v < min_delta_v:
            min_n = x
            min_delta_v = inj_delta_v + ins_delta_v

    plot_lambert(lambert, color='purple', sol=min_n, legend=False, units=AU, ax=orbit_ax)

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

    return jsonify(success=True)

@APP.route("/transfer", methods=['POST'])
def transfer():
    star = Star(1817514095, 1905216634)
    origin = Planet(1817514095, 1905216634, -455609026)
    target = Planet(1817514095, 1905216634, 272811578)
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    launch_time_start = int(float(flask_request.values['launch_start']))
    launch_time_end = int(float(flask_request.values['launch_end']))
    launch_time_offset = 0 if launch_time_start >= 0 else -launch_time_start

    flight_time_start = int(float(flask_request.values['flight_start']))
    flight_time_start = max(1, flight_time_start)
    flight_time_end = int(float(flask_request.values['flight_end']))
    flight_time_end = max(1, flight_time_end)

    t0 = epoch_from_string(str(datetime.now()))
    t0_number = int(t0.mjd2000)

    delta_v = np.full(
        (
            flight_time_end - flight_time_start,
            launch_time_end + launch_time_offset),
        None)

    for launch_time in range(launch_time_start, launch_time_end):
        for flight_time in range(flight_time_start, flight_time_end):
            launch_time = int(launch_time)
            launch_time_index = launch_time + launch_time_offset
            flight_time = int(flight_time)
            flight_time_index = flight_time - flight_time_start

            t1 = epoch(launch_time + t0_number)
            t2 = epoch(launch_time + t0_number + flight_time)
            dt = (t2.mjd - t1.mjd) * DAY2SEC
            r1, v1 = origin.planet.eph(t1)
            r2, v2 = target.planet.eph(t2)
            lambert = lambert_problem(list(r1), list(r2), dt, star.gm)

            delta_vs = list()

            for x in range(lambert.get_Nmax() + 1):
                vp_vec = np.array(v1)
                vs_vec = np.array(lambert.get_v1()[x])
                vsp_vec = vs_vec - vp_vec
                vsp = np.linalg.norm(vsp_vec)
                vo = sqrt(vsp * vsp + 2 * origin.planet.mu_self / origin_orbit_radius)
                inj_delta_v = vo - sqrt(origin.planet.mu_self / origin_orbit_radius)

                vp_vec = np.array(v2)
                vs_vec = np.array(lambert.get_v2()[x])
                vsp_vec = vs_vec - vp_vec
                vsp = np.linalg.norm(vsp_vec)
                vo = sqrt(vsp * vsp + 2 * target.planet.mu_self / target_orbit_radius)
                ins_delta_v = vo - sqrt(target.planet.mu_self / target_orbit_radius)

                delta_vs.append(inj_delta_v + ins_delta_v)

            delta_v[
                flight_time_index,
                launch_time_index] = min(delta_vs)

    return jsonify(
        success=True,
        delta_v=delta_v.tolist(),
        launch_start=launch_time_start,
        launch_end=launch_time_end,
        flight_start=flight_time_start,
        flight_end=flight_time_end,
        launch_offset=-launch_time_offset)

def main():
    APP.run(host="0.0.0.0", port=5001)

if __name__ == "__main__":
    main()

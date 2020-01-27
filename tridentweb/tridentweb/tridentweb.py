"""
Main execution script.
"""
from datetime import datetime, timedelta
from tqdm import tqdm
from math import sqrt, atan2, cos, sin, pi
import gzip
import json

from tridentweb.epoch_offset import epoch_offset
from tridentweb.pykep_addons import lambert_positions, orbit_positions
from tridentweb.plotting import plot_orbits

from flask import Flask, has_app_context, jsonify, request as flask_request
from flask_cors import CORS
import matplotlib.pyplot as plt
import numpy as np
from numpy import mod
from pykep import epoch, epoch_from_string, lambert_problem, \
        propagate_lagrangian, AU, DAY2SEC, SEC2DAY, DEG2RAD, MU_SUN, RAD2DEG
from pykep.orbit_plots import plot_lambert, plot_planet
from tridentweb.planet import Planet
from tridentweb.star import Star

from pykep.planet import jpl_lp, keplerian

APP = Flask(__name__)
CORS(APP, resources={r"/*": {"origins": "http://senorpez.com"}})

@APP.route("/epochoffset", methods=['GET'])
def get_epoch_offset():
    planet = Planet(1817514095, 1905216634, -455609026)
    summer = np.arctan2(-1, 0)
    offset, error = epoch_offset(planet.planet, summer)
    return jsonify(offset,error) if has_app_context() else (offset, error)

@APP.route("/systemorbits", methods=['GET'])
def systemorbits():
    planets = [
        Planet(1817514095, 1905216634, -1485460920),
        Planet(1817514095, 1905216634, -1722015868),
        Planet(1817514095, 1905216634, -455609026),
        Planet(1817514095, 1905216634, 272811578),
        Planet(1817514095, 1905216634, -393577255),
        Planet(1817514095, 1905216634, -1420756477),
        Planet(1817514095, 1905216634, -93488736),
        Star(1817514095, -1385166447, Star(1817514095, 1905216634))]

    system_x, system_y, system_z, planet_positions, planet_colors, planet_names = plot_orbits(planets)
    return jsonify(
        success=True,
        x=system_x,
        y=system_y,
        z=system_z,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else planet_names

@APP.route("/innerorbits", methods=['GET'])
def innerorbits():
    planets = [
        Planet(1817514095, 1905216634, -1485460920),
        Planet(1817514095, 1905216634, -1722015868),
        Planet(1817514095, 1905216634, -455609026),
        Planet(1817514095, 1905216634, 272811578)]

    system_x, system_y, system_z, planet_positions, planet_colors, planet_names = plot_orbits(planets)
    return jsonify(
        success=True,
        x=system_x,
        y=system_y,
        z=system_z,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else planet_names

@APP.route("/plottransfer", methods=['POST'])
def plottransfer():
    star = Star(1817514095, 1905216634)
    origin = Planet(1817514095, 1905216634, -455609026)
    target = Planet(1817514095, 1905216634, 272811578)
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    t0 = epoch_from_string("{:%Y-%m-%d 00:00:00}".format(datetime.now()))
    t0_number = int(t0.mjd2000)
    launch_time = int(flask_request.values['launch_time']) + t0_number
    flight_time = int(flask_request.values['flight_time']) + launch_time

    t1 = epoch(int(launch_time))
    t2 = epoch(int(flight_time))

    fig = plt.figure(figsize=(4, 4))
    orbit_ax = fig.gca(projection='3d', proj_type='ortho')
    orbit_ax.scatter([0], [0], [0], color='orange')
    orbit_ax.set_aspect('equal')

    x_fig = plt.figure(figsize=(4, 4))
    x_ax = x_fig.gca()
    x_ax.scatter([0], [0], color='orange')

    y_fig = plt.figure(figsize=(4, 4))
    y_ax = y_fig.gca()
    y_ax.scatter([0], [0], color='orange')

    z_fig = plt.figure(figsize=(4, 4))
    z_ax = z_fig.gca()
    z_ax.scatter([0], [0], color='orange')

    plot_planet(
        origin.planet,
        t0=t1,
        color='green',
        legend=True,
        units=AU,
        ax=orbit_ax)
    plot_planet(
        target.planet,
        t0=t2,
        color='gray',
        legend=True,
        units=AU,
        ax=orbit_ax)

    o_x, o_y, o_z = tuple(x / AU for x in orbit_positions(origin.planet, t1))
    t_x, t_y, t_z = tuple(x / AU for x in orbit_positions(target.planet, t2))

    x_ax.plot(o_y, o_z, label=origin.planet.name, c='green')
    x_ax.scatter(o_y[0], o_z[0], s=40, color='green')
    x_ax.plot(t_y, t_z, label=target.planet.name, c='gray')
    x_ax.scatter(t_y[0], t_z[0], s=40, color='gray')

    y_ax.plot(o_x, o_z, label=origin.planet.name, c='green')
    y_ax.scatter(o_x[0], o_z[0], s=40, color='green')
    y_ax.plot(t_x, t_z, label=target.planet.name, c='gray')
    y_ax.scatter(t_x[0], t_z[0], s=40, color='gray')

    z_ax.plot(o_x, o_y, label=origin.planet.name, c='green')
    z_ax.scatter(o_x[0], o_y[0], s=40, color='green')
    z_ax.plot(t_x, t_y, label=target.planet.name, c='gray')
    z_ax.scatter(t_x[0], t_y[0], s=40, color='gray')

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

    l_x, l_y, l_z = tuple(x / AU for x in lambert_positions(lambert, sol=min_n))

    x_ax.plot(l_y, l_z, c='purple')
    y_ax.plot(l_x, l_z, c='purple')
    z_ax.plot(l_x, l_y, c='purple')

    orbit_ax.set_xlim(-max_value * 1.2, max_value * 1.2)
    orbit_ax.set_ylim(-max_value * 1.2, max_value * 1.2)
    orbit_ax.set_zlim(-max_z_value * 1.2, max_z_value * 1.2)

    x_ax.set_xlim(-1.0, 1.0)
    x_ax.set_ylim(-0.05, 0.05)

    y_ax.set_xlim(-1.0, 1.0)
    y_ax.set_ylim(-0.05, 0.05)

    z_ax.set_xlim(-1.0, 1.0)
    z_ax.set_ylim(-1.0, 1.0)

    fig.savefig('orbit')

    x_fig.savefig('orbit-x')
    y_fig.savefig('orbit-y')
    z_fig.savefig('orbit-z')

    plt.close('all')

    return jsonify(success=True)

@APP.route("/transfer", methods=['POST'])
def transfer():
    with gzip.open(
            '/home/senorpez/glowing-potato/tridentweb/tridentweb/transfer_data.gz',
            'rt') as fp:
        json_data = json.load(fp)

    delta_v = np.array(json_data['delta_v'])

    return jsonify(
            success=True,
            delta_v=delta_v.tolist(),
            launch_date=json_data['launch_date'],
            arrival_date=json_data['arrival_date'],
            launch_time=json_data['launch_time'],
            flight_time=json_data['flight_time'],
            min_delta_v=json_data['min_delta_v']
            )

def main():
    APP.run(host="0.0.0.0", port=5001, debug=True)

if __name__ == "__main__":
    main()

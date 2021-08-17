"""Provides additional plotting functions.

"""
from datetime import datetime
from math import sqrt

import matplotlib.pyplot as plt
import numpy as np
from pykep import epoch_from_string, AU, epoch, DAY2SEC, lambert_problem
from pykep.planet import jpl_lp
from pykep.orbit_plots import plot_lambert, plot_planet

from tridentweb.planet import Planet
from tridentweb.pykep_addons import orbit_positions, lambert_positions
from tridentweb.star import Star


def plot_orbits(planets):
    """Plots a set of orbits.

    Parameters:
        planets: An iterable of pykep.planets.

    Returns:
        system_x: A list of lists. Each internal list contains the x positions of a single orbit.
        system_y: A list of lists. Each internal list contains the y positions of a single orbit.
        system_z: A list of lists. Each internal list contains the z positions of a single orbit.
        planet_positions: A list of 3 element lists. Each internal list contains the x, y, and z
            positions of a single orbiting object at the time specified.
        planet_names: A list of names for the orbiting objects.
        planet_colors: A list of colors for the orbiting objects
    """
    t0 = epoch_from_string(str(datetime.now()))

    # Longitude zero (fall equinox)
    #t0 = epoch_from_string("2020-09-22 00:00:00")

    # Epoch
    #t0 = epoch_from_string("2000-01-01 00:00:00")

    # Epoch Offset 1 Eta Veneris 3
    #t0 = epoch(-73)

    system_x = list()
    system_y = list()
    system_z = list()
    planet_positions = list()
    planet_names = list()
    planet_colors = list()

    for planet in planets:
        planet_x, planet_y, planet_z = tuple(x / AU for x in orbit_positions(planet.planet, t0))
        system_x.append(planet_x.tolist())
        system_y.append(planet_y.tolist())
        system_z.append(planet_z.tolist())
        planet_positions.append(list((planet_x[0], planet_y[0], planet_z[0])))
        #if planet.id == -455609026:
            #planet_colors.append("green")
        #elif planet.id == -1385166447:
            #planet_colors.append("orange")
        #else:
            #planet_colors.append("gray")
        if planet.inclination == 0 and planet.longitude_of_ascending_node == 0:
            planet_colors.append("green")
        else:
            planet_colors.append("gray")
        
        planet_names.append(planet.name)

    earth = jpl_lp('earth')
    planet_x, planet_y, planet_z = tuple(x / AU for x in orbit_positions(earth, t0))
    system_x.append(planet_x.tolist())
    system_y.append(planet_y.tolist())
    system_z.append(planet_z.tolist())
    planet_positions.append(list((planet_x[0], planet_y[0], planet_z[0])))
    planet_colors.append("blue")
    planet_names.append("Earth")

    return system_x, system_y, system_z, planet_positions, planet_colors, planet_names


def plot_transfer(flask_values):
    star = Star(1817514095, 1905216634)
    origin = Planet(1817514095, 1905216634, -455609026)
    target = Planet(1817514095, 1905216634, 272811578)
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    t0 = epoch_from_string("{:%Y-%m-%d 00:00:00}".format(datetime.now()))
    t0_number = int(t0.mjd2000)
    launch_time = int(flask_values['launch_time']) + t0_number
    flight_time = int(flask_values['flight_time']) + launch_time

    t1 = epoch(int(launch_time))
    t2 = epoch(int(flight_time))

    fig = plt.figure(figsize=(4, 4))
    orbit_ax = fig.gca(projection='3d', proj_type='ortho')
    orbit_ax.scatter([0], [0], [0], color='orange')

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
        axes=orbit_ax)
    plot_planet(
        target.planet,
        t0=t2,
        color='gray',
        legend=True,
        units=AU,
        axes=orbit_ax)

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

    plot_lambert(lambert, color='purple', sol=min_n, legend=False, units=AU, axes=orbit_ax)

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

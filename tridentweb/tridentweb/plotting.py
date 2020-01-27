"""Provides additional plotting functions.

"""
from datetime import datetime

from pykep import epoch_from_string, AU
from pykep.planet import jpl_lp

from tridentweb.pykep_addons import orbit_positions

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
    #t0 = epoch_from_string("2000-01-01 00:00:00")

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
        if planet.id == -455609026:
            planet_colors.append("green")
        elif planet.id == -1385166447:
            planet_colors.append("orange")
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

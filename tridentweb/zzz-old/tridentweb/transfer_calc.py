"""Provides a crontab-compatible function for generating transfer delta_vs.

"""
from datetime import datetime, timedelta
import gzip
from json import dumps
from math import sqrt
import sys

import numpy as np
from pykep import epoch, epoch_from_string, lambert_problem, DAY2SEC

from tridentweb.star import Star
from tridentweb.planet import Planet


def transfer_delta_v(vp_input, vs_input, mu, orbit_radius):
    """Calculates the delta-v requirement for an injection or insertion maneuver.

    Parameters:
        vp_input: [x, y, z] components of planetary velocity.
            Typically created by pykep.planet.eph(t)
        vs_input: [x, y, z] components of spacecraft velocity.
            Typically created by the solution to a Lambert problem.
        mu: Gravitational parameter for planet.
        orbit_radius: Circular orbit radius around planet.

    Returns:
        delta_v: Delta-v requirement for the maneuver.
    """
    vp_vec = np.array(vp_input)
    vs_vec = np.array(vs_input)
    vsp_vec = vs_vec - vp_vec
    vsp = np.linalg.norm(vsp_vec)
    vo = sqrt(vsp * vsp + 2 * mu / orbit_radius)
    return vo - sqrt(mu / orbit_radius)


def transfer_calc():
    """A crontab-compatible function for generating transfer delta_vs."""
    arrival_time_max = 500
    launch_time_start = 0
    launch_time_end = 200

    t0 = epoch_from_string("{:%Y-%m-%d 00:00:00}".format(datetime.now()))
    t0_number = int(t0.mjd2000)

    delta_v = np.full((arrival_time_max, launch_time_end), None)
    min_delta_v = (None, None, None)

    star = Star(1817514095, 1905216634)
    origin = Planet(1817514095, 1905216634, -455609026)
    target = Planet(1817514095, 1905216634, 272811578)
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    for launch_time in range(launch_time_start, launch_time_end):
        for flight_time in range(1, arrival_time_max - launch_time):
            t1 = epoch(launch_time + t0_number)
            t2 = epoch(flight_time + launch_time + t0_number)
            dt = (t2.mjd - t1.mjd) * DAY2SEC

            r1, v1 = origin.planet.eph(t1)
            r2, v2 = target.planet.eph(t2)
            lambert = lambert_problem(list(r1), list(r2), dt, star.gm)

            lambert_delta_vs = list()
            for x in range(lambert.get_Nmax() + 1):
                lambert_delta_vs.append(
                    transfer_delta_v(
                        v1,
                        lambert.get_v1()[x],
                        origin.planet.mu_self,
                        origin_orbit_radius
                    )
                    + transfer_delta_v(
                        v2,
                        lambert.get_v2()[x],
                        target.planet.mu_self,
                        target_orbit_radius
                    )
                )

            delta_v[flight_time + launch_time, launch_time] = min(lambert_delta_vs)
            if min_delta_v[2] is None or min(lambert_delta_vs) < min_delta_v[2]:
                min_delta_v = (flight_time + launch_time, launch_time, min(lambert_delta_vs))

    launch_delta = timedelta(min_delta_v[1])
    arrival_delta = timedelta(min_delta_v[0])
    output = {
        "date": "{:%Y-%m-%d}".format(datetime.now()),
        "delta_v": delta_v.tolist(),
        "launch_date": "{:%x} ({} days)".format(datetime.now() + launch_delta, min_delta_v[1]),
        "arrival_date": "{:%x} ({} days)".format(datetime.now() + arrival_delta, min_delta_v[0]),
        "launch_time": min_delta_v[1],
        "flight_time": min_delta_v[0] - min_delta_v[1],
        "min_delta_v": "{} m/s".format(min_delta_v[2])
    }

    with gzip.open(sys.argv[1] + 'transfer_data.gz', 'wt') as file_out:
        file_out.write(dumps(output))

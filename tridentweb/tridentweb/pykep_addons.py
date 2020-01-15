"""Additions to the standard functionality provided by PyKep.

"""

from numpy import array, linspace
from pykep import SEC2DAY, epoch

def orbit_positions(planet, t0=0, N=60):
    """Provides the orbit positions for an orbiting element for one complete orbit.

    Parameters:
        planet: A PyKep object (pykep.planet._base) representing the orbiting object.
        t0: The current time. Can be a number or a PyKep object (pykep.epoch).
            Defaults to pykep.epoch(0) (MJD2000 0)
        N: Number of divisions in the complete orbit. Increase for greater precision.
            Defaults to 60

    Returns:
        X: An array of size N containing the x position of the object at each point.
        Y: An array of size N containing the y position of the object at each point.
        Z: An array of size N containing the z position of the object at each point.

        (0, 0, 0) is defined as the nominal center of the system primary star.
        +x is defined as the direction of the vernal equinox of the system primary planet.
        +y is defined as the direction of the summer solstice of the system primary planet.
    """

    if not isinstance(t0, epoch):
        t0 = epoch(t0)

    orbit_period = planet.compute_period(t0) * SEC2DAY
    when = linspace(0, orbit_period, N)

    x = array([0.0] * N)
    y = array([0.0] * N)
    z = array([0.0] * N)

    for i, day in enumerate(when):
        r, _ = planet.eph(epoch(t0.mjd2000 + day))
        x[i] = r[0]
        y[i] = r[1]
        z[i] = r[2]

    return x, y, z

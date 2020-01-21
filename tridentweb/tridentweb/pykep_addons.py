"""Additions to the standard functionality provided by PyKep.

"""

from math import atan2, cos, pi, sin, sqrt
from numpy import array, linspace
from pykep import SEC2DAY, epoch, propagate_lagrangian

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

def lambert_positions(lambert, sol=0, N=60):
    """Provides the positions for an object from a Lambert problem solution.

    Parameters:
        lambert: A PyKep object (pykep.lambert_problem) for the lambert problem.
        sol: Solution number from lambert to use.
            Must be in the range of 0 <= sol <= lambert.Nmax*2
            Defaults to 0
        N: Number of divisions in the position plt. Increase for greater precision.
            Defaults to 60

    Returns:
        X: An array of size N containing the x position of the object at each point.
        Y: An array of size N containing the y position of the object at each point.
        Z: An array of size N containing the z position of the object at each point.

        (0, 0, 0) is defined as the nominal center of the system primary star.
        +x is defined as the direction of the vernal equinox of the system primary planet.
        +y is defined as the direction of the summer solstice of the system primary planet.
    """

    if sol > lambert.get_Nmax() * 2:
        raise ValueError

    r = lambert.get_r1()
    v = lambert.get_v1()[sol]
    tof = lambert.get_tof()
    mu = lambert.get_mu()
    dt = tof / (N - 1)

    x = array([0.0] * N)
    y = array([0.0] * N)
    z = array([0.0] * N)

    for i in range(N):
        x[i] = r[0]
        y[i] = r[1]
        z[i] = r[2]
        r, v = propagate_lagrangian(r, v, dt, mu)

    return x, y, z

def eccentric_from_true(e, f):
    """Provides the eccentric anomaly given the eccentricity of an orbit and the true anomaly of
        an object in that orbit.

    Parameters:
        e: Eccentricity of the orbit. Dimensionless
        f: True anomaly of an object in the orbit. Radians

    Returns:
        E: The eccentric anomaly of the object in the orbit.
    """

    return atan2(sqrt(1 - e ** 2) * sin(f), e + cos(f)) % (2 * pi)

def mean_from_eccentric(e, E):
    """Provides the mean anomaly given the eccentricity of an orbit and the eccentric anomaly of
        an object in that orbit.

    Parameters:
        e: Eccentricty of the orbit. Dimensionless
        E: Eccentric anomaly of an object in the orbit. Radians

    Returns:
        M: The mean anomaly of the object in the orbit.
    """

    return E - e * sin(E)

def mean_from_true(e, f):
    """Provides the mean anomaly given the eccentricty of an orbit and the true anomaly of an
        object in that orbit.

    Parameters:
        e: Eccentricty of the orbit. Dimensionless
        f: True anomaly of an object in the oribt. Radians

    Returns:
        M: The mean anomaly of the object in the orbit. Radians
    """

    return mean_from_eccentric(e, eccentric_from_true(e, f))

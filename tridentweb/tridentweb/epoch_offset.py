"""Determines the epoch offset for a given celestial body.

"""
from math import pi
from numpy import arctan2
from pykep import epoch


def epoch_offset(planet, target_angle=0, low_bound=-100, high_bound=100, iterations=1000):
    """Calculates the epoch offset for an orbiting element.

    Parameters:
        planet: A Pykep object (pykep.planet._base) representing the orbiting element.
        target_angle: The angle, in radians, against which to calculate the epoch offset.
            The angle must be in the range -pi < x <= pi.
            Defaults to 0 rad. 0 rad for the Earth system corresponds to the first point of Ares
                pi rad corresponds to the vernal equinox of the system primary planet.
        low_bound: The low bound for number of standard days to check.
            Defaults to -100; 100 days before the J2000 epoch.
        high_bound: The high bound for number of standard days to check.
            Defaults to 100; 100 days after the J2000 epoch.
        iterations: The number of iterations to run.

    Returns:
        A tuple containing:
            Epoch Offset: The offset, in standard days, between the position of the celestial body
                at the start of the J2000 epoch and the start of the celestial body's local
                calendar, such that J2000 + Epoch Offset = Local Epoch

                For example, on Jan 1 2000, a celestial body with an Epoch Offset of -50 would be
                on the 50th standard day of its local calendar.
            Error: The error, in radians, between the desired target_angle and final calculated
                angle. Large values indicate insufficient bound parameters.
    """
    if target_angle > pi or target_angle <= -pi:
        raise ValueError

    if low_bound >= high_bound:
        raise ValueError

    if iterations < 1:
        raise ValueError

    for _ in range(iterations):
        midpoint = (low_bound + high_bound) / 2
        r_a, _ = planet.eph(epoch(low_bound))
        r_c, _ = planet.eph(epoch(midpoint))

        angle_a = arctan2(r_a[1], r_a[0])
        angle_c = arctan2(r_c[1], r_c[0])

        if abs(target_angle - angle_a) > abs(angle_c - angle_a):
            low_bound = midpoint
        else:
            high_bound = midpoint

    return midpoint, angle_c - target_angle

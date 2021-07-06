from datetime import datetime

from flask import Blueprint, request as flask_request, has_app_context, jsonify
from flask_cors import cross_origin
from numpy import linspace, array
from pykep import epoch_from_string, SEC2DAY, epoch, DAY2SEC, lambert_problem
from pykep.planet import jpl_lp

from tridentweb.constant import Constant
from tridentweb.planet import Planet
from tridentweb.pykep_addons import lambert_positions
from tridentweb.star import Star

bp = Blueprint("orbit", __name__, url_prefix="/orbit")


@bp.route("/earthposition", methods=['POST'])
@cross_origin()
def earth_position():
    """Returns position data for Earth.

    Parameters (included in POST body, as JSON):
        t0: POSIX timestamp (number of seconds since Jan 1, 1970 UTC)

    Returns:
        x: x position of object (m)
        y: y position of object (m)
        z: z position of object (m)

        (0, 0, 0) is defined as the nominal center of the system primary star.
        (x, y, 0) is defined as the orbital plane of the system primary planet.
        +x is defined as the direction of the autumnal equinox of the system
            primary planet.
        +y is defined as the direction of the winter solstice of the system
            primary planet.
    """

    earth = jpl_lp('earth')
    t0 = epoch_from_string("{:%Y-%m-%d %H:%M:%S}".format(
        datetime.fromtimestamp(flask_request.json['t0'])))

    (x, y, z), _ = earth.eph(t0)
    return jsonify(
        x=x,
        y=y,
        z=z) if has_app_context() else (x, y, z)


@bp.route("/earthpath", methods=['POST'])
@cross_origin()
def earth_path():
    earth = jpl_lp('earth')
    t0 = epoch_from_string("{:%Y-%m-%d %H:%M:%S}".format(
        datetime.fromtimestamp(flask_request.json['t0'])))

    divisions = 60
    orbit_period = earth.compute_period(t0) * SEC2DAY
    when = linspace(0, orbit_period, divisions)

    x = array([0.0] * divisions)
    y = array([0.0] * divisions)
    z = array([0.0] * divisions)

    for i, day in enumerate(when):
        r, _ = earth.eph(epoch(t0.mjd2000 + day))
        x[i] = r[0]
        y[i] = r[1]
        z[i] = r[2]

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist()) if has_app_context() else (x, y, z)


@bp.route("/lambert", methods=['POST'])
@cross_origin()
def lambert_transfer():
    star = Star(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']))
    origin = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['origin_planet_id']))
    target = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['target_planet_id']))

    # TODO: Customizable orbits
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    # TODO: Customizable flight time
    flight_time = 60
    t1 = epoch_from_string(flask_request.json['launch_date'])
    t2 = epoch(int(t1.mjd2000) + flight_time)
    dt = (t2.mjd - t1.mjd) * DAY2SEC

    r1, v1 = origin.planet.eph(t1)
    r2, v2 = target.planet.eph(t2)
    lambert = lambert_problem(list(r1), list(r2), dt, star.gm)

    x, y, z = lambert_positions(lambert)

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist()) if has_app_context() else (x, y, z)


@bp.route("/path", methods=['POST'])
@cross_origin()
def orbit_path():
    planet = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['planet_id']))

    divisions = 60
    orbit_period = planet.planet.compute_period(epoch(0)) * SEC2DAY
    when = linspace(0, orbit_period, divisions)

    x = array([0.0] * divisions)
    y = array([0.0] * divisions)
    z = array([0.0] * divisions)

    for i, day in enumerate(when):
        r, _ = planet.planet.eph(epoch(day))
        x[i] = r[0]
        y[i] = r[1]
        z[i] = r[2]

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist()) if has_app_context() else (x, y, z)


@bp.route("/planet", methods=['POST'])
@cross_origin()
def get_planet():
    planet = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['planet_id']))

    return jsonify(
        name=planet.name,
        mass=planet.mass,
        radius=planet.radius,
        semimajorAxis=planet.semimajor_axis,
        eccentricity=planet.eccentricity,
        inclination=planet.inclination,
        longitudeOfAscendingNode=planet.longitude_of_ascending_node,
        argumentOfPeriapsis=planet.argument_of_periapsis,
        trueAnomalyAtEpoch=planet.true_anomaly_at_epoch,

        starGM=planet.star_gm,
        GM=planet.gm) if has_app_context() else planet


@bp.route("/Rpln", methods=['GET'])
@cross_origin()
def get_Rpln():
    planet_radius = Constant("Rpln")
    return jsonify(
        Rpln=planet_radius.value
    ) if has_app_context() else planet_radius

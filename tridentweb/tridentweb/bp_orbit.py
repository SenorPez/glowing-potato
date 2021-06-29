from datetime import datetime

from flask import Blueprint, request as flask_request, has_app_context, jsonify
from flask_cors import cross_origin
from numpy import linspace, array
from pykep import epoch_from_string, SEC2DAY, epoch
from pykep.planet import jpl_lp

from tridentweb.planet import Planet

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


@bp.route("/path", methods=['POST'])
@cross_origin()
def orbit_path():
    planet = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['planet_id']))

    t0 = epoch_from_string("{:%Y-%m-%d %H:%M:%S}".format(
        datetime.fromtimestamp(flask_request.json['t0'])))

    divisions = 60
    orbit_period = planet.planet.compute_period(t0) * SEC2DAY
    when = linspace(0, orbit_period, divisions)

    x = array([0.0] * divisions)
    y = array([0.0] * divisions)
    z = array([0.0] * divisions)

    for i, day in enumerate(when):
        r, _ = planet.planet.eph(epoch(t0.mjd2000 + day))
        x[i] = r[0]
        y[i] = r[1]
        z[i] = r[2]

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist()) if has_app_context() else (x, y, z)


@bp.route("/position", methods=['POST'])
@cross_origin()
def orbit_position():
    """Returns position data for an orbiting object.

    Parameters (included in POST body, as JSON):
        system_id: Solar system ID, for use with the Trident API
        star_id: Star ID, for use with the Trident API
        planet_id: Planet ID, for use with the Trident API
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
    planet = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['planet_id']))
    t0 = epoch_from_string("{:%Y-%m-%d %H:%M:%S}".format(
        datetime.fromtimestamp(flask_request.json['t0'])))

    (x, y, z), _ = planet.planet.eph(t0)
    return jsonify(
        x=x,
        y=y,
        z=z) if has_app_context() else (x, y, z)

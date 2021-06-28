from flask import Blueprint, request as flask_request, has_app_context, jsonify
from pykep import epoch

from tridentweb.planet import Planet

bp = Blueprint("orbit", __name__, url_prefix="/orbit")


@bp.route("/position", methods=['POST'])
def orbit_position():
    """Returns position data for an orbiting object.

    Parameters (included in POST data):
        system_id: Solar system ID, for use with the Trident API
        star_id: Star ID, for use with the Trident API
        planet_id: Planet ID, for use with the Trident API
        t0: The current time in days since the J2000 epoch
            (2000-Jan-01 00:00:00); defaults to 0

    Returns:
        x: x position of object (AU)
        y: y position of object (AU)
        z: z position of object (AU)

        (0, 0, 0) is defined as the nominal center of the system primary star.
        (x, y, 0) is defined as the orbital plane of the system primary planet.
        +x is defined as the direction of the vernal equinox of the system
            primary planet.
        +y is defined as the direction of the summer solstice of the system
            primary planet.
    """
    planet = Planet(
        int(flask_request.form['system_id']),
        int(flask_request.form['star_id']),
        int(flask_request.form['planet_id']))
    t0 = int(flask_request.form['t0'])

    (x, y, z), _ = planet.planet.eph(epoch(t0))
    return jsonify(
        x=x,
        y=y,
        z=z) if has_app_context() else (x, y, z)

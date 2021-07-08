from datetime import datetime

from flask import Blueprint, request as flask_request, has_app_context, jsonify
from flask_cors import cross_origin
from pykep import epoch_from_string, epoch, DAY2SEC, lambert_problem
from pykep.planet import jpl_lp

from tridentweb.constant import Constant
from tridentweb.planet import Planet
from tridentweb.pykep_addons import orbit_positions, transfer_delta_v, lambert_positions
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
    x, y, z = orbit_positions(jpl_lp('earth'))

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist()) if has_app_context() else (x, y, z)


def lambert_transfer(request_data):
    star = Star(
        int(request_data.json['system_id']),
        int(request_data.json['star_id']))
    origin = Planet(
        int(request_data.json['system_id']),
        int(request_data.json['star_id']),
        int(request_data.json['origin_planet_id']))
    target = Planet(
        int(request_data.json['system_id']),
        int(request_data.json['star_id']),
        int(request_data.json['target_planet_id']))

    # TODO: Customizable orbits
    origin_orbit_radius = origin.planet.radius + 200000
    target_orbit_radius = target.planet.radius + 200000

    # TODO: Customizable max flight time
    MAX_FLIGHT_TIME = 147  # 75% of 28 week endurance

    lambert_dvs = list()

    t1 = epoch_from_string(request_data.json['launch_date'])

    for flight_time in range(1, MAX_FLIGHT_TIME + 1):
        t2 = epoch(int(t1.mjd2000) + flight_time)
        dt = (t2.mjd - t1.mjd) * DAY2SEC

        r1, v1 = origin.planet.eph(t1)
        r2, v2 = target.planet.eph(t2)
        lambert = lambert_problem(list(r1), list(r2), dt, star.gm)

        lambert_dvs.append(
            {'flight_time': flight_time,
             'dv':
                 transfer_delta_v(
                     v1,
                     lambert.get_v1()[0],
                     origin.planet.mu_self,
                     origin_orbit_radius
                 )
                 + transfer_delta_v(
                     v2,
                     lambert.get_v2()[0],
                     target.planet.mu_self,
                     target_orbit_radius
                 ),
             'lambert': lambert
             }
        )

    return sorted(lambert_dvs, key=lambda item: item['flight_time']), star.gm


@bp.route("/dvlambert", methods=['POST'])
@cross_origin()
def dv_lambert_transfer():
    sorted_solutions, mu = lambert_transfer(flask_request)

    min_delta_v = min([x['dv'] for x in sorted_solutions])
    result = next(filter(lambda x: x['dv'] == min_delta_v, sorted_solutions))

    x, y, z = lambert_positions(result['lambert'])
    r1 = result['lambert'].get_r1()
    v1 = result['lambert'].get_v1()[0]

    dv = result['dv']
    flight_time = result['flight_time']

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist(),
        r1=r1,
        v1=v1,
        mu=mu,
        dv=dv,
        flight_time=flight_time) if has_app_context() else ()


@bp.route("/ftlambert", methods=['POST'])
@cross_origin()
def ft_lambert_transfer():
    sorted_solutions, mu = lambert_transfer(flask_request)

    # TODO: Customizable max dV
    MAX_DV = 71250  # 75% of 95 km/sec

    min_ft = min([x['flight_time'] for x in sorted_solutions if x['dv'] <= MAX_DV])
    result = next(filter(lambda x: x['flight_time'] == min_ft, sorted_solutions))

    x, y, z = lambert_positions(result['lambert'])
    r1 = result['lambert'].get_r1()
    v1 = result['lambert'].get_v1()[0]

    dv = result['dv']
    flight_time = result['flight_time']

    return jsonify(
        x=x.tolist(),
        y=y.tolist(),
        z=z.tolist(),
        r1=r1,
        v1=v1,
        mu=mu,
        dv=dv,
        flight_time=flight_time) if has_app_context() else ()


@bp.route("/path", methods=['POST'])
@cross_origin()
def orbit_path():
    planet = Planet(
        int(flask_request.json['system_id']),
        int(flask_request.json['star_id']),
        int(flask_request.json['planet_id']))

    x, y, z = orbit_positions(planet.planet)

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

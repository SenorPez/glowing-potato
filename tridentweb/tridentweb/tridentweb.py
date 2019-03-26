"""
Main execution script.
"""
from datetime import datetime
from flask import Flask, has_app_context, jsonify
from flask_cors import CORS
import numpy as np
from pykep import epoch, epoch_from_string, AU, SEC2DAY

from tridentweb.planet import Planet
from tridentweb.star import Star

"""Launches Flask application to respond to requests."""
APP = Flask(__name__)
CORS(APP, resources={r"/*": {"origins": "http://senorpez.com"}})

@APP.route("/epochoffset", methods=['GET'])
def epoch_offset():
    star = Star(1817514095, 1905216634)
    planet = Planet(1817514095, 1905216634, -455609026)

    winter = np.arctan2(-1, 0)

    a = -75.0
    b = 0.0

    for _ in range(1000):
        c = (a + b) / 2
        r_c, _ = planet.planet.eph(epoch(c))

        angle = np.arctan2(r_c[1], r_c[0])

        if angle - winter < 0:
            a = c
        else:
            b = c

    return jsonify(c) if has_app_context() else c

@APP.route("/orbit", methods=['GET'])
def orbit():
    star = Star(1817514095, 1905216634)
    planets = [
            Planet(1817514095, 1905216634, -1485460920),
            Planet(1817514095, 1905216634, -1722015868),
            Planet(1817514095, 1905216634, -455609026),
            Planet(1817514095, 1905216634, 272811578)]

    t0 = epoch_from_string(str(datetime.now()))
    x_val = list()
    y_val = list()
    z_val = list()
    planet_positions = list()
    planet_names = list()
    planet_colors = list()

    for orbiter in planets:
        orbit_period = orbiter.planet.compute_period(epoch(0)) * SEC2DAY
        orbit_when = np.linspace(0, orbit_period, 60)

        x = np.zeros(60)
        y = np.zeros(60)
        z = np.zeros(60)

        for i, day in enumerate(orbit_when):
            r, _ = orbiter.planet.eph(epoch(t0.mjd2000 + day))
            x[i] = r[0] / AU
            y[i] = r[1] / AU
            z[i] = r[2] / AU

        x_val.append(x.tolist())
        y_val.append(y.tolist())
        z_val.append(z.tolist())

        planet_positions.append(list((x[0], y[0], z[0])))
        planet_colors.append("gray")
        planet_names.append(orbiter.name)

    return jsonify(
        success=True,
        x=x_val,
        y=y_val,
        z=z_val,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else x_val

def main():
    print(orbit())
    #APP.run(host="0.0.0.0", port=5002)

if __name__ == "__main__":
    main()

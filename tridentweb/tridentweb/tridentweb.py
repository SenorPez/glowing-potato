"""
Main execution script.
"""
from flask import Flask
from flask_cors import CORS
import numpy as np
from pykep import epoch

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

    print(c)

def main():
    epoch_offset()
    #APP.run(host="0.0.0.0", port=5002)

if __name__ == "__main__":
    main()

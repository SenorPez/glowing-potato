"""Main Flask execution script.

"""
import gzip
import json

from flask import Flask, has_app_context, jsonify, request as flask_request
from flask_cors import CORS
import numpy as np

from tridentweb.epoch_offset import epoch_offset
from tridentweb.plotting import plot_orbits, plot_transfer
from tridentweb.planet import Planet
from tridentweb.star import Star

APP = Flask(__name__)
CORS(APP, resources={r"/*": {"origins": "http://senorpez.com"}})

@APP.route("/epochoffset", methods=['GET'])
def get_epoch_offset():
    """Produces the epoch offset for 1 Eta Veneris 3."""
    planet = Planet(1817514095, 1905216634, -455609026)
    summer = np.arctan2(-1, 0)
    offset, error = epoch_offset(planet.planet, summer)
    return jsonify(offset, error) if has_app_context() else (offset, error)

@APP.route("/systemorbits", methods=['GET'])
def systemorbits():
    """Produces plot data for the 1 Eta Veneris system and 2 Eta Veneris."""
    planets = [
        Planet(1817514095, 1905216634, -1485460920),
        Planet(1817514095, 1905216634, -1722015868),
        Planet(1817514095, 1905216634, -455609026),
        Planet(1817514095, 1905216634, 272811578),
        Planet(1817514095, 1905216634, -393577255),
        Planet(1817514095, 1905216634, -1420756477),
        Planet(1817514095, 1905216634, -93488736),
        Star(1817514095, -1385166447, Star(1817514095, 1905216634))]

    system_x, system_y, system_z, planet_positions, planet_colors, planet_names \
        = plot_orbits(planets)
    return jsonify(
        success=True,
        x=system_x,
        y=system_y,
        z=system_z,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else planet_names

@APP.route("/innerorbits", methods=['GET'])
def innerorbits():
    """Produces plot data for the inner planets of the 1 Eta Veneris system, with an Earth
        orbit overlayed."""
    planets = [
        Planet(1817514095, 1905216634, -1485460920),
        Planet(1817514095, 1905216634, -1722015868),
        Planet(1817514095, 1905216634, -455609026),
        Planet(1817514095, 1905216634, 272811578)]

    system_x, system_y, system_z, planet_positions, planet_colors, planet_names \
        = plot_orbits(planets)
    return jsonify(
        success=True,
        x=system_x,
        y=system_y,
        z=system_z,
        p=planet_positions,
        c=planet_colors,
        n=planet_names) if has_app_context() else planet_names

@APP.route("/plottransfer", methods=['POST'])
def plottransfer():
    """Produces 1 3D and 3 2D plots of a specified transfer between 1 Eta Veneris 3 and 1 Eta
        Veneris 4."""
    plot_transfer(flask_request.values)
    return jsonify(success=True)

@APP.route("/transfer", methods=['POST'])
def transfer():
    """Accesses generated transfer data between 1 Eta Veneris 3 and 1 Eta Veneris 4. That data
        is updated outside of this tool (see transfer_calc.py and transfer_data.gz)."""
    with gzip.open(
            '/home/senorpez/glowing-potato/tridentweb/tridentweb/transfer_data.gz',
            'rt') as file_pointer:
        json_data = json.load(file_pointer)

    delta_v = np.array(json_data['delta_v'])

    return jsonify(
        success=True,
        delta_v=delta_v.tolist(),
        launch_date=json_data['launch_date'],
        arrival_date=json_data['arrival_date'],
        launch_time=json_data['launch_time'],
        flight_time=json_data['flight_time'],
        min_delta_v=json_data['min_delta_v'])

def main():
    """Main execution."""
    APP.run(host="0.0.0.0", port=5001, debug=True)

if __name__ == "__main__":
    main()

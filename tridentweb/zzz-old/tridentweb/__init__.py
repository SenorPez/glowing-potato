from flask import Flask
from flask_cors import CORS


def create_app(app_config=None):
    app = Flask(__name__, instance_relative_config=True)
    CORS(app, resources={r"/*": {"origins": "https://www.senorpez.com"}})

    if app_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(app_config)

    from tridentweb import bp_orbit
    app.register_blueprint(bp_orbit.bp)

    return app


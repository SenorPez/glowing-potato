from flask import Flask


def create_app(app_config=None):
    app = Flask(__name__, instance_relative_config=True)

    if app_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(app_config)

    return app

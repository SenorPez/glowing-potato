from flask import Flask
from flask_cors import CORS


def create_app():
    app = Flask(__name__, instance_relative_config=True)
    CORS(app, resources={r"/*": {"origins": "https://www.senorpez.com"}})

    @app.route('/hello')
    def hello():
        return 'Hello, World!'

    return app


"""
Main execution script.
"""
from flask import Flask
from flask_cors import CORS

def main():
    """
    Launches Flask application to respond to requests.
    """
    application = Flask(__name__)
    CORS(application, resources={r"/*": {"origins": "http://senorpez.com"}})

    application.run(host="0.0.0.0", port=5002)


if __name__ == "__main__":
    main()

from tridentweb import create_app


def app():
    """Create and configure a Flask app for each test."""
    test_app = create_app({"TESTING": True})
    return test_app


def client(test_app):
    """A test client for the Flask app."""
    return test_app.test_client()

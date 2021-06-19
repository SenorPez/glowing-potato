import unittest

from conftest import app, client


class MyTestCase(unittest.TestCase):
    def test_something(self):
        with client(app()) as test_client:
            response = test_client.get('/hello')
            self.assertEqual(response.status_code, 200)


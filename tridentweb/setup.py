"""
Setup file for Trident API Web Backend
"""

from setuptools import setup, find_packages
from codecs import open
from os import path

here = path.abspath(path.dirname(__file__))

with open(path.join(here, 'README.md'), encoding='utf-8') as f:
    long_description = f.read()

setup(
    name='tridentweb',
    version='0.0.1.dev1',
    description='Trident API Web Backend Development',
    long_description=long_description,
    url='https://github.com/SenorPez/glowing-potato',
    author='Senor Pez',
    author_email='contact_at_github@example.org',
    license='MIT',
    classifiers=[
        'Development Status :: 4 - Beta',
        'Intended Audience :: Developers',
        'License :: OSI Approved :: MIT License',
        'Programming Language :: Python :: 3.3',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: 3.5',
        'Programming Language :: Python :: 3.6',
        'Programming Language :: Python :: 3.7',
        'Programming Language :: Python :: 3 :: Only',
    ],
    keywords='trident',
    packages=find_packages(exclude=['test']),
    python_requires='~=3.3',
    install_requires=['flask', 'flask_cors', 'matplotlib', 'numpy', 'pykep', 'requests'],
    extras_require={
        'dev': [],
        'test': []
    },
    entry_points={
        'console_scripts': [
            'tridentweb=tridentweb.tridentweb:main'
        ],
    },
    project_urls={
        'Feature Requests and Bug Reports': 'https://github.com/SenorPez/glowing-potato/issues'
    },
)

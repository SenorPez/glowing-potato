# Trident Tools and Support
[![Build Status](https://travis-ci.org/SenorPez/glowing-potato.svg?branch=develop)](https://travis-ci.org/SenorPez/glowing-potato)
[![codecov](https://codecov.io/gh/SenorPez/glowing-potato/branch/develop/graph/badge.svg)](https://codecov.io/gh/SenorPez/glowing-potato)

Trident consists of tools and support for something vague and pointless. So, really, like most of what you'll find on Github dot com.

## Current Release
**Release 2** is the most current release of Trident and consists of the following components. See below for detailed release notes for each component.

- **API Version 1.1.0**
- **Clock Version 1.0.0**
- **Web Version 2.0.0**

## API
The current version of **API** is **Version 1.1.0**.

The API provides a HAL-compliant HATEOAS REST application for serving data

The reference implementation is located at http://trident.senorpez.com/.
Complete documentation of acceptable headers, HTTP methods, and endpoints is located at http://\<server\>/docs/reference.html.

### Changelog
**Version 1.1.0**
Added simple planetary calendar support.

**Version 1.0.0**
First release.

## Clock
The current version of **Clock** is **Version 1.0.0**.

Clock is an Android application that provides a conversion between standard time (UTC) and local 1 Eta Veneris 3 time (STK), and displays it using a local clock format. If none of this makes sense to you, don't worry.

### Changelog
**Version 1.0.0**
First release.

## Web
The current version of **Web** is **Version 2.0.0**.

Web is a combination of Python scripts and Javascript pages that implement several web applications.

### Changelog
**Version 2.0.0**
Rework of Python scripts to be object-oriented, unit tested, and integration tested against the reference implementation of **API**. Rework of Web pages to use updated Python workers.

**Version 1.0.0**
First release.

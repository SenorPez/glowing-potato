package com.senorpez.trident.api;

class SolarSystemNotFoundException extends RuntimeException {
    SolarSystemNotFoundException(final int id) {
        super(String.format("Solar system with ID of %d not found", id));
    }
}

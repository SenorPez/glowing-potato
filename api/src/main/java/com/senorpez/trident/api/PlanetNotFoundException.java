package com.senorpez.trident.api;

class PlanetNotFoundException extends RuntimeException {
    PlanetNotFoundException(final int id) {
        super(String.format("Planet with ID of %d not found", id));
    }
}

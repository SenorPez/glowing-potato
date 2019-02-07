package com.senorpez.trident.api;

public class SolarSystemNotFoundException extends RuntimeException {
    public SolarSystemNotFoundException(final int id) {
        super(String.format("Solar system with ID of %d not found", id));
    }
}

package com.senorpez.trident.api;

class PlanetaryCalendarNotFoundException extends RuntimeException {
    PlanetaryCalendarNotFoundException(final int id) {
        super(String.format("Calendar with ID of %d not found", id));
    }
}

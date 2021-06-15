package com.senorpez.trident.api;

import java.util.Locale;

class SolarSystemNotFoundException extends RuntimeException {
    SolarSystemNotFoundException(final int id) {
        super(String.format(Locale.ENGLISH, "Solar system with ID of %d not found", id));
    }
}

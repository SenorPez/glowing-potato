package com.senorpez.trident.api;

import java.util.Locale;

class PlanetNotFoundException extends RuntimeException {
    PlanetNotFoundException(final int id) {
        super(String.format(Locale.ENGLISH, "Planet with ID of %d not found", id));
    }
}

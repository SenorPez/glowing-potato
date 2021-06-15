package com.senorpez.trident.api;

import java.util.Locale;

class PlanetaryCalendarNotFoundException extends RuntimeException {
    PlanetaryCalendarNotFoundException(final int id) {
        super(String.format(Locale.ENGLISH, "Calendar with ID of %d not found", id));
    }
}

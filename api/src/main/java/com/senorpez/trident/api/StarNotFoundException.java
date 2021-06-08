package com.senorpez.trident.api;

import java.util.Locale;

class StarNotFoundException extends RuntimeException {
    StarNotFoundException(final int id) {
        super(String.format(Locale.ENGLISH, "Star with ID of %d not found", id));
    }
}

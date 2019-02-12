package com.senorpez.trident.api;

class StarNotFoundException extends RuntimeException {
    StarNotFoundException(final int id) {
        super(String.format("Star with ID of %d not found", id));
    }
}

package com.senorpez.trident.api;

class ConstantNotFoundException extends RuntimeException {
    ConstantNotFoundException(final String id) {
        super(String.format("Constant with ID of %s not found", id));
    }
}

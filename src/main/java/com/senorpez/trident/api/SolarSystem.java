package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

class SolarSystem {
    private final int id;
    private final String name;

    SolarSystem(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name) {
        this.id = id;
        this.name = name;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }
}

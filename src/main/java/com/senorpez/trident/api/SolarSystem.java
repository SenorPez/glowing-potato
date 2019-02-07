package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolarSystem {
    private final int id;
    private final String name;

    public SolarSystem(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

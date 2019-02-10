package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Star {
    private final int id;
    private final String name;
    private final float solarMass;

    Star(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("solarMass") final float solarMass) {
        this.id = id;
        this.name = name;
        this.solarMass = solarMass;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    float getSolarMass() {
        return solarMass;
    }
}

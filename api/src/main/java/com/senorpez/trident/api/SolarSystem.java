package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

class SolarSystem {
    private final int id;
    private final String name;
    private final Set<Star> stars;

    @JsonCreator
    SolarSystem(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("stars") final JsonNode stars) {
        this.id = id;
        this.name = name;
        this.stars = Application.getData(Star.class, stars);
    }

    SolarSystem(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("stars") final Set<Star> stars) {
        this.id = id;
        this.name = name;
        this.stars = stars;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    Set<Star> getStars() {
        return stars;
    }
}

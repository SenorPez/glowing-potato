package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public class Star {
    private final int id;
    private final String name;
    private final float solarMass;
    private final Set<Planet> planets;

    @JsonCreator
    Star(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("solarMass") final float solarMass,
            @JsonProperty("planets") final JsonNode planets) {
        this.id = id;
        this.name = name;
        this.solarMass = solarMass;
        this.planets = Application.getData(Planet.class, planets);
    }

    Star(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("solarMass") final float solarMass,
            @JsonProperty("planets") final Set<Planet> planets) {
        this.id = id;
        this.name = name;
        this.solarMass = solarMass;
        this.planets = planets;
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

    Set<Planet> getPlanets() {
        return planets;
    }
}

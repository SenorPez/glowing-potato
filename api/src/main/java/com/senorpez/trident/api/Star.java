package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Set;

public class Star {
    private final int id;
    private final String name;
    private final float mass;

    private final Float semimajorAxis;
    private final Float eccentricity;
    private final Float inclination;
    private final Float longitudeOfAscendingNode;
    private final Float argumentOfPeriapsis;
    private final Float trueAnomalyAtEpoch;

    private final Set<Planet> planets;

    @JsonCreator
    Star(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("mass") final float mass,
            @JsonProperty("semimajorAxis") final Float semimajorAxis,
            @JsonProperty("eccentricity") final Float eccentricity,
            @JsonProperty("inclination") final Float inclination,
            @JsonProperty("longitudeOfAscendingNode") final Float longitudeOfAscendingNode,
            @JsonProperty("argumentOfPeriapsis") final Float argumentOfPeriapsis,
            @JsonProperty("trueAnomalyAtEpoch") final Float trueAnomalyAtEpoch,
            @JsonProperty("planets") final JsonNode planets) {
        this.id = id;
        this.name = name;
        this.mass = mass;
        this.semimajorAxis = semimajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;
        this.planets = Application.getData(Planet.class, planets);
    }

    Star(
            final int id,
            final String name,
            final float mass,
            final Set<Planet> planets) {
        this(id, name, mass, null, null, null, null, null, null, planets);
    }

    Star(
            final int id,
            final String name,
            final float mass,
            final Float semimajorAxis,
            final Float eccentricity,
            final Float inclination,
            final Float longitudeOfAscendingNode,
            final Float argumentOfPeriapsis,
            final Float trueAnomalyAtEpoch,
            final Set<Planet> planets) {
        this.id = id;
        this.name = name;
        this.mass = mass;
        this.semimajorAxis = semimajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;
        this.planets = planets;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    float getMass() {
        return mass;
    }

    Float getSemimajorAxis() {
        return semimajorAxis;
    }

    Float getEccentricity() {
        return eccentricity;
    }

    Float getInclination() {
        return inclination;
    }

    Float getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    Float getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    Float getTrueAnomalyAtEpoch() {
        return trueAnomalyAtEpoch;
    }

    Set<Planet> getPlanets() {
        return planets;
    }
}

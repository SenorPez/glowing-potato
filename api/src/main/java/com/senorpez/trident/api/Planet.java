package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.ApplicationContextInitializer;

import java.util.Set;

class Planet {
    private final int id;
    private final String name;

    private final float mass;
    private final float radius;

    private final float semimajorAxis;
    private final float eccentricity;
    private final float inclination;
    private final float longitudeOfAscendingNode;
    private final float argumentOfPeriapsis;
    private final float trueAnomalyAtEpoch;

    private final Set<PlanetaryCalendar> calendars;

    Planet(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("mass") final float mass,
            @JsonProperty("radius") final float radius,
            @JsonProperty("semimajorAxis") final float semimajorAxis,
            @JsonProperty("eccentricity") final float eccentricity,
            @JsonProperty("inclination") final float inclination,
            @JsonProperty("longitudeOfAscendingNode") final float longitudeOfAscendingNode,
            @JsonProperty("argumentOfPeriapsis") final float argumentOfPeriapsis,
            @JsonProperty("trueAnomalyAtEpoch") final float trueAnomalyAtEpoch,
            @JsonProperty("calendars") final JsonNode calendars) {
        this.id = id;
        this.name = name;

        this.mass = mass;
        this.radius = radius;

        this.semimajorAxis = semimajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;

        this.calendars = Application.getData(PlanetaryCalendar.class, calendars);
    }

    Planet(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("mass") final float mass,
            @JsonProperty("radius") final float radius,
            @JsonProperty("semimajorAxis") final float semimajorAxis,
            @JsonProperty("eccentricity") final float eccentricity,
            @JsonProperty("inclination") final float inclination,
            @JsonProperty("longitudeOfAscendingNode") final float longitudeOfAscendingNode,
            @JsonProperty("argumentOfPeriapsis") final float argumentOfPeriapsis,
            @JsonProperty("trueAnomalyAtEpoch") final float trueAnomalyAtEpoch,
            @JsonProperty("calendars") final Set<PlanetaryCalendar> calendars) {
        this.id = id;
        this.name = name;

        this.mass = mass;
        this.radius = radius;

        this.semimajorAxis = semimajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;

        this.calendars = calendars;
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

    float getRadius() {
        return radius;
    }

    float getSemimajorAxis() {
        return semimajorAxis;
    }

    float getEccentricity() {
        return eccentricity;
    }

    float getInclination() {
        return inclination;
    }

    float getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    float getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    float getTrueAnomalyAtEpoch() {
        return trueAnomalyAtEpoch;
    }

    public Set<PlanetaryCalendar> getCalendars() {
        return calendars;
    }
}

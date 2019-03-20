package com.senorpez.trident.api;

class PlanetBuilder {
    private int id;
    private String name;

    private float mass;
    private float radius;

    private float semimajorAxis;
    private float eccentricity;
    private float inclination;
    private float longitudeOfAscendingNode;
    private float argumentOfPeriapsis;
    private float trueAnomalyAtEpoch;

    PlanetBuilder() {
    }

    Planet build() {
        return new Planet(
                id,
                name,
                mass,
                radius,
                semimajorAxis,
                eccentricity,
                inclination,
                longitudeOfAscendingNode,
                argumentOfPeriapsis,
                trueAnomalyAtEpoch);
    }

    PlanetBuilder setId(int id) {
        this.id = id;
        return this;
    }

    PlanetBuilder setName(String name) {
        this.name = name;
        return this;
    }

    PlanetBuilder setMass(float mass) {
        this.mass = mass;
        return this;
    }

    PlanetBuilder setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    PlanetBuilder setSemimajorAxis(float semimajorAxis) {
        this.semimajorAxis = semimajorAxis;
        return this;
    }

    PlanetBuilder setEccentricity(float eccentricity) {
        this.eccentricity = eccentricity;
        return this;
    }

    PlanetBuilder setInclination(float inclination) {
        this.inclination = inclination;
        return this;
    }

    PlanetBuilder setLongitudeOfAscendingNode(float longitudeOfAscendingNode) {
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        return this;
    }

    PlanetBuilder setArgumentOfPeriapsis(float argumentOfPeriapsis) {
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        return this;
    }

    PlanetBuilder setTrueAnomalyAtEpoch(float trueAnomalyAtEpoch) {
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;
        return this;
    }
}

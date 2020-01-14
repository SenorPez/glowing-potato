package com.senorpez.trident.api;

import java.util.Set;

class StarBuilder {
    private int id = 0;
    private String name = null;
    private float mass = 0;

    private Float semimajorAxis = null;
    private Float eccentricty = null;
    private Float inclination = null;
    private Float longitudeofAscendingNode = null;
    private Float argumentOfPeriapsis = null;
    private Float trueAnomalyAtEpoch = null;

    private Set<Planet> planets = null;

    StarBuilder() {
    }

    Star build() {
        return new Star(
                id,
                name,
                mass,
                semimajorAxis,
                eccentricty,
                inclination,
                longitudeofAscendingNode,
                argumentOfPeriapsis,
                trueAnomalyAtEpoch,
                planets);
    }

    StarBuilder setId(int id) {
        this.id = id;
        return this;
    }

    StarBuilder setName(String name) {
        this.name = name;
        return this;
    }

    StarBuilder setMass(float mass) {
        this.mass = mass;
        return this;
    }

    StarBuilder setSemimajorAxis(Float semimajorAxis) {
        this.semimajorAxis = semimajorAxis;
        return this;
    }

    StarBuilder setEccentricty(Float eccentricty) {
        this.eccentricty = eccentricty;
        return this;
    }

    StarBuilder setInclination(Float inclination) {
        this.inclination = inclination;
        return this;
    }

    StarBuilder setLongitudeofAscendingNode(Float longitudeofAscendingNode) {
        this.longitudeofAscendingNode = longitudeofAscendingNode;
        return this;
    }

    StarBuilder setArgumentOfPeriapsis(Float argumentOfPeriapsis) {
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        return this;
    }

    StarBuilder setTrueAnomalyAtEpoch(Float trueAnomalyAtEpoch) {
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;
        return this;
    }

    StarBuilder setPlanets(Set<Planet> planets) {
        this.planets = planets;
        return this;
    }
}

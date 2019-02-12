package com.senorpez.trident.api;

import java.util.Set;

class StarBuilder {
    private int id = 0;
    private String name = null;
    private float solarMass = 0;
    private Set<Planet> planets = null;

    StarBuilder() {
    }

    Star build() {
        return new Star(id, name, solarMass, planets);
    }

    StarBuilder setId(int id) {
        this.id = id;
        return this;
    }

    StarBuilder setName(String name) {
        this.name = name;
        return this;
    }

    StarBuilder setSolarMass(float solarMass) {
        this.solarMass = solarMass;
        return this;
    }

    StarBuilder setPlanets(Set<Planet> planets) {
        this.planets = planets;
        return this;
    }
}

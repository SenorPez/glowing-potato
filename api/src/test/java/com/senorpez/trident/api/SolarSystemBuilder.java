package com.senorpez.trident.api;

import java.util.Set;

class SolarSystemBuilder {
    private int id = 0;
    private String name = null;
    private Set<Star> stars = null;

    SolarSystemBuilder() {
    }

    SolarSystem build() {
        return new SolarSystem(id, name, stars);
    }

    SolarSystemBuilder setId(int id) {
        this.id = id;
        return this;
    }

    SolarSystemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    SolarSystemBuilder setStars(Set<Star> stars) {
        this.stars = stars;
        return this;
    }
}

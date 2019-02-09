package com.senorpez.trident.api;

class StarBuilder {
    private int id = 0;
    private String name = null;
    private float solarMass = 0;

    StarBuilder() {
    }

    Star build() {
        return new Star(id, name, solarMass);
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
}

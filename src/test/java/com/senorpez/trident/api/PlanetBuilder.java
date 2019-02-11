package com.senorpez.trident.api;

class PlanetBuilder {
    private int id;
    private String name;

    PlanetBuilder() {
    }

    Planet build() {
        return new Planet(id, name);
    }

    PlanetBuilder setId(int id) {
        this.id = id;
        return this;
    }

    PlanetBuilder setName(String name) {
        this.name = name;
        return this;
    }
}

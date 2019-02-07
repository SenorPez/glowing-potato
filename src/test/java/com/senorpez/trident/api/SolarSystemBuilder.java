package com.senorpez.trident.api;

class SolarSystemBuilder {
    private int id = 0;
    private String name = null;

    SolarSystemBuilder() {
    }

    SolarSystem build() {
        return new SolarSystem(id, name);
    }

    SolarSystemBuilder setId(int id) {
        this.id = id;
        return this;
    }

    SolarSystemBuilder setName(String name) {
        this.name = name;
        return this;
    }
}

package com.senorpez.trident.api;

public class SolarSystemBuilder {
    private int id = 0;
    private String name = null;

    public SolarSystemBuilder() {
    }

    SolarSystem build() {
        return new SolarSystem(id, name);
    }

    public int getId() {
        return id;
    }

    public SolarSystemBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SolarSystemBuilder setName(String name) {
        this.name = name;
        return this;
    }
}

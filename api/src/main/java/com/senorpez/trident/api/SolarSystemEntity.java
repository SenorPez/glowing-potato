package com.senorpez.trident.api;

class SolarSystemEntity implements APIEntity<Integer> {
    private final int id;
    private final String name;

    SolarSystemEntity(final SolarSystem solarSystem) {
        this.id = solarSystem.getId();
        this.name = solarSystem.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

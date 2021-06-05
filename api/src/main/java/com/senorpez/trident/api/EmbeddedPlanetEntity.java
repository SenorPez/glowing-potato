package com.senorpez.trident.api;

class EmbeddedPlanetEntity implements APIEntity<Integer> {
    private final int id;
    private final String name;

    EmbeddedPlanetEntity(final Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

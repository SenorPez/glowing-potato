package com.senorpez.trident.api;

class EmbeddedStarEntity implements APIEntity<Integer> {
    private final int id;
    private final String name;

    EmbeddedStarEntity(final Star star) {
        this.id = star.getId();
        this.name = star.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}



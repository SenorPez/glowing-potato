package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "star", collectionRelation = "star")
class StarModel implements Identifiable<Integer> {
    private final int id;
    private final String name;
    private final float mass;

    StarModel(final Star star) {
        this.id = star.getId();
        this.name = star.getName();
        this.mass = star.getMass();
    }

    StarResource toResource(final int solarSystemId) {
        final APIResourceAssembler<StarModel, StarResource> assembler = new APIResourceAssembler<>(
                StarController.class,
                StarResource.class,
                () -> new StarResource(this, solarSystemId));
        return assembler.toResource(this, solarSystemId);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getMass() {
        return mass;
    }
}

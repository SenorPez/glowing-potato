package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "system", collectionRelation = "system")
class SolarSystemModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    SolarSystemModel(final SolarSystem solarSystem) {
        this.id = solarSystem.getId();
        this.name = solarSystem.getName();
    }

    SolarSystemResource toResource() {
        final APIResourceAssembler<SolarSystemModel, SolarSystemResource> assembler = new APIResourceAssembler<>(SolarSystemController.class, SolarSystemResource.class, () -> new SolarSystemResource(this));
        return assembler.toResource(this);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

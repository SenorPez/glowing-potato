package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "planet", collectionRelation = "planet")
public class PlanetModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    PlanetModel(final Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
    }

    PlanetResource toResource(final int solarSystemId, final int starId) {
        final APIResourceAssembler<PlanetModel, PlanetResource> assembler = new APIResourceAssembler<>(
                PlanetController.class,
                PlanetResource.class,
                () -> new PlanetResource(this, solarSystemId, starId));
        return assembler.toResource(this, solarSystemId, starId);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

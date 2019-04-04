package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(value = "planet", collectionRelation = "planet")
class EmbeddedPlanetModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    EmbeddedPlanetModel(final Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
    }

    Resource<EmbeddedPlanetModel> toResource(final int solarSystemId, final int starId) {
        final APIEmbeddedResourceAssembler<EmbeddedPlanetModel, EmbeddedPlanetResource> assembler = new APIEmbeddedResourceAssembler<>(PlanetController.class, EmbeddedPlanetResource.class, () -> new EmbeddedPlanetResource(this, solarSystemId, starId));
        return assembler.toResource(this, solarSystemId, starId);
    }

    private class EmbeddedPlanetResource extends Resource<EmbeddedPlanetModel> {
        private EmbeddedPlanetResource(final EmbeddedPlanetModel content, final int solarSystemId, final int starId, final Link... links) {
            super(content, links);
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

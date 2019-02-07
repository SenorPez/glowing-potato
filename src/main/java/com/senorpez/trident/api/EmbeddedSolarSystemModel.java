package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(value = "system", collectionRelation = "system")
class EmbeddedSolarSystemModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    EmbeddedSolarSystemModel(final SolarSystem solarSystem) {
        this.id = solarSystem.getId();
        this.name = solarSystem.getName();
    }

    Resource<EmbeddedSolarSystemModel> toResource() {
        final APIEmbeddedResourceAssembler<EmbeddedSolarSystemModel, EmbeddedSolarSystemResource> assembler = new APIEmbeddedResourceAssembler<>(SolarSystemController.class, EmbeddedSolarSystemResource.class, () -> new EmbeddedSolarSystemResource(this));
        return assembler.toResource(this);
    }

    private class EmbeddedSolarSystemResource extends Resource<EmbeddedSolarSystemModel> {
        private EmbeddedSolarSystemResource(final EmbeddedSolarSystemModel content, final Link... links) {
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

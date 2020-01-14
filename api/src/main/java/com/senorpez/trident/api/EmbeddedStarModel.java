package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(value = "star", collectionRelation = "star")
class EmbeddedStarModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    EmbeddedStarModel(final Star star) {
        this.id = star.getId();
        this.name = star.getName();
    }

    Resource<EmbeddedStarModel> toResource(final int solarSystemId) {
        final APIEmbeddedResourceAssembler<EmbeddedStarModel, EmbeddedStarResource> assembler = new APIEmbeddedResourceAssembler<>(StarController.class, EmbeddedStarResource.class, () -> new EmbeddedStarResource(this, solarSystemId));
        return assembler.toResource(this, solarSystemId);
    }

    private class EmbeddedStarResource extends Resource<EmbeddedStarModel> {
        private EmbeddedStarResource(final EmbeddedStarModel content, final int solarSystemId, final Link... links) {
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



package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "star", collectionRelation = "star")
class StarModel extends RepresentationModel<StarModel> {
    StarModel(final StarEntity content, final int solarSystemId) {
        final APIResourceAssembler<StarEntity, StarModel> assembler = new APIResourceAssembler<>(
                StarController.class,
                StarModel.class,
                () -> new StarModel(content, solarSystemId)
        );
        assembler.toModel(content, solarSystemId);
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId)).withRel("stars"));
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, content.getId())).withRel("planets"));
    }

    StarModel(final EmbeddedStarEntity content, final int solarSystemId) {
        final APIResourceAssembler<EmbeddedStarEntity, StarModel> assembler = new APIResourceAssembler<>(
                StarController.class,
                StarModel.class,
                () -> new StarModel(content, solarSystemId));
        assembler.toModel(content, solarSystemId);
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId)).withRel("stars"));
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, content.getId())).withRel("planets"));
    }
}

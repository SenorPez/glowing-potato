package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "planet", collectionRelation = "planet")
class PlanetModel extends RepresentationModel<PlanetModel> {
    PlanetModel(final PlanetEntity content, final int solarSystemId, final int starId) {
        final APIResourceAssembler<PlanetEntity, PlanetModel> assembler = new APIResourceAssembler<>(
                PlanetController.class,
                PlanetModel.class,
                () -> new PlanetModel(content, solarSystemId, starId)
        );
        assembler.toModel(content, solarSystemId, starId);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withRel("planets"));
    }

    PlanetModel(final EmbeddedPlanetEntity content, final int solarSystemId, final int starId) {
        final APIResourceAssembler<EmbeddedPlanetEntity, PlanetModel> assembler = new APIResourceAssembler<>(
                PlanetController.class,
                PlanetModel.class,
                () -> new PlanetModel(content, solarSystemId, starId)
        );
        assembler.toModel(content, solarSystemId, starId);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withRel("planets"));
    }
}

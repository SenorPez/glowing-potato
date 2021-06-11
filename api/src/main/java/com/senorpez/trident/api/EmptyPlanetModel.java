package com.senorpez.trident.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.Relation;

import java.util.Collection;

import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "planet", collectionRelation = "planet")
class EmptyPlanetModel extends RepresentationModel<EmptyPlanetModel> {
    EmptyPlanetModel(Collection<EmbeddedWrapper> entities, final int solarSystemId, final int starId) {
        CollectionModel.of(entities);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withSelfRel());
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId, starId)).withRel("star"));
        this.add(linkTo(methodOn(RootController.class).root()).withRel(INDEX));
    }
}

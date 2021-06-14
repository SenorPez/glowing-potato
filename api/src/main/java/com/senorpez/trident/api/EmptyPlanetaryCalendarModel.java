package com.senorpez.trident.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.Relation;

import java.util.Collection;

import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "calendar", collectionRelation = "calendar")
class EmptyPlanetaryCalendarModel extends RepresentationModel<EmbeddedPlanetaryCalendarModel> {
    public EmptyPlanetaryCalendarModel(Collection<EmbeddedWrapper> entities, final int solarSystemId, final int starId, final int planetId) {
        CollectionModel.of(entities);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId, planetId)).withRel("planet"));
        this.add(linkTo(methodOn(RootController.class).root()).withRel(INDEX));
    }
}

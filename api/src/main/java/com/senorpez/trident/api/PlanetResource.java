package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class PlanetResource extends Resource<PlanetModel> {
    PlanetResource(final PlanetModel content, final int solarSystemId, final int starId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withRel("planets"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, content.getId())).withRel("calendars"));
    }
}

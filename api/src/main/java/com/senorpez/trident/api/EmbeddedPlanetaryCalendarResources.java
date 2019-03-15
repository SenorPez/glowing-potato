package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmbeddedPlanetaryCalendarResources extends APIResources<EmbeddedPlanetaryCalendarModel> {
    EmbeddedPlanetaryCalendarResources(Iterable<Resource<EmbeddedPlanetaryCalendarModel>> content, final int solarSystemId, final int starId, final int planetId, Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withSelfRel());
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId, planetId)).withRel("planet"));
    }
}

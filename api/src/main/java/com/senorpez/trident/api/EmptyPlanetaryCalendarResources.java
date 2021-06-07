package com.senorpez.trident.api;

import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class EmptyPlanetaryCalendarResources extends EmptyAPIResources {
    EmptyPlanetaryCalendarResources(Iterable<Object> content, int solarSystemId, int starId, int planetId, Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withSelfRel());
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId, planetId)).withRel("planet"));
    }
}

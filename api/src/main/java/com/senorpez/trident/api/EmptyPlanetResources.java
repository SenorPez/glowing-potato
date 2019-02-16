package com.senorpez.trident.api;

import org.springframework.hateoas.Link;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmptyPlanetResources extends EmptyAPIResources {
    EmptyPlanetResources(Iterable<Object> content, int solarSystemId, int starId, Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withSelfRel());
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId, starId)).withRel("star"));
    }
}

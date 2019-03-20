package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmbeddedPlanetResources extends APIResources<EmbeddedPlanetModel> {
    EmbeddedPlanetResources(final Iterable<Resource<EmbeddedPlanetModel>> content, final int solarSystemId, final int starId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withSelfRel());
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId, starId)).withRel("star"));
    }
}

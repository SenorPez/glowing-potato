package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmbeddedStarResources extends APIResources<EmbeddedStarModel> {
    EmbeddedStarResources(final Iterable<Resource<EmbeddedStarModel>> content, final int solarSystemId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId)).withSelfRel());
        this.add(linkTo(methodOn(SolarSystemController.class).solarSystems(solarSystemId)).withRel("system"));
    }
}

package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmbeddedSolarSystemResources extends APIResources<EmbeddedSolarSystemModel> {
    EmbeddedSolarSystemResources(final Iterable<Resource<EmbeddedSolarSystemModel>> content, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withSelfRel());
    }
}

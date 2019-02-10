package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class SolarSystemResource extends Resource<SolarSystemModel> {
    SolarSystemResource(final SolarSystemModel content, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel("systems"));
        this.add(linkTo(methodOn(StarController.class).stars(content.getId())).withRel("stars"));
    }
}

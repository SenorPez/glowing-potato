package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class StarResource extends Resource<StarModel> {
    StarResource(final StarModel content, final int solarSystemId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(StarController.class).stars(solarSystemId)).withRel("stars"));
    }
}

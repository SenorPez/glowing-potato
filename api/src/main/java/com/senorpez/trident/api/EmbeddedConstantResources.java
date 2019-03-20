package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmbeddedConstantResources extends APIResources<EmbeddedConstantModel> {
    EmbeddedConstantResources(final Iterable<Resource<EmbeddedConstantModel>> content, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(ConstantController.class).constants()).withSelfRel());
    }
}

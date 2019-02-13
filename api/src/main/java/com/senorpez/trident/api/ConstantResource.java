package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class ConstantResource extends Resource<ConstantModel> {
    ConstantResource(final ConstantModel content, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(ConstantController.class).constants()).withRel("constants"));
    }
}

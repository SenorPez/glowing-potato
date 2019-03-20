package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class EmptyAPIResources extends Resources<Object> {
    EmptyAPIResources(Iterable<Object> content, Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
    }
}

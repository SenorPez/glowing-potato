package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import java.io.Serializable;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class APIResources<M extends Identifiable<? extends Serializable>> extends Resources<Resource<M>> {
    APIResources(final Iterable<Resource<M>> content, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
    }
}

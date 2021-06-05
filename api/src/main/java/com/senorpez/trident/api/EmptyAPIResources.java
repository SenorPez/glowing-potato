package com.senorpez.trident.api;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class EmptyAPIResources extends CollectionModel<Object> {
    EmptyAPIResources(Iterable<Object> content, Link... links) {
        CollectionModel.of(content, links);
        this.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
    }
}

package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class SolarSystemResource extends Resource<SolarSystemModel> {
    SolarSystemResource(final SolarSystemModel content, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel("systems"));
        this.add(linkTo(methodOn(StarController.class).stars(content.getId())).withRel("stars"));
    }

    static Resources<SolarSystemResource> makeResources(final Collection<SolarSystemResource> resources) {
        resources.forEach(ResourceSupport::removeLinks);
        resources.forEach(solarSystemResource -> solarSystemResource.add(
                linkTo(methodOn(SolarSystemController.class).solarSystems(solarSystemResource.getContent().getId())).withSelfRel()));
        final Resources<SolarSystemResource> solarSystemResources = new Resources<>(resources);
        solarSystemResources.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withSelfRel());
        solarSystemResources.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return solarSystemResources;
    }
}

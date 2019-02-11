package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class PlanetResource extends Resource<PlanetModel> {
    PlanetResource(final PlanetModel content, final int solarSystemId, final int starId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withRel("planets"));
    }

    static Resources<PlanetResource> makeResources(
            final Collection<PlanetResource> resources,
            final int solarSystemId,
            final int starId) {
        resources.forEach(ResourceSupport::removeLinks);
        resources.forEach(planetResource -> planetResource.add(
                linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId, planetResource.getContent().getId())).withSelfRel()));
        final Resources<PlanetResource> planetResources = new Resources<>(resources);
        planetResources.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withSelfRel());
        planetResources.add(linkTo(methodOn(StarController.class).stars(solarSystemId, starId)).withRel("star"));
        planetResources.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return planetResources;
    }
}

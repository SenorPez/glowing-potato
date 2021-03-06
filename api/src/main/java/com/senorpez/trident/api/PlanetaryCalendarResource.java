package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class PlanetaryCalendarResource extends Resource<PlanetaryCalendarModel> {
    PlanetaryCalendarResource(final PlanetaryCalendarModel content, final int solarSystemId, final int starId, final int planetId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withRel("calendars"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).currentCalendar(solarSystemId, starId, planetId, content.getId())).withRel("currentTime"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).festivalYear(solarSystemId, starId, planetId, content.getId(), null)).withRel("festivalYear"));
    }
}

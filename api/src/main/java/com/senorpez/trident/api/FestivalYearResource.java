package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class FestivalYearResource extends Resource<FestivalYearModel> {
    FestivalYearResource(final FestivalYearModel content, final int solarSystemId, final int starId, final int planetId, final int calendarId, final Link... links) {
        super(content, links);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId, calendarId)).withRel("calendar"));
    }
}

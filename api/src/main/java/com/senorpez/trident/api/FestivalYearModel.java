package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class FestivalYearModel extends RepresentationModel<FestivalYearModel> {
    FestivalYearModel(final FestivalYearEntity content, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        final APIResourceAssembler<FestivalYearEntity, FestivalYearModel> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                FestivalYearModel.class,
                () -> new FestivalYearModel(content, solarSystemId, starId, planetId, calendarId)
        );
        assembler.toModel(content, solarSystemId, starId, planetId, calendarId);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).festivalYear(solarSystemId, starId, planetId, calendarId, content.getId())).withSelfRel());
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId, calendarId)).withRel("calendar"));
    }
}

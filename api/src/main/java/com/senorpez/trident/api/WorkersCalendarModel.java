package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class WorkersCalendarModel extends RepresentationModel<WorkersCalendarModel> {
    WorkersCalendarModel(final WorkersCalendarEntity content, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        final APIResourceAssembler<WorkersCalendarEntity, WorkersCalendarModel> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                WorkersCalendarModel.class,
                () -> new WorkersCalendarModel(content, solarSystemId, starId, planetId, calendarId)
        );
        assembler.toModel(content, solarSystemId, starId, planetId, calendarId);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).currentCalendar(solarSystemId, starId, planetId, calendarId)).withSelfRel());
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId, calendarId)).withRel("calendar"));
    }
}

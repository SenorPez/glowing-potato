package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "calendar", collectionRelation = "calendar")
class PlanetaryCalendarModel extends RepresentationModel<PlanetaryCalendarModel> {
    PlanetaryCalendarModel(final PlanetaryCalendarEntity content, final int solarSystemId, final int starId, final int planetId) {
        final APIResourceAssembler<PlanetaryCalendarEntity, PlanetaryCalendarModel> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                PlanetaryCalendarModel.class,
                () -> new PlanetaryCalendarModel(content, solarSystemId, starId, planetId)
        );
        assembler.toModel(content, solarSystemId, starId, planetId);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withRel("calendars"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).currentCalendar(solarSystemId, starId, planetId, content.getId())).withRel("currentTime"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).festivalYear(solarSystemId, starId, planetId, content.getId(), null)).withRel("festivalYear"));
    }

    PlanetaryCalendarModel(final EmbeddedPlanetaryCalendarEntity content, final int solarSystemId, final int starId, final int planetId) {
        final APIResourceAssembler<EmbeddedPlanetaryCalendarEntity, PlanetaryCalendarModel> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                PlanetaryCalendarModel.class,
                () -> new PlanetaryCalendarModel(content, solarSystemId, starId, planetId)
        );
        assembler.toModel(content, solarSystemId, starId, planetId);
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withRel("calendars"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).currentCalendar(solarSystemId, starId, planetId, content.getId())).withRel("currentTime"));
        this.add(linkTo(methodOn(PlanetaryCalendarController.class).festivalYear(solarSystemId, starId, planetId, content.getId(), null)).withRel("festivalYear"));
    }
}

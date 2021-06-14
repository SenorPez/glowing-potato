package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.APIService.findCalendar;
import static com.senorpez.trident.api.APIService.findPlanet;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars/{starId}/planets/{planetId}/calendars",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_VALUE}
)
@RestController
class PlanetaryCalendarController {
    private final APIService apiService;
    private final Collection<SolarSystem> solarSystems;

    @Autowired
    public PlanetaryCalendarController(APIService apiService) {
        this.apiService = apiService;
        this.solarSystems = Application.SOLAR_SYSTEMS;
    }

    @RequestMapping
    ResponseEntity<CollectionModel<?>> calendars(@PathVariable final int solarSystemId, @PathVariable final int starId, @PathVariable final int planetId) {
        final Planet planet = findPlanet(apiService, solarSystems, solarSystemId, starId, planetId);
        if (planet.getCalendars() == null) {
            EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
            EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(EmptyPlanetaryCalendarModel.class);
            CollectionModel<EmbeddedWrapper> resources = CollectionModel.of(Collections.singletonList(wrapper));
            resources.add(linkTo(methodOn(RootController.class).root()).withRel(INDEX));
            resources.add(linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withSelfRel());
            resources.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId, planetId)).withRel("planet"));
            return ResponseEntity.ok(resources);
        } else {
            final Collection<PlanetaryCalendar> calendars = planet.getCalendars();
            final Collection<PlanetaryCalendarEntity> planetaryCalendarEntities = calendars
                    .stream()
                    .map(PlanetaryCalendarEntity::new)
                    .collect(Collectors.toList());
            final Collection<RepresentationModel<EmbeddedPlanetaryCalendarModel>> calendarModels = planetaryCalendarEntities
                    .stream()
                    .map(entity -> EmbeddedPlanetaryCalendarModel.toModel(entity, solarSystemId, starId, planetId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(CollectionModel.of(
                    calendarModels,
                    linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withSelfRel(),
                    linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId, planetId)).withRel("planet"),
                    linkTo(methodOn(RootController.class).root()).withRel(INDEX)));
        }
    }

    @RequestMapping("/{calendarId}")
    ResponseEntity<RepresentationModel<PlanetaryCalendarModel>> calendars(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId) {
        final PlanetaryCalendar calendar = findCalendar(apiService, solarSystems, solarSystemId, starId, planetId, calendarId);
        final PlanetaryCalendarEntity calendarEntity = new PlanetaryCalendarEntity(calendar);
        final RepresentationModel<PlanetaryCalendarModel> calendarModel = PlanetaryCalendarModel.toModel(calendarEntity, solarSystemId, starId, planetId, calendarId);
        calendarModel.add(
                linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withRel("calendars"),
                linkTo(methodOn(PlanetaryCalendarController.class).currentCalendar(solarSystemId, starId, planetId, calendarId)).withRel("currentTime"),
                linkTo(methodOn(PlanetaryCalendarController.class).festivalYear(solarSystemId, starId, planetId, calendarId, 1234)).withRel("festivalYear"),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)
        );
        return ResponseEntity.ok(calendarModel);
    }

    @RequestMapping("/{calendarId}/currentTime")
    ResponseEntity<RepresentationModel<WorkersCalendarModel>> currentCalendar(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId) {
        final PlanetaryCalendar calendar = findCalendar(apiService, solarSystems, solarSystemId, starId, planetId, calendarId);
        final WorkersCalendarEntity workersCalendarEntity = new WorkersCalendarEntity(calendar);
        final RepresentationModel<WorkersCalendarModel> calendarModel = WorkersCalendarModel.toModel(workersCalendarEntity, solarSystemId, starId, planetId, calendarId);
        calendarModel.add(
                linkTo(methodOn(RootController.class).root()).withRel(INDEX),
                linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId, calendarId)).withRel("calendar")
        );
        return ResponseEntity.ok(calendarModel);
    }

    @RequestMapping(value = "/{calendarId}/festivalYear/{localYear}")
    ResponseEntity<RepresentationModel<FestivalYearModel>> festivalYear(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId,
            @PathVariable final Integer localYear) {
        final PlanetaryCalendar calendar = findCalendar(apiService, solarSystems, solarSystemId, starId, planetId, calendarId);
        final FestivalYearEntity festivalYearEntity = new FestivalYearEntity(calendar, localYear);
        final RepresentationModel<FestivalYearModel> calendarModel = FestivalYearModel.toModel(festivalYearEntity, solarSystemId, starId, planetId, calendarId, localYear);
        calendarModel.add(
                linkTo(methodOn(RootController.class).root()).withRel(INDEX),
                linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId, calendarId)).withRel("calendar")
        );
        return ResponseEntity.ok(calendarModel);
    }
}

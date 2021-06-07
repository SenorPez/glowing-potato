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
    ResponseEntity<? extends RepresentationModel<?>> calendars(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId) {
        final Planet planet = findPlanet(apiService, solarSystems, solarSystemId, starId, planetId);
        if (planet.getCalendars() == null) {
            EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
            EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(PlanetaryCalendarEntity.class);
            return ResponseEntity.ok(new EmptyPlanetaryCalendarResources(Collections.singletonList(wrapper), solarSystemId, starId, planetId));
        } else {
            final Collection<PlanetaryCalendar> calendars = planet.getCalendars();
            final CollectionModel<PlanetaryCalendarModel> calendarModels = CollectionModel.of(calendars
                    .stream()
                    .map(EmbeddedPlanetaryCalendarEntity::new)
                    .map(embeddedPlanetaryCalendarEntity -> new PlanetaryCalendarModel(embeddedPlanetaryCalendarEntity, solarSystemId, starId, planetId))
                    .collect(Collectors.toList())
            );
            return ResponseEntity.ok(calendarModels);
        }
    }


    @RequestMapping("/{calendarId}")
    ResponseEntity<PlanetaryCalendarModel> calendars(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId) {
        final PlanetaryCalendar calendar = findCalendar(apiService, solarSystems, solarSystemId, starId, planetId, calendarId);
        final PlanetaryCalendarEntity planetaryCalendarEntity = new PlanetaryCalendarEntity(calendar);
        final PlanetaryCalendarModel planetaryCalendarModel = new PlanetaryCalendarModel(planetaryCalendarEntity, solarSystemId, starId, planetId);
        return ResponseEntity.ok(planetaryCalendarModel);
    }

    @RequestMapping("/{calendarId}/currentTime")
    ResponseEntity<WorkersCalendarModel> currentCalendar(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId) {
        final PlanetaryCalendar calendar = findCalendar(apiService, solarSystems, solarSystemId, starId, planetId, calendarId);
        final WorkersCalendarEntity workersCalendarEntity = new WorkersCalendarEntity(calendar);
        final WorkersCalendarModel workersCalendarModel = new WorkersCalendarModel(workersCalendarEntity, solarSystemId, starId, planetId, calendarId);
        return ResponseEntity.ok(workersCalendarModel);
    }

    @RequestMapping(value = "/{calendarId}/festivalYear/{localYear}")
    ResponseEntity<FestivalYearModel> festivalYear(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId,
            @PathVariable final Integer localYear) {
        final PlanetaryCalendar calendar = findCalendar(apiService, solarSystems, solarSystemId, starId, planetId, calendarId);
        final FestivalYearEntity festivalYearEntity = new FestivalYearEntity(calendar, localYear);
        final FestivalYearModel festivalYearModel = new FestivalYearModel(festivalYearEntity, solarSystemId, starId, planetId, calendarId);
        return ResponseEntity.ok(festivalYearModel);
    }
}

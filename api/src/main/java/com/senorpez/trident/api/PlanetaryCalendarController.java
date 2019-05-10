package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars/{starId}/planets/{planetId}/calendars",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_UTF8_VALUE}
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
    ResponseEntity<? extends Resources> calendars(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Star star = apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId));
        final Planet planet = apiService.findOne(
                star.getPlanets(),
                findPlanet -> findPlanet.getId() == planetId,
                () -> new PlanetNotFoundException(planetId));
        if (planet.getCalendars() == null) {
            EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
            EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(PlanetaryCalendarModel.class);
            return ResponseEntity.ok(new EmptyPlanetaryCalendarResources(Collections.singletonList(wrapper), solarSystemId, starId, planetId));
        } else {
            final Collection<PlanetaryCalendar> calendars = planet.getCalendars();
            final Collection<EmbeddedPlanetaryCalendarModel> calendarModels = calendars.stream()
                    .map(EmbeddedPlanetaryCalendarModel::new)
                    .collect(Collectors.toList());
            final Collection<Resource<EmbeddedPlanetaryCalendarModel>> calendarResources = calendarModels.stream()
                    .map(calendarModel -> calendarModel.toResource(solarSystemId, starId, planetId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new EmbeddedPlanetaryCalendarResources(calendarResources, solarSystemId, starId, planetId));
        }
    }

    @RequestMapping("/{calendarId}")
    ResponseEntity<PlanetaryCalendarResource> calendars(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Star star = apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId));
        final Planet planet = apiService.findOne(
                star.getPlanets(),
                findPlanet -> findPlanet.getId() == planetId,
                () -> new PlanetNotFoundException(planetId));
        final PlanetaryCalendar calendar = apiService.findOne(
                planet.getCalendars(),
                findCalendar -> findCalendar.getId() == calendarId,
                () -> new PlanetaryCalendarNotFoundException(calendarId));
        final PlanetaryCalendarModel calendarModel = new PlanetaryCalendarModel(calendar);
        final PlanetaryCalendarResource calendarResource = calendarModel.toResource(solarSystemId, starId, planetId);
        return ResponseEntity.ok(calendarResource);
    }

    @RequestMapping("/{calendarId}/currentTime")
    ResponseEntity<WorkersCalendarResource> currentCalendar(
            @PathVariable final int solarSystemId,
            @PathVariable final int starId,
            @PathVariable final int planetId,
            @PathVariable final int calendarId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Star star = apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId));
        final Planet planet = apiService.findOne(
                star.getPlanets(),
                findPlanet -> findPlanet.getId() == planetId,
                () -> new PlanetNotFoundException(planetId));
        final PlanetaryCalendar calendar = apiService.findOne(
                planet.getCalendars(),
                findCalendar -> findCalendar.getId() == calendarId,
                () -> new PlanetaryCalendarNotFoundException(calendarId));
        final WorkersCalendarModel workersCalendarModel = new WorkersCalendarModel(calendar);
        final WorkersCalendarResource workersCalendarResource = workersCalendarModel.toResource(solarSystemId, starId, planetId, calendarId);
        return ResponseEntity.ok(workersCalendarResource);
    }
}
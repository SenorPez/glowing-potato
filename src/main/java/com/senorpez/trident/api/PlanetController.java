package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars/{starId}/planets",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_UTF8_VALUE}
)
@RestController
class PlanetController {
    private final APIService apiService;
    private final Collection<SolarSystem> solarSystems;

    @Autowired
    PlanetController(final APIService apiService) {
        this.apiService = apiService;
        this.solarSystems = Application.SOLAR_SYSTEMS;
    }

    PlanetController(final APIService apiService, final Collection<SolarSystem> solarSystems) {
        this.apiService = apiService;
        this.solarSystems = solarSystems;
    }

    @RequestMapping
    ResponseEntity<Resources<PlanetResource>> planets(@PathVariable final int solarSystemId, @PathVariable final int starId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Star star = apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId));
        final Collection<Planet> planets = star.getPlanets();
        final Collection<PlanetModel> planetModels = planets.stream()
                .map(PlanetModel::new)
                .collect(Collectors.toList());
        final Collection<PlanetResource> planetResources = planetModels.stream()
                .map(planetModel -> planetModel.toResource(solarSystemId, starId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(PlanetResource.makeResources(planetResources, solarSystemId, starId));
    }

    @RequestMapping("/{planetId}")
    ResponseEntity<PlanetResource> planets(@PathVariable final int solarSystemId, @PathVariable final int starId, @PathVariable final int planetId) {
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
        final PlanetModel planetModel = new PlanetModel(planet);
        final PlanetResource planetResource = planetModel.toResource(solarSystemId, starId);
        return ResponseEntity.ok(planetResource);
    }
}

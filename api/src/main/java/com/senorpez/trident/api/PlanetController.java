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

    @RequestMapping
    ResponseEntity<? extends Resources> planets(@PathVariable final int solarSystemId, @PathVariable final int starId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Star star = apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId));
        if (star.getPlanets() == null) {
            EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
            EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(PlanetModel.class);
            return ResponseEntity.ok(new EmptyPlanetResources(Collections.singletonList(wrapper), solarSystemId, starId));
        } else {
            final Collection<Planet> planets = star.getPlanets();
            final Collection<EmbeddedPlanetModel> planetModels = planets.stream()
                    .map(EmbeddedPlanetModel::new)
                    .collect(Collectors.toList());
            final Collection<Resource<EmbeddedPlanetModel>> planetResources = planetModels.stream()
                    .map(planetModel -> planetModel.toResource(solarSystemId, starId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new EmbeddedPlanetResources(planetResources, solarSystemId, starId));
        }
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

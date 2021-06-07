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

import static com.senorpez.trident.api.APIService.findPlanet;
import static com.senorpez.trident.api.APIService.findStar;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars/{starId}/planets",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_VALUE}
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
    ResponseEntity<? extends RepresentationModel<?>> planets(@PathVariable final int solarSystemId, @PathVariable final int starId) {
        final Star star = findStar(apiService, solarSystems, solarSystemId, starId);
        if (star.getPlanets() == null) {
            EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
            EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(PlanetEntity.class);
            return ResponseEntity.ok(CollectionModel.of(new EmptyPlanetResources(Collections.singletonList(wrapper), solarSystemId, starId)));
        } else {
            final Collection<Planet> planets = star.getPlanets();
            final CollectionModel<PlanetModel> planetModels = CollectionModel.of(planets
                    .stream()
                    .map(EmbeddedPlanetEntity::new)
                    .map(embeddedPlanetEntity -> new PlanetModel(embeddedPlanetEntity, solarSystemId, starId))
                    .collect(Collectors.toList())
            );
            return ResponseEntity.ok(planetModels);
        }
    }

    @RequestMapping("/{planetId}")
    ResponseEntity<PlanetModel> planets(@PathVariable final int solarSystemId, @PathVariable final int starId, @PathVariable final int planetId) {
        final Planet planet = findPlanet(apiService, solarSystems, solarSystemId, starId, planetId);
        final PlanetEntity planetEntity = new PlanetEntity(planet);
        final PlanetModel planetModel = new PlanetModel(planetEntity, solarSystemId, starId);
        return ResponseEntity.ok(planetModel);
    }
}

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
import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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
    ResponseEntity<CollectionModel<?>> planets(@PathVariable final int solarSystemId, @PathVariable final int starId) {
        final Star star = findStar(apiService, solarSystems, solarSystemId, starId);
        if (star.getPlanets() == null) {
            EmbeddedWrappers wrappers = new EmbeddedWrappers(false);
            EmbeddedWrapper wrapper = wrappers.emptyCollectionOf(EmptyPlanetModel.class);
            CollectionModel<EmbeddedWrapper> resources = CollectionModel.of(Collections.singletonList(wrapper));
            resources.add(linkTo(methodOn(RootController.class).root()).withRel(INDEX));
            resources.add(linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withSelfRel());
            resources.add(linkTo(methodOn(StarController.class).stars(solarSystemId, starId)).withRel("star"));
            return ResponseEntity.ok(resources);
        } else {
            final Collection<Planet> planets = star.getPlanets();
            final Collection<PlanetEntity> planetEntities = planets
                    .stream()
                    .map(PlanetEntity::new)
                    .collect(Collectors.toList());
            final Collection<RepresentationModel<EmbeddedPlanetModel>> planetModels = planetEntities
                    .stream()
                    .map(entity -> EmbeddedPlanetModel.toModel(entity, solarSystemId, starId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(CollectionModel.of(
                    planetModels,
                    linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withSelfRel(),
                    linkTo(methodOn(StarController.class).stars(solarSystemId, starId)).withRel("star"),
                    linkTo(methodOn(RootController.class).root()).withRel(INDEX)));
        }
    }

    @RequestMapping("/{planetId}")
    ResponseEntity<RepresentationModel<PlanetModel>> planets(@PathVariable final int solarSystemId, @PathVariable final int starId, @PathVariable final int planetId) {
        final Planet planet = findPlanet(apiService, solarSystems, solarSystemId, starId, planetId);
        final PlanetEntity planetEntity = new PlanetEntity(planet);
        final RepresentationModel<PlanetModel> planetModel = PlanetModel.toModel(planetEntity, solarSystemId, starId);
        planetModel.add(
                linkTo(methodOn(PlanetaryCalendarController.class).calendars(solarSystemId, starId, planetId)).withRel("calendars"),
                linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withRel("planets"),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)
        );
        return ResponseEntity.ok(planetModel);
    }
}

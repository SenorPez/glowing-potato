package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.APIService.findSolarSystem;
import static com.senorpez.trident.api.APIService.findStar;
import static com.senorpez.trident.api.SupportedMediaTypes.FALLBACK_VALUE;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, FALLBACK_VALUE}
)
@RestController
class StarController {
    private final APIService apiService;
    private final Collection<SolarSystem> solarSystems;

    @Autowired
    StarController(final APIService apiService) {
        this.apiService = apiService;
        this.solarSystems = Application.SOLAR_SYSTEMS;
    }

    @RequestMapping
    ResponseEntity<CollectionModel<RepresentationModel<EmbeddedStarModel>>> stars(@PathVariable final int solarSystemId) {
        final SolarSystem solarSystem = findSolarSystem(apiService, solarSystems, solarSystemId);
        final Collection<Star> stars = solarSystem.getStars();
        final Collection<StarEntity> starEntities = stars
                .stream()
                .map(StarEntity::new)
                .collect(Collectors.toList());
        final Collection<RepresentationModel<EmbeddedStarModel>> starModels = starEntities
                .stream()
                .map(entity -> EmbeddedStarModel.toModel(entity, solarSystemId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(
                starModels,
                linkTo(methodOn(StarController.class).stars(solarSystemId)).withSelfRel(),
                linkTo(methodOn(SolarSystemController.class).solarSystems(solarSystemId)).withRel("system"),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)));
    }

    @RequestMapping("/{starId}")
    ResponseEntity<RepresentationModel<StarModel>> stars(@PathVariable final int solarSystemId, @PathVariable final int starId) {
        final Star star = findStar(apiService, solarSystems, solarSystemId, starId);
        final StarEntity starEntity = new StarEntity(star);
        final RepresentationModel<StarModel> starModel = StarModel.toModel(starEntity, solarSystemId);
        starModel.add(
                linkTo(methodOn(PlanetController.class).planets(solarSystemId, starId)).withRel("planets"),
                linkTo(methodOn(StarController.class).stars(solarSystemId)).withRel("stars"),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)
        );
        return ResponseEntity.ok(starModel);
    }
}

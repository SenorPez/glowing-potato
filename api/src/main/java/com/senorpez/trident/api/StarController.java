package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        value = "/systems/{solarSystemId}/stars",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_VALUE}
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

    StarController(final APIService apiService, final Collection<SolarSystem> solarSystems) {
        this.apiService = apiService;
        this.solarSystems = solarSystems;
    }

    @RequestMapping
    ResponseEntity<CollectionModel<StarModel>> stars(@PathVariable final int solarSystemId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Collection<Star> stars = solarSystem.getStars();
        final CollectionModel<StarModel> starModels = CollectionModel.of(stars
                .stream()
                .map(EmbeddedStarEntity::new)
                .map(embeddedStarEntity -> new StarModel(embeddedStarEntity, solarSystemId))
                .collect(Collectors.toList())
        );
        return ResponseEntity.ok(starModels);
    }

    @RequestMapping("/{starId}")
    ResponseEntity<StarModel> stars(@PathVariable final int solarSystemId, @PathVariable final int starId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final Star star = apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId));
        final StarEntity starEntity = new StarEntity(star);
        final StarModel starModel = new StarModel(starEntity, solarSystemId);
        return ResponseEntity.ok(starModel);
    }
}

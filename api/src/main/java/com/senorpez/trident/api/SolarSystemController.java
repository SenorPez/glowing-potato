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

import static com.senorpez.trident.api.APIService.findSolarSystem;
import static com.senorpez.trident.api.Application.SOLAR_SYSTEMS;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        value = "/systems",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_VALUE}
)
@RestController
class SolarSystemController {
    private final APIService apiService;
    private final Collection<SolarSystem> solarSystems;

    @Autowired
    SolarSystemController(final APIService apiService) {
        this.solarSystems = SOLAR_SYSTEMS;
        this.apiService = apiService;
    }

    SolarSystemController(final APIService apiService, final Collection<SolarSystem> solarSystems) {
        this.solarSystems = solarSystems;
        this.apiService = apiService;
    }

    @RequestMapping
    ResponseEntity<CollectionModel<SolarSystemModel>> solarSystems() {
        final Collection<SolarSystem> solarSystems = apiService.findAll(this.solarSystems);
        final CollectionModel<SolarSystemModel> solarSystemModels = CollectionModel.of(solarSystems
                .stream()
                .map(SolarSystemEntity::new)
                .map(SolarSystemModel::new)
                .collect(Collectors.toList())
        );
        return ResponseEntity.ok(solarSystemModels);
    }

    @RequestMapping("/{solarSystemId}")
    ResponseEntity<SolarSystemModel> solarSystems(@PathVariable final int solarSystemId) {
        final SolarSystem solarSystem = findSolarSystem(apiService, solarSystems, solarSystemId);
        final SolarSystemEntity solarSystemEntity = new SolarSystemEntity(solarSystem);
        final SolarSystemModel solarSystemModel = new SolarSystemModel(solarSystemEntity);
        return ResponseEntity.ok(solarSystemModel);
    }


}

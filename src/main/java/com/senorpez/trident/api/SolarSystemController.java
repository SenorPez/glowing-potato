package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.Application.SOLAR_SYSTEMS;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RequestMapping(
        value = "/systems",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_UTF8_VALUE}
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
    ResponseEntity<EmbeddedSolarSystemResources> solarSystems() {
        final Collection<SolarSystem> solarSystems = apiService.findAll(this.solarSystems);
        final Collection<EmbeddedSolarSystemModel> solarSystemModels = solarSystems.stream()
                .map(EmbeddedSolarSystemModel::new)
                .collect(Collectors.toList());
        final Collection<Resource<EmbeddedSolarSystemModel>> solarSystemResources = solarSystemModels.stream()
                .map(EmbeddedSolarSystemModel::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new EmbeddedSolarSystemResources(solarSystemResources));
    }

    @RequestMapping("/{solarSystemId}")
    ResponseEntity<SolarSystemResource> solarSystems(@PathVariable final int solarSystemId) {
        final SolarSystem solarSystem = apiService.findOne(
                this.solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId));
        final SolarSystemModel solarSystemModel = new SolarSystemModel(solarSystem);
        final SolarSystemResource solarSystemResource = solarSystemModel.toResource();
        return ResponseEntity.ok(solarSystemResource);
    }
}

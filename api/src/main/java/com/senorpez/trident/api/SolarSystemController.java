package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.APIService.findSolarSystem;
import static com.senorpez.trident.api.Application.SOLAR_SYSTEMS;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        value = "/systems",
        method = RequestMethod.GET,
        produces = {APPLICATION_JSON_VALUE}
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
    RepresentationModel<CollectionModel<SolarSystemModel>> solarSystems() {
        final Collection<RepresentationModel<SolarSystemModel>> solarSystemModels = apiService
                .findAll(this.solarSystems)
                .stream()
                .map(SolarSystemEntity::new)
                .map(SolarSystemModel::toModel)
                .collect(Collectors.toList());
        return HalModelBuilder
                .emptyHalModel()
                .embed(solarSystemModels)
                .link(linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel(IanaLinkRelations.SELF))
                .link(linkTo(methodOn(RootController.class).root()).withRel("index"))
                .build();
    }

//    @RequestMapping("/{solarSystemId}")
//    ResponseEntity<SolarSystemModel> solarSystems(@PathVariable final int solarSystemId) {
//        final SolarSystem solarSystem = findSolarSystem(apiService, solarSystems, solarSystemId);
//        final SolarSystemEntity solarSystemEntity = new SolarSystemEntity(solarSystem);
//        final SolarSystemModel solarSystemModel = new SolarSystemModel(solarSystemEntity);
//        return ResponseEntity.ok(solarSystemModel);
//    }
}

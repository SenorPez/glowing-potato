package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.APIService.findSolarSystem;
import static com.senorpez.trident.api.Application.SOLAR_SYSTEMS;
import static com.senorpez.trident.api.SupportedMediaTypes.FALLBACK_VALUE;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(
        value = "/systems",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, FALLBACK_VALUE}
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
    ResponseEntity<CollectionModel<RepresentationModel<SolarSystemModel>>> solarSystems() {
        final Collection<SolarSystem> solarSystems = apiService.findAll(this.solarSystems);
        final Collection<SolarSystemEntity> solarSystemEntities = solarSystems
                .stream()
                .map(SolarSystemEntity::new)
                .collect(Collectors.toList());
        final Collection<RepresentationModel<SolarSystemModel>> solarSystemModels = solarSystemEntities
                .stream()
                .map(SolarSystemModel::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(
                solarSystemModels,
                linkTo(methodOn(SolarSystemController.class).solarSystems()).withSelfRel(),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)));
    }

    @RequestMapping("/{solarSystemId}")
    ResponseEntity<RepresentationModel<SolarSystemModel>> solarSystems(@PathVariable final int solarSystemId) {
        final SolarSystem solarSystem = findSolarSystem(apiService, solarSystems, solarSystemId);
        final SolarSystemEntity solarSystemEntity = new SolarSystemEntity(solarSystem);
        final RepresentationModel<SolarSystemModel> solarSystemModel = SolarSystemModel.toModel(solarSystemEntity);
        solarSystemModel.add(
                linkTo(methodOn(StarController.class).stars(solarSystemId)).withRel("stars"),
                linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel("systems"),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)
        );
        return ResponseEntity.ok(solarSystemModel);
    }
}

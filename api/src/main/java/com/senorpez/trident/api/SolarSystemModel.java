package com.senorpez.trident.api;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "system", collectionRelation = "system")
class SolarSystemModel extends RepresentationModel<SolarSystemModel> {
    SolarSystemModel(final SolarSystemEntity content) {
        final APIResourceAssembler<SolarSystemEntity, SolarSystemModel> assembler = new APIResourceAssembler<>(
                SolarSystemController.class,
                SolarSystemModel.class,
                () -> new SolarSystemModel(content)
        );
        assembler.toModel(content);
        this.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel("systems"));
        this.add(linkTo(methodOn(StarController.class).stars(content.getId())).withRel("stars"));
    }
}

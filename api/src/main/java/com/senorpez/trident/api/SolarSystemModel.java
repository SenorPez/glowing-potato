package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "system", collectionRelation = "system")
class SolarSystemModel extends RepresentationModel<SolarSystemModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;

    static RepresentationModel<SolarSystemModel> toModel(final SolarSystemEntity content) {
        SolarSystemModelAssembler assembler = new SolarSystemModelAssembler();
        return assembler.toModel(content);
    }

    private SolarSystemModel setId(int id) {
        this.id = id;
        return this;
    }

    private SolarSystemModel setName(String name) {
        this.name = name;
        return this;
    }

    static class SolarSystemModelAssembler extends RepresentationModelAssemblerSupport<SolarSystemEntity, SolarSystemModel> {
        public SolarSystemModelAssembler() {
            super(SolarSystemController.class, SolarSystemModel.class);
        }

        @Override
        @NonNull
        public SolarSystemModel toModel(@NonNull SolarSystemEntity entity) {
            return createModelWithId(entity.getId(), entity)
                    .setId(entity.getId())
                    .setName(entity.getName());
        }
    }
}

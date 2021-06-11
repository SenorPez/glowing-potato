package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

@Relation(value = "system", collectionRelation = "system")
class SolarSystemModel extends RepresentationModel<SolarSystemModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;

    private SolarSystemModel setId(int id) {
        this.id = id;
        return this;
    }

    private SolarSystemModel setName(String name) {
        this.name = name;
        return this;
    }

    static RepresentationModel<SolarSystemModel> toModel(final SolarSystemEntity content) {
        SolarSystemModelAssembler assembler = new SolarSystemModelAssembler();
        return assembler.toModel(content);
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

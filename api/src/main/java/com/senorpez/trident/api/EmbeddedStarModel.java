package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

@Relation(value = "star", collectionRelation = "star")
class EmbeddedStarModel extends RepresentationModel<EmbeddedStarModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;

    public EmbeddedStarModel setId(int id) {
        this.id = id;
        return this;
    }

    public EmbeddedStarModel setName(String name) {
        this.name = name;
        return this;
    }

    static RepresentationModel<EmbeddedStarModel> toModel(final StarEntity entity, final int solarSystemId) {
        EmbeddedStarModelAssembler assembler = new EmbeddedStarModelAssembler();
        return assembler.toModel(entity, solarSystemId);
    }

    static class EmbeddedStarModelAssembler extends RepresentationModelAssemblerSupport<StarEntity, EmbeddedStarModel> {
        public EmbeddedStarModelAssembler() {
            super(StarController.class, EmbeddedStarModel.class);
        }

        @Override
        @NonNull
        public EmbeddedStarModel toModel(@NonNull StarEntity entity) {
            throw new NotImplementedException();
        }

        public EmbeddedStarModel toModel(StarEntity entity, final int solarSystemId) {
            return createModelWithId(entity.getId(), entity, solarSystemId)
                    .setId(entity.getId())
                    .setName(entity.getName());
        }
    }
}

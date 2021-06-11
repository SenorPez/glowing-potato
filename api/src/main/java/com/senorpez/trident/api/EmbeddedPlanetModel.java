package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Relation(value = "planet", collectionRelation = "planet")
class EmbeddedPlanetModel extends RepresentationModel<EmbeddedPlanetModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;

    public EmbeddedPlanetModel setId(int id) {
        this.id = id;
        return this;
    }

    public EmbeddedPlanetModel setName(String name) {
        this.name = name;
        return this;
    }

    static RepresentationModel<EmbeddedPlanetModel> toModel(final PlanetEntity entity, final int solarSystemId, final int starId) {
        EmbeddedPlanetModelAssembler assembler = new EmbeddedPlanetModelAssembler();
        return assembler.toModel(entity, solarSystemId, starId);
    }

    static class EmbeddedPlanetModelAssembler extends RepresentationModelAssemblerSupport<PlanetEntity, EmbeddedPlanetModel> {
        public EmbeddedPlanetModelAssembler() {
            super(PlanetController.class, EmbeddedPlanetModel.class);
        }

        @Override
        @NonNull
        public EmbeddedPlanetModel toModel(@NonNull PlanetEntity entity) {
            throw new NotImplementedException();
        }

        public EmbeddedPlanetModel toModel(final PlanetEntity entity, final int solarSystemId, final int starId) {
            return createModelWithId(entity.getId(), entity, solarSystemId, starId)
                    .setId(entity.getId())
                    .setName(entity.getName());
        }
    }

}

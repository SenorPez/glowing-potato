package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

@Relation(value = "calendar", collectionRelation = "calendar")
class EmbeddedPlanetaryCalendarModel extends RepresentationModel<EmbeddedPlanetaryCalendarModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;

    public EmbeddedPlanetaryCalendarModel setId(int id) {
        this.id = id;
        return this;
    }

    public EmbeddedPlanetaryCalendarModel setName(String name) {
        this.name = name;
        return this;
    }

    static RepresentationModel<EmbeddedPlanetaryCalendarModel> toModel(final PlanetaryCalendarEntity content, final int solarSystemId, final int starId, final int planetId) {
        EmbeddedPlanetaryCalendarModelAssembler assembler = new EmbeddedPlanetaryCalendarModelAssembler();
        return assembler.toModel(content, solarSystemId, starId, planetId);
    }

    static class EmbeddedPlanetaryCalendarModelAssembler extends RepresentationModelAssemblerSupport<PlanetaryCalendarEntity, EmbeddedPlanetaryCalendarModel> {
        public EmbeddedPlanetaryCalendarModelAssembler() {
            super(PlanetaryCalendarController.class, EmbeddedPlanetaryCalendarModel.class);
        }

        @Override
        @NonNull
        public EmbeddedPlanetaryCalendarModel toModel(@NonNull PlanetaryCalendarEntity entity) {
            throw new NotImplementedException();
        }

        public EmbeddedPlanetaryCalendarModel toModel(final PlanetaryCalendarEntity entity, final int solarSystemId, final int starId, final int planetId) {
            return createModelWithId(entity.getId(), entity, solarSystemId, starId, planetId)
                    .setId(entity.getId())
                    .setName(entity.getName());
        }
    }
}

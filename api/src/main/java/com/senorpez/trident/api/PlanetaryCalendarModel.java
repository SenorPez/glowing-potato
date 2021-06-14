package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Relation(value = "calendar", collectionRelation = "calendar")
class PlanetaryCalendarModel extends RepresentationModel<PlanetaryCalendarModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private double standardHoursPerDay;
    @JsonProperty
    private double epochOffset;

    public PlanetaryCalendarModel setId(int id) {
        this.id = id;
        return this;
    }

    public PlanetaryCalendarModel setName(String name) {
        this.name = name;
        return this;
    }

    public PlanetaryCalendarModel setStandardHoursPerDay(double standardHoursPerDay) {
        this.standardHoursPerDay = standardHoursPerDay;
        return this;
    }

    public PlanetaryCalendarModel setEpochOffset(double epochOffset) {
        this.epochOffset = epochOffset;
        return this;
    }

    static RepresentationModel<PlanetaryCalendarModel> toModel(final PlanetaryCalendarEntity content, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        PlanetaryCalendarModelAssembler assembler = new PlanetaryCalendarModelAssembler();
        return assembler.toModel(content, solarSystemId, starId, planetId, calendarId);
    }

    static class PlanetaryCalendarModelAssembler extends RepresentationModelAssemblerSupport<PlanetaryCalendarEntity, PlanetaryCalendarModel> {
        public PlanetaryCalendarModelAssembler() {
            super(PlanetaryCalendarController.class, PlanetaryCalendarModel.class);
        }

        @Override
        @NonNull
        public PlanetaryCalendarModel toModel(@NonNull PlanetaryCalendarEntity entity) {
            throw new NotImplementedException();
        }

        public PlanetaryCalendarModel toModel(final PlanetaryCalendarEntity entity, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
            return createModelWithId(entity.getId(), entity, solarSystemId, starId, planetId, calendarId)
                    .setId(entity.getId())
                    .setName(entity.getName())
                    .setStandardHoursPerDay(entity.getStandardHoursPerDay())
                    .setEpochOffset(entity.getEpochOffset());
        }
    }
}

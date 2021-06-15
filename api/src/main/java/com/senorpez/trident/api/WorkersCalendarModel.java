package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class WorkersCalendarModel extends RepresentationModel<WorkersCalendarModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private int year;
    @JsonProperty
    private int caste;
    @JsonProperty
    private int day;
    @JsonProperty
    private int shift;
    @JsonProperty
    private double tithe;

    public WorkersCalendarModel setId(int id) {
        this.id = id;
        return this;
    }

    public WorkersCalendarModel setYear(int year) {
        this.year = year;
        return this;
    }

    public WorkersCalendarModel setCaste(int caste) {
        this.caste = caste;
        return this;
    }

    public WorkersCalendarModel setDay(int day) {
        this.day = day;
        return this;
    }

    public WorkersCalendarModel setShift(int shift) {
        this.shift = shift;
        return this;
    }

    public WorkersCalendarModel setTithe(double tithe) {
        this.tithe = tithe;
        return this;
    }
    
    static RepresentationModel<WorkersCalendarModel> toModel(final WorkersCalendarEntity content, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        WorkersCalendarModelAssembler assembler = new WorkersCalendarModelAssembler();
        return assembler.toModel(content, solarSystemId, starId, planetId, calendarId);
    }
    
    static class WorkersCalendarModelAssembler extends RepresentationModelAssemblerSupport<WorkersCalendarEntity, WorkersCalendarModel> {
        public WorkersCalendarModelAssembler() {
            super(PlanetaryCalendarController.class, WorkersCalendarModel.class);
        }

        @Override
        @NonNull
        public WorkersCalendarModel toModel(@NonNull WorkersCalendarEntity entity) {
            throw new NotImplementedException();
        }

        public WorkersCalendarModel toModel(final WorkersCalendarEntity entity, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
            WorkersCalendarModel model = createModelWithId(entity.getId(), entity, solarSystemId, starId, planetId, calendarId)
                    .setId(entity.getId())
                    .setYear(entity.getYear())
                    .setCaste(entity.getCaste())
                    .setDay(entity.getDay())
                    .setShift(entity.getShift())
                    .setTithe(entity.getTithe());
            model.removeLinks();
            model.add(linkTo(methodOn(PlanetaryCalendarController.class).currentCalendar(solarSystemId, starId, planetId, calendarId)).withSelfRel());
            return model;
        }
    }
}

package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

class FestivalYearModel extends RepresentationModel<FestivalYearModel> {
    @JsonProperty
    private int localYear;
    @JsonProperty
    private boolean isFestivalYear;

    public FestivalYearModel setLocalYear(int localYear) {
        this.localYear = localYear;
        return this;
    }

    public FestivalYearModel setFestivalYear(boolean isFestivalYear) {
        this.isFestivalYear = isFestivalYear;
        return this;
    }

    static RepresentationModel<FestivalYearModel> toModel(final FestivalYearEntity content, final int solarSystemId, final int starId, final int planetId, final int calendarId, final int currentYear) {
        FestivalYearModel.FestivalYearModelAssembler assembler = new FestivalYearModel.FestivalYearModelAssembler();
        return assembler.toModel(content, solarSystemId, starId, planetId, calendarId, currentYear);
    }

    static class FestivalYearModelAssembler extends RepresentationModelAssemblerSupport<FestivalYearEntity, FestivalYearModel> {
        public FestivalYearModelAssembler() {
            super(PlanetaryCalendarController.class, FestivalYearModel.class);
        }

        @Override
        @NonNull
        public FestivalYearModel toModel(@NonNull FestivalYearEntity entity) {
            throw new NotImplementedException();
        }

        public FestivalYearModel toModel(final FestivalYearEntity entity, final int solarSystemId, final int starId, final int planetId, final int calendarId, final int currentYear) {
            FestivalYearModel model = createModelWithId(entity.getId(), entity, solarSystemId, starId, planetId, calendarId)
                    .setLocalYear(entity.getId())
                    .setFestivalYear(entity.isFestivalYear());
            model.removeLinks();
            model.add(linkTo(methodOn(PlanetaryCalendarController.class).festivalYear(solarSystemId, starId, planetId, calendarId, currentYear)).withSelfRel());
            return model;

        }
    }
}

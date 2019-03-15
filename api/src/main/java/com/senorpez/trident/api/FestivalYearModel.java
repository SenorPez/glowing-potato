package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;

public class FestivalYearModel implements Identifiable<Integer> {
    private final int localYear;
    private final boolean festivalYear;

    FestivalYearModel(final FestivalYear festivalYear) {
        this.localYear = festivalYear.getLocalYear();
        this.festivalYear = festivalYear.isFestivalYear();
    }

    FestivalYearResource toResource(final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        final APIResourceAssembler<FestivalYearModel, FestivalYearResource> assembler = new APIResourceAssembler<>(
                FestivalYearController.class,
                FestivalYearResource.class,
                () -> new FestivalYearResource(this, solarSystemId, starId, planetId, calendarId));
        return assembler.toResource(this, solarSystemId, starId, planetId, calendarId);
    }

    @Override
    @JsonProperty("localYear")
    public Integer getId() {
        return localYear;
    }

    @JsonProperty("isFestivalYear")
    public boolean isFestivalYear() {
        return festivalYear;
    }
}

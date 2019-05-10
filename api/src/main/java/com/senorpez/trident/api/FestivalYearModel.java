package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senorpez.trident.libraries.WorkersCalendar;
import org.springframework.hateoas.Identifiable;

public class FestivalYearModel implements Identifiable<Integer> {
    private final int localYear;
    private final boolean festivalYear;

    FestivalYearModel(final PlanetaryCalendar planetaryCalendar, final int localYear) {
        this.localYear = localYear;

        WorkersCalendar workersCalendar = planetaryCalendar.getWorkersCalendar();
        this.festivalYear = workersCalendar.isFestivalYear(localYear);
    }

    FestivalYearResource toResource(final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        final APIResourceAssembler<FestivalYearModel, FestivalYearResource> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                FestivalYearResource.class,
                () -> new FestivalYearResource(this, solarSystemId, starId, planetId, calendarId));
        return assembler.addIndexLink(assembler.instantiateResource(this));
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

package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.senorpez.trident.libraries.WorkersCalendar;

public class FestivalYearEntity implements APIEntity<Integer> {
    private final int localYear;
    private final boolean festivalYear;

    FestivalYearEntity(final PlanetaryCalendar planetaryCalendar, final int localYear) {
        this.localYear = localYear;

        WorkersCalendar workersCalendar = planetaryCalendar.getWorkersCalendar();
        this.festivalYear = workersCalendar.isFestivalYear(localYear);
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

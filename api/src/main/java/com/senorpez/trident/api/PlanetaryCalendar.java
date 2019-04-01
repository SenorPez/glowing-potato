package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

class PlanetaryCalendar {
    private final int id;
    private final String name;
    private final float standardHoursPerDay;
    private final float epochOffset;

    PlanetaryCalendar(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("standardHoursPerDay") final float standardHoursPerDay,
            @JsonProperty("epochOffset") final float epochOffset) {
        this.id = id;
        this.name = name;
        this.standardHoursPerDay = standardHoursPerDay;
        this.epochOffset = epochOffset;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    float getStandardHoursPerDay() {
        return standardHoursPerDay;
    }

    float getEpochOffset() {
        return epochOffset;
    }
}

package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlanetaryCalendar {
    private final int id;
    private final String name;
    private final float standardHoursPerDay;
    private final float epochOffset;

    public PlanetaryCalendar(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("standardHoursPerDay") final float standardHoursPerDay,
            @JsonProperty("epochOffset") final float epochOffset) {
        this.id = id;
        this.name = name;
        this.standardHoursPerDay = standardHoursPerDay;
        this.epochOffset = epochOffset;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getStandardHoursPerDay() {
        return standardHoursPerDay;
    }

    public double getEpochOffset() {
        return epochOffset;
    }
}

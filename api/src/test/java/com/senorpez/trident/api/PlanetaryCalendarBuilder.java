package com.senorpez.trident.api;

import java.util.Set;

public class PlanetaryCalendarBuilder {
    private int id;
    private String name;
    private float standardHoursPerDay;
    private float epochOffset;

    public PlanetaryCalendarBuilder() {
    }

    PlanetaryCalendar build() {
        return new PlanetaryCalendar(id, name, standardHoursPerDay, epochOffset);
    }

    public PlanetaryCalendarBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public PlanetaryCalendarBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlanetaryCalendarBuilder setStandardHoursPerDay(float standardHoursPerDay) {
        this.standardHoursPerDay = standardHoursPerDay;
        return this;
    }

    public PlanetaryCalendarBuilder setEpochOffset(float epochOffset) {
        this.epochOffset = epochOffset;
        return this;
    }
}
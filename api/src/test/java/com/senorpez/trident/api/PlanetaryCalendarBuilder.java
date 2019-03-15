package com.senorpez.trident.api;

class PlanetaryCalendarBuilder {
    private int id;
    private String name;
    private float standardHoursPerDay;
    private float epochOffset;

    PlanetaryCalendarBuilder() {
    }

    PlanetaryCalendar build() {
        return new PlanetaryCalendar(id, name, standardHoursPerDay, epochOffset);
    }

    PlanetaryCalendarBuilder setId(int id) {
        this.id = id;
        return this;
    }

    PlanetaryCalendarBuilder setName(String name) {
        this.name = name;
        return this;
    }

    PlanetaryCalendarBuilder setStandardHoursPerDay(float standardHoursPerDay) {
        this.standardHoursPerDay = standardHoursPerDay;
        return this;
    }

    PlanetaryCalendarBuilder setEpochOffset(float epochOffset) {
        this.epochOffset = epochOffset;
        return this;
    }
}
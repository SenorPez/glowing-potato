package com.senorpez.trident.api;

class PlanetaryCalendarEntity implements APIEntity<Integer> {
    private final int id;
    private final String name;
    private final double standardHoursPerDay;
    private final double epochOffset;

    PlanetaryCalendarEntity(final PlanetaryCalendar planetaryCalendar) {
        this.id = planetaryCalendar.getId();
        this.name = planetaryCalendar.getName();
        this.standardHoursPerDay = planetaryCalendar.getStandardHoursPerDay();
        this.epochOffset = planetaryCalendar.getEpochOffset();
    }

    @Override
    public Integer getId() {
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

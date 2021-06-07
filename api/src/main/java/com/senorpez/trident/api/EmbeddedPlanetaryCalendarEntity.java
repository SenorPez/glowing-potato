package com.senorpez.trident.api;

class EmbeddedPlanetaryCalendarEntity implements APIEntity<Integer> {
    private final int id;
    private final String name;

    EmbeddedPlanetaryCalendarEntity(final PlanetaryCalendar planetaryCalendar) {
        this.id = planetaryCalendar.getId();
        this.name = planetaryCalendar.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

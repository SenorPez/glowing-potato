package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "calendar", collectionRelation = "calendar")
class PlanetaryCalendarModel implements Identifiable<Integer> {
    private final int id;
    private final String name;
    private final float standardHoursPerDay;
    private final float epochOffset;

    PlanetaryCalendarModel(final PlanetaryCalendar planetaryCalendar) {
        this.id = planetaryCalendar.getId();
        this.name = planetaryCalendar.getName();
        this.standardHoursPerDay = planetaryCalendar.getStandardHoursPerDay();
        this.epochOffset = planetaryCalendar.getEpochOffset();
    }

    PlanetaryCalendarResource toResource(final int solarSystemId, final int starId, final int planetId) {
        final APIResourceAssembler<PlanetaryCalendarModel, PlanetaryCalendarResource> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                PlanetaryCalendarResource.class,
                () -> new PlanetaryCalendarResource(this, solarSystemId, starId, planetId));
        return assembler.toResource(this, solarSystemId, starId, planetId);
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

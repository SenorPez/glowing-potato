package com.senorpez.trident.api;

import com.senorpez.trident.libraries.WorkersCalendar;
import org.springframework.hateoas.Identifiable;

class WorkersCalendarModel implements Identifiable<Integer> {
    private final int id;
    private final int year;
    private final int caste;
    private final int day;
    private final int shift;
    private final double tithe;

    WorkersCalendarModel(final PlanetaryCalendar planetaryCalendar) {
        this.id = planetaryCalendar.getId();

        WorkersCalendar workersCalendar = planetaryCalendar.getWorkersCalendar();
        this.year = workersCalendar.getLocalYear();
        this.caste = workersCalendar.getCaste();
        this.day = workersCalendar.getCasteDay();
        this.shift = workersCalendar.getShift();
        this.tithe = workersCalendar.getTithe();
    }

    WorkersCalendarResource toResource(final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        final APIResourceAssembler<WorkersCalendarModel, WorkersCalendarResource> assembler = new APIResourceAssembler<>(
                PlanetaryCalendarController.class,
                WorkersCalendarResource.class,
                () -> new WorkersCalendarResource(this, solarSystemId, starId, planetId, calendarId));
        return assembler.addIndexLink(assembler.instantiateResource(this));
    }

    @Override
    public Integer getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getCaste() {
        return caste;
    }

    public int getDay() {
        return day;
    }

    public int getShift() {
        return shift;
    }

    public double getTithe() {
        return tithe;
    }
}

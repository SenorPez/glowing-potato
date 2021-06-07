package com.senorpez.trident.api;

import com.senorpez.trident.libraries.WorkersCalendar;

class WorkersCalendarEntity implements APIEntity<Integer> {
    private final int id;
    private final int year;
    private final int caste;
    private final int day;
    private final int shift;
    private final double tithe;

    WorkersCalendarEntity(final PlanetaryCalendar planetaryCalendar) {
        this.id = planetaryCalendar.getId();

        WorkersCalendar workersCalendar = planetaryCalendar.getWorkersCalendar();
        this.year = workersCalendar.getLocalYear();
        this.caste = workersCalendar.getCaste();
        this.day = workersCalendar.getCasteDay();
        this.shift = workersCalendar.getShift();
        this.tithe = workersCalendar.getTithe();
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

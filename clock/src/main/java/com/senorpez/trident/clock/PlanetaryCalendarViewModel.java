package com.senorpez.trident.clock;

import androidx.lifecycle.ViewModel;

import java.util.Locale;

class PlanetaryCalendarViewModel extends ViewModel {
    private PlanetaryCalendar planetaryCalendar;

    void init(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
    }

    int getShift() {
        return planetaryCalendar.getShift();
    }

    int getTithe() {
        return (int) Math.floor(planetaryCalendar.getTithe() * 10);
    }

    int getSubtithe() {
        return (int) (Math.floor(planetaryCalendar.getTithe() * 100)) % 10;
    }

    int getSpinner() {
        return (int) (Math.floor(planetaryCalendar.getTithe() * 1000)) % 10;
    }

    String getLocalDateTime() {
        return String.format(
                Locale.US,
                "%d FY %d Caste %d Day %d.%02d Shift",
                planetaryCalendar.getLocalYear(),
                planetaryCalendar.getCaste(),
                planetaryCalendar.getCasteDay(),
                planetaryCalendar.getShift(),
                (int) Math.floor(planetaryCalendar.getTithe() * 100));
    }
}

package com.senorpez.trident.clock;

import androidx.lifecycle.ViewModel;

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
}

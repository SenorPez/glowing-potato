package com.senorpez.trident.clock;

import androidx.lifecycle.ViewModel;

class PlanetaryCalendarViewModel extends ViewModel {
    private PlanetaryCalendar planetaryCalendar;

    void init(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
    }

    int getShift() {
        return -1;
    }
}

package com.senorpez.trident.clock;

import android.arch.lifecycle.ViewModel;

public class PlanetaryCalendarViewModel extends ViewModel {
    private PlanetaryCalendar planetaryCalendar;

    public PlanetaryCalendarViewModel(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
    }

    PlanetaryCalendar getPlanetaryCalendar() {
        return planetaryCalendar;
    }
}

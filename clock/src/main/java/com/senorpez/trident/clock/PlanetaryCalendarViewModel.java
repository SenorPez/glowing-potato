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
        if (planetaryCalendar.getCasteDay() == 0) {
            if (planetaryCalendar.getCaste() == 0) {
                return String.format(
                        Locale.US,
                        "%d FY Festival Day %d.%02d Shift",
                        planetaryCalendar.getLocalYear(),
                        planetaryCalendar.getShift(),
                        (int) Math.floor(planetaryCalendar.getTithe() * 100));
            } else if (planetaryCalendar.getCaste() == 3) {
                return String.format(
                        Locale.US,
                        "%d FY Midfestival Day %d.%02d Shift",
                        planetaryCalendar.getLocalYear(),
                        planetaryCalendar.getShift(),
                        (int) Math.floor(planetaryCalendar.getTithe() * 100));
            }
        }
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

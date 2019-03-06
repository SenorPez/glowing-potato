package com.senorpez.trident.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class PlanetaryCalendarViewModel extends ViewModel {
    private PlanetaryCalendar planetaryCalendar;

    private ClockLiveData<Integer> shift;
    private ClockLiveData<Integer> tithe;
    private ClockLiveData<Integer> subTithe;
    private ClockLiveData<Integer> spinner;
    private ClockLiveData<String> localDateTime;
    private ClockLiveData<String> standardDateTime;

    public PlanetaryCalendarViewModel(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
        this.shift = new ClockLiveData<>(() -> this.getShift().getValue());
        this.tithe = new ClockLiveData<>(() -> this.getTithe().getValue());
        this.subTithe = new ClockLiveData<>(() -> this.getSubtithe().getValue());
        this.spinner = new ClockLiveData<>(() -> this.getSpinner().getValue());
        this.localDateTime = new ClockLiveData<>(() -> this.getLocalDateTime().getValue());
        this.standardDateTime = new ClockLiveData<>(() -> this.getStandardDateTime().getValue());
    }

    LiveData<Integer> getShift() {
        shift.setValue(planetaryCalendar.getShift());
        return shift;
    }

    LiveData<Integer> getTithe() {
        tithe.setValue((int) Math.floor(planetaryCalendar.getTithe() * 10));
        return tithe;
    }

    LiveData<Integer> getSubtithe() {
        subTithe.setValue((int) (Math.floor(planetaryCalendar.getTithe() * 100)) % 10);
        return subTithe;
    }

    LiveData<Integer> getSpinner() {
        spinner.setValue((int) (Math.floor(planetaryCalendar.getTithe() * 1000)) % 10);
        return spinner;
    }

    LiveData<String> getLocalDateTime() {
        localDateTime.setValue(createLocalDateTime());
        return localDateTime;
    }

    private String createLocalDateTime() {
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

    LiveData<String> getStandardDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy G MMM d HH:mm:ss", Locale.US);
        standardDateTime.setValue(formatter.format(ZonedDateTime.now()));
        return standardDateTime;
    }
}

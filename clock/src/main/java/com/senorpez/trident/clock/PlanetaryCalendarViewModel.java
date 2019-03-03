package com.senorpez.trident.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Locale;

class PlanetaryCalendarViewModel extends ViewModel {
    private PlanetaryCalendar planetaryCalendar;

    private ClockLiveData<Integer> shift;
    private ClockLiveData<Integer> tithe;
    private ClockLiveData<Integer> subTithe;
    private ClockLiveData<Integer> spinner;
    private ClockLiveData<String> localDateTime;
    private ClockLiveData<String> standardDateTime;

    void init(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
        this.shift = new ClockLiveData<>(() -> this.getShift().getValue());
        this.tithe = new ClockLiveData<>(() -> this.getTithe().getValue());
        this.subTithe = new ClockLiveData<>(() -> this.getSubtithe().getValue());
        this.spinner = new ClockLiveData<>(() -> this.getSpinner().getValue());
        this.localDateTime = new ClockLiveData<>(this::createLocalDateTime);
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
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy G MMM d HH:mm:ss", Locale.US);

        try {
            Date date = inputFormat.parse(String.format("%s", Clock.systemUTC().instant()));
            standardDateTime.setValue(outputFormat.format(date));
            return standardDateTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

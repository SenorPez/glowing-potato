package com.senorpez.trident.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Locale;

class PlanetaryCalendarViewModel extends ViewModel {
    private PlanetaryCalendar planetaryCalendar;

    private final MutableLiveData<Integer> shift = new MutableLiveData<>();
    private final MutableLiveData<Integer> tithe = new MutableLiveData<>();
    private final MutableLiveData<Integer> subTithe = new MutableLiveData<>();
    private final MutableLiveData<Integer> spinner = new MutableLiveData<>();
    private final LiveData<String> localDateTime = new MutableLiveData<>();
    private final LiveData<String> standardDateTime = new MutableLiveData<>();

    void init(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
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

    String getStandardDateTime() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy G MMM d HH:mm:ss", Locale.US);

        try {
            Date date = inputFormat.parse(String.format("%s", Clock.systemUTC().instant()));
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

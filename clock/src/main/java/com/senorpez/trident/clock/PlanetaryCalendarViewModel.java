package com.senorpez.trident.clock;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

class PlanetaryCalendarViewModel extends ViewModel {
    private LiveData<PlanetaryCalendar> planetaryCalendar;
    private ClockLiveData<Integer> shift = new ClockLiveData<>(this::createShift);
    private ClockLiveData<Integer> tithe = new ClockLiveData<>(this::createTithe);
    private ClockLiveData<Integer> subTithe = new ClockLiveData<>(this::createSubTithe);
    private ClockLiveData<Integer> spinner = new ClockLiveData<>(this::createSpinner);
    private ClockLiveData<String> localDateTime = new ClockLiveData<>(this::createLocalDateTime);
    private ClockLiveData<String> standardDateTime = new ClockLiveData<>(this::createStandardDateTime);

    @Inject
    PlanetaryCalendarViewModel(PlanetaryCalendarRepository planetaryCalendarRepository) {
        planetaryCalendar = planetaryCalendarRepository.getPlanetaryCalendar();
    }

    ClockLiveData<Integer> getShift() {
        return shift;
    }

    ClockLiveData<Integer> getTithe() {
        return tithe;
    }

    ClockLiveData<Integer> getSubTithe() {
        return subTithe;
    }

    ClockLiveData<Integer> getSpinner() {
        return spinner;
    }

    ClockLiveData<String> getLocalDateTime() {
        return localDateTime;
    }

    ClockLiveData<String> getStandardDateTime() {
        return standardDateTime;
    }

    private Integer createShift() {
        if (planetaryCalendar.getValue() == null) {
            return 0;
        } else {
            return planetaryCalendar.getValue().getShift() - 1;
        }
    }

    private Integer createTithe() {
        if (planetaryCalendar.getValue() == null) {
            return 0;
        } else {
            return (int) Math.floor(planetaryCalendar.getValue().getTithe() * 10);
        }
    }

    private Integer createSubTithe() {
        if (planetaryCalendar.getValue() == null) {
            return 0;
        } else {
            return (int) (Math.floor(planetaryCalendar.getValue().getTithe() * 100)) % 10;
        }
    }

    private Integer createSpinner() {
        if (planetaryCalendar.getValue() == null) {
            return 0;
        } else {
            return (int) (Math.floor(planetaryCalendar.getValue().getTithe() * 1000)) % 10;
        }
    }

    private String createLocalDateTime() {
        if (planetaryCalendar.getValue() == null) {
            return "NULL";
        } else {
            if (planetaryCalendar.getValue().getCasteDay() == 0
                    && planetaryCalendar.getValue().getCaste() == 0) {
                return String.format(
                        Locale.US,
                        "%d FY Festival Day %d.%02d Shift",
                        planetaryCalendar.getValue().getLocalYear(),
                        planetaryCalendar.getValue().getShift(),
                        (int) Math.floor(planetaryCalendar.getValue().getTithe() * 100));
            } else if (planetaryCalendar.getValue().getCasteDay() == 0
                    && planetaryCalendar.getValue().getCaste() == 3) {
                return String.format(
                        Locale.US,
                        "%d FY Midfestival Day %d.%02d Shift",
                        planetaryCalendar.getValue().getLocalYear(),
                        planetaryCalendar.getValue().getShift(),
                        (int) Math.floor(planetaryCalendar.getValue().getTithe() * 100));
            } else {
                return String.format(
                        Locale.US,
                        "%d FY %d Caste %d Day %d.%02d Shift",
                        planetaryCalendar.getValue().getLocalYear(),
                        planetaryCalendar.getValue().getCaste(),
                        planetaryCalendar.getValue().getCasteDay(),
                        planetaryCalendar.getValue().getShift(),
                        (int) Math.floor(planetaryCalendar.getValue().getTithe() * 100));
            }
        }
    }

    private String createStandardDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy G MMM d HH:mm:ss", Locale.US);
        return formatter.format(ZonedDateTime.now());
    }
}

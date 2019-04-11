package com.senorpez.trident.clock;


import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PlanetaryCalendarViewModelFactory implements ViewModelProvider.Factory {
    private PlanetaryCalendar planetaryCalendar;
    private PlanetaryCalendarRepository planetaryCalendarRepository;

    @Inject
    public PlanetaryCalendarViewModelFactory(PlanetaryCalendarRepository planetaryCalendarRepository) {
        this.planetaryCalendarRepository = planetaryCalendarRepository;
    }

    public PlanetaryCalendarViewModelFactory(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PlanetaryCalendarViewModel(planetaryCalendarRepository);
    }
}

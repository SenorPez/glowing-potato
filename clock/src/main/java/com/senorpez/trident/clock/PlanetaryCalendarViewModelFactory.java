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
    private final PlanetaryCalendar planetaryCalendar;

    @Inject
    public PlanetaryCalendarViewModelFactory() {
        Clock clockJ2000 = Clock.offset(
                Clock.systemUTC(),
                Duration.ofMillis(
                        Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1));
        this.planetaryCalendar = new PlanetaryCalendar(clockJ2000);
    }

    public PlanetaryCalendarViewModelFactory(PlanetaryCalendar planetaryCalendar) {
        this.planetaryCalendar = planetaryCalendar;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PlanetaryCalendarViewModel(planetaryCalendar);
    }
}

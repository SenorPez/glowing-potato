package com.senorpez.trident.clock;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.time.*;

public class PlanetaryCalendarViewModelFactory implements ViewModelProvider.Factory {
    private PlanetaryCalendar planetaryCalendar;

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

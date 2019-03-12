package com.senorpez.trident.clock;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class PlanetaryClockTestActivityModule {
    private PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory;

    public PlanetaryClockTestActivityModule(PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory) {
        this.planetaryCalendarViewModelFactory = planetaryCalendarViewModelFactory;
    }

    @Provides
    @Singleton
    PlanetaryCalendarViewModelFactory providesPlanetaryCalendarViewModelFactory() {
        return planetaryCalendarViewModelFactory;
    }
}

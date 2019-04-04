package com.senorpez.trident.clock;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
class PlanetaryClockTestActivityModule {
    private final PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory;

    PlanetaryClockTestActivityModule(PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory) {
        this.planetaryCalendarViewModelFactory = planetaryCalendarViewModelFactory;
    }

    @Provides
    @Singleton
    PlanetaryCalendarViewModelFactory providesPlanetaryCalendarViewModelFactory() {
        return planetaryCalendarViewModelFactory;
    }
}

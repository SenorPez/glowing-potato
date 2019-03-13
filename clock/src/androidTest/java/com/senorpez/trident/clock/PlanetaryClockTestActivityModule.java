package com.senorpez.trident.clock;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class PlanetaryClockTestActivityModule {
    private PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory;

    PlanetaryClockTestActivityModule(PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory) {
        this.planetaryCalendarViewModelFactory = planetaryCalendarViewModelFactory;
    }

    @Provides
    @Singleton
    PlanetaryCalendarViewModelFactory providesPlanetaryCalendarViewModelFactory() {
        return planetaryCalendarViewModelFactory;
    }
}

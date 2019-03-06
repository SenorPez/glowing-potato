package com.senorpez.trident.clock;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class PlanetaryCalendarViewModelFactoryModel {
    @Provides
    @Singleton
    PlanetaryCalendarViewModelFactory providePlanetaryCalendarViewModelFactory() {
        return new PlanetaryCalendarViewModelFactory();
    }
}

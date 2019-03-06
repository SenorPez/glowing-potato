package com.senorpez.trident.clock;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {PlanetaryCalendarViewModelFactoryModule.class})
public interface PlanetaryClockComponent {
    void inject(PlanetaryClockActivity activity);
}

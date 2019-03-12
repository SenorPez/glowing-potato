package com.senorpez.trident.clock;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

import javax.inject.Singleton;

@Component(modules = {AndroidInjectionModule.class, PlanetaryClockApplicationModule.class, PlanetaryClockTestActivityModule.class})
@Singleton
public interface PlanetaryClockTestApplicationComponent extends AndroidInjector<PlanetaryClockTestApplication> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<PlanetaryClockTestApplication> {
        abstract Builder activityModule(PlanetaryClockTestActivityModule activityModule);
    }
}

package com.senorpez.trident.clock;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Component(modules = {AndroidInjectionModule.class, PlanetaryClockApplicationModule.class})
public interface PlanetaryClockApplicationComponent extends AndroidInjector<PlanetaryClockApplication> {
}

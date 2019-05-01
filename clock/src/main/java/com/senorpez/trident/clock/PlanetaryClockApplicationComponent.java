package com.senorpez.trident.clock;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {AndroidInjectionModule.class, PlanetaryClockApplicationModule.class})
interface PlanetaryClockApplicationComponent extends AndroidInjector<PlanetaryClockApplication> {
}

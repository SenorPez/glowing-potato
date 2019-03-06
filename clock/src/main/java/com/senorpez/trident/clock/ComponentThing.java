package com.senorpez.trident.clock;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AndroidInjectionModule.class, NetModule.class})
public interface ComponentThing {
    void inject(PlanetaryClockActivity activity);
}

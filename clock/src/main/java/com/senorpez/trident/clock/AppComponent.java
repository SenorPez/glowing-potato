package com.senorpez.trident.clock;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {
    void inject(PlanetaryClockActivity activity);
}

package com.senorpez.trident.clock;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class PlanetaryClockApplication extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerPlanetaryClockApplicationComponent.builder().create(this);
    }
}

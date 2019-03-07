package com.senorpez.trident.clock;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class PlanetaryClockApplicationModule {
    @ContributesAndroidInjector
    abstract PlanetaryClockActivity contributeActivityInjector();
}

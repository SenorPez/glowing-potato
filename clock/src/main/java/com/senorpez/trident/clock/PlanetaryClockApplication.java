package com.senorpez.trident.clock;

import android.app.Activity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.DispatchingAndroidInjector;

public class PlanetaryClockApplication extends DaggerApplication {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerPlanetaryClockApplicationComponent.builder().build();
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}

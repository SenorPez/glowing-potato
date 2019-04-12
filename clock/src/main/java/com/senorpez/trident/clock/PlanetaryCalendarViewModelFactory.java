package com.senorpez.trident.clock;


import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class PlanetaryCalendarViewModelFactory implements ViewModelProvider.Factory {
    private final PlanetaryCalendarRepository planetaryCalendarRepository;

    @Inject
    public PlanetaryCalendarViewModelFactory(PlanetaryCalendarRepository planetaryCalendarRepository) {
        this.planetaryCalendarRepository = planetaryCalendarRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PlanetaryCalendarViewModel(planetaryCalendarRepository);
    }
}

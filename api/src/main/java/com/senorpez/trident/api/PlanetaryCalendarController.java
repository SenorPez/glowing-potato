package com.senorpez.trident.api;

import java.util.Collection;

public class PlanetaryCalendarController {
    private final APIService apiService;
    private final Collection<SolarSystem> solarSystems;

    public PlanetaryCalendarController(APIService apiService) {
        this.apiService = apiService;
        this.solarSystems = Application.SOLAR_SYSTEMS;
    }
}

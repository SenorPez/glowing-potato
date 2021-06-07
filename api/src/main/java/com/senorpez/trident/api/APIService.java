package com.senorpez.trident.api;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
class APIService {
    <T> T findOne(final Collection<T> collection, final Predicate<T> predicate, final Supplier<RuntimeException> exceptionSupplier) {
        return collection.stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(exceptionSupplier);
    }

    <T> Collection<T> findAll(final Collection<T> collection) {
        return collection;
    }

    static SolarSystem findSolarSystem(final APIService apiService, final Collection<SolarSystem> solarSystems, final int solarSystemId) {
        return apiService.findOne(
                solarSystems,
                findSolarSystem -> findSolarSystem.getId() == solarSystemId,
                () -> new SolarSystemNotFoundException(solarSystemId)
        );
    }

    static Star findStar(final APIService apiService, final Collection<SolarSystem> solarSystems, final int solarSystemId, final int starId) {
        final SolarSystem solarSystem = findSolarSystem(apiService, solarSystems, solarSystemId);
        return apiService.findOne(
                solarSystem.getStars(),
                findStar -> findStar.getId() == starId,
                () -> new StarNotFoundException(starId)
        );
    }

    static Planet findPlanet(final APIService apiService, final Collection<SolarSystem> solarSystems, final int solarSystemId, final int starId, final int planetId) {
        final Star star = findStar(apiService, solarSystems, solarSystemId, starId);
        return apiService.findOne(
                star.getPlanets(),
                findPlanet -> findPlanet.getId() == planetId,
                () -> new PlanetNotFoundException(planetId)
        );
    }

    static PlanetaryCalendar findCalendar(final APIService apiService, final Collection<SolarSystem> solarSystems, final int solarSystemId, final int starId, final int planetId, final int calendarId) {
        final Planet planet = findPlanet(apiService, solarSystems, solarSystemId, starId, planetId);
        return apiService.findOne(
                planet.getCalendars(),
                findCalendar -> findCalendar.getId() == calendarId,
                () -> new PlanetaryCalendarNotFoundException(calendarId)
        );
    }
}

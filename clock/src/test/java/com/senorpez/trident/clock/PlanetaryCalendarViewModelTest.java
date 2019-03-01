package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class PlanetaryCalendarViewModelTest {
    private PlanetaryCalendarViewModel planetaryCalendarViewModel = new PlanetaryCalendarViewModel();

    @Mock
    PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPlanetaryCalendar() {
        planetaryCalendarViewModel.init(planetaryCalendar);
        assertThat(planetaryCalendarViewModel.getPlanetaryCalendar(), sameInstance(planetaryCalendar));
    }
}

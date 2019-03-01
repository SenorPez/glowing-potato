package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PlanetaryCalendarViewModelTest {
    @InjectMocks
    PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Mock
    PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPlanetaryCalendar() {
        assertThat(planetaryCalendarViewModel.getPlanetaryCalendar(), is(planetaryCalendar));
    }
}

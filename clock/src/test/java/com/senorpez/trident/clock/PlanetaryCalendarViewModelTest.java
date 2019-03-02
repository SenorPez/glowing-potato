package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class PlanetaryCalendarViewModelTest {
    private PlanetaryCalendarViewModel planetaryCalendarViewModel = new PlanetaryCalendarViewModel();

    @Mock
    PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        planetaryCalendarViewModel.init(planetaryCalendar);
    }

    @Test
    public void testGetShift() {
        when(planetaryCalendar.getShift()).thenReturn(1);
        assertThat(planetaryCalendarViewModel.getShift(), is(1));
    }

    @Test
    public void testGetTithe() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getTithe(), is(3));
    }

    @Test
    public void testGetSubtithe() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getSubtithe(), is(2));
    }

    @Test
    public void testGetSpinner() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getSpinner(), is(9));
    }

    @Test
    public void testGetLocalDateTime_Regular() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(2);
        when(planetaryCalendar.getCasteDay()).thenReturn(15);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getLocalDateTime(), is("35 FY 2 Caste 15 Day 3.32 Shift"));
    }

    @Test
    public void testGetLocalDateTime_FestivalDay() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(0);
        when(planetaryCalendar.getCasteDay()).thenReturn(0);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getLocalDateTime(), is("35 FY Festival Day 3.32 Shift"));
    }

    @Test
    public void testGetLocalDateTime_MidfestivalDay() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(3);
        when(planetaryCalendar.getCasteDay()).thenReturn(0);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getLocalDateTime(), is("35 FY Midfestival Day 3.32 Shift"));
    }
}

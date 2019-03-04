package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class PlanetaryCalendarCasteTest {
    private double days;
    private boolean festivalDay;
    private int caste;
    private int casteDay;
    private double standardMilliseconds;

    @InjectMocks
    PlanetaryCalendar planetaryCalendar;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameterized.Parameters(name = "days: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {4569, true, 0, 0, 591860140233.182},
                {4570, false, 1, 1, 591991045183.862},
                {4588, false, 1, 19, 594347334296.102},
                {4589, false, 2, 1, 594478239246.782},
                {4608, false, 2, 20, 596965433309.702},
                {4609, false, 3, 1, 597096338260.382},
                {4628, false, 3, 20, 599583532323.302},
                {4629, false, 4, 1, 599714437273.982},
                {4648, false, 4, 20, 602201631336.902},
                {4649, false, 5, 1, 602332536287.582},
                {4667, false, 5, 19, 604688825399.822},
                {4668, true, 0, 0, 604819730350.502},
                {4669, false, 1, 1, 604950635301.182},
                {4687, false, 1, 19, 607306924413.422},
                {4688, false, 2, 1, 607437829364.102},
                {4707, false, 2, 20, 609925023427.022},
                {4708, false, 3, 1, 610055928377.702},
                {4717, false, 3, 10, 611234072933.822},
                {4718, true, 3, 0, 611364977884.502},
                {4719, false, 3, 11, 611495882835.182},
                {4728, false, 3, 20, 612674027391.302},
                {4729, false, 4, 1, 612804932341.982},
                {4748, false, 4, 20, 615292126404.902},
                {4749, false, 5, 1, 615423031355.582},
                {4767, false, 5, 19, 617779320467.822}
        });
    }

    public PlanetaryCalendarCasteTest(double days, boolean festivalDay, int caste, int casteDay, double standardMilliseconds) {
        this.days = days;
        this.festivalDay = festivalDay;
        this.caste = caste;
        this.casteDay = casteDay;
        this.standardMilliseconds = standardMilliseconds;
    }

    @Test
    public void testGetCaste() {
        assertThat(planetaryCalendar.getCaste(days), is(caste));
    }

    @Test
    public void testGetCaste_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getCaste(), is(caste));
    }

    @Test
    public void testGetCasteDay() {
        assertThat(planetaryCalendar.getCasteDay(days), is(casteDay));
    }

    @Test
    public void testGetCasteDay_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getCasteDay(), is(casteDay));
    }

    @Test
    public void testIsFestivalDay() {
        assertThat(planetaryCalendar.isFestivalDay(days), is(festivalDay));
    }

    @Test
    public void testIsFestivalDay_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.isFestivalDay(), is(festivalDay));
    }
}

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
                {4569, true, 0, 0, 595142368690.392},
                {4570, false, 1, 1, 595273273641.072},
                {4588, false, 1, 19, 597629562753.312},
                {4589, false, 2, 1, 597760467703.992},
                {4608, false, 2, 20, 600247661766.912},
                {4609, false, 3, 1, 600378566717.592},
                {4628, false, 3, 20, 602865760780.512},
                {4629, false, 4, 1, 602996665731.192},
                {4648, false, 4, 20, 605483859794.112},
                {4649, false, 5, 1, 605614764744.792},
                {4667, false, 5, 19, 607971053857.032},
                {4668, true, 0, 0, 608101958807.712},
                {4669, false, 1, 1, 608232863758.392},
                {4687, false, 1, 19, 610589152870.632},
                {4688, false, 2, 1, 610720057821.312},
                {4707, false, 2, 20, 613207251884.232},
                {4708, false, 3, 1, 613338156834.912},
                {4717, false, 3, 10, 614516301391.032},
                {4718, true, 3, 0, 614647206341.712},
                {4719, false, 3, 11, 614778111292.392},
                {4728, false, 3, 20, 615956255848.512},
                {4729, false, 4, 1, 616087160799.192},
                {4748, false, 4, 20, 618574354862.112},
                {4749, false, 5, 1, 618705259812.792},
                {4767, false, 5, 19, 621061548925.032}
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

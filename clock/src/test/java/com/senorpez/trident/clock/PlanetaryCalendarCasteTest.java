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
                {4569, true, 0, 0, 601067070623.448},
                {4570, false, 1, 1, 601197975574.128},
                {4588, false, 1, 19, 603554264686.368},
                {4589, false, 2, 1, 603685169637.048},
                {4608, false, 2, 20, 606172363699.968},
                {4609, false, 3, 1, 606303268650.648},
                {4628, false, 3, 20, 608790462713.568},
                {4629, false, 4, 1, 608921367664.248},
                {4648, false, 4, 20, 611408561727.168},
                {4649, false, 5, 1, 611539466677.848},
                {4667, false, 5, 19, 613895755790.088},
                {4668, true, 0, 0, 614026660740.768},
                {4669, false, 1, 1, 614157565691.448},
                {4687, false, 1, 19, 616513854803.688},
                {4688, false, 2, 1, 616644759754.368},
                {4707, false, 2, 20, 619131953817.288},
                {4708, false, 3, 1, 619262858767.968},
                {4717, false, 3, 10, 620441003324.088},
                {4718, true, 3, 0, 620571908274.768},
                {4719, false, 3, 11, 620702813225.448},
                {4728, false, 3, 20, 621880957781.568},
                {4729, false, 4, 1, 622011862732.248},
                {4748, false, 4, 20, 624499056795.168},
                {4749, false, 5, 1, 624629961745.848},
                {4767, false, 5, 19, 626986250858.088}
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
    public void testIsFestivalDay() {
        assertThat(planetaryCalendar.isFestivalDay(days), is(festivalDay));
    }

    @Test
    public void testGetCasteDay() {
        assertThat(planetaryCalendar.getCasteDay(days), is(casteDay));
    }

    @Test
    public void testGetCaste_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getCaste(), is(caste));
    }

    @Test
    public void testIsFestivalDay_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.isFestivalDay(), is(festivalDay));
    }

    @Test
    public void testGetCasteDay_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getCasteDay(), is(casteDay));
    }
}

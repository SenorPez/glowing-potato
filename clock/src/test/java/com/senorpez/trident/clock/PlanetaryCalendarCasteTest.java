package com.senorpez.trident.clock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.Clock;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class PlanetaryCalendarCasteTest {
    private double days;
    private boolean festivalDay;
    private int caste;
    private int casteDay;

    private PlanetaryCalendar planetaryCalendar = new PlanetaryCalendar(Clock.systemUTC());

    @Parameterized.Parameters(name = "days: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {4569, true, 0, 0},
                {4570, false, 1, 1},
                {4588, false, 1, 19},
                {4589, false, 2, 1},
                {4608, false, 2, 20},
                {4609, false, 3, 1},
                {4628, false, 3, 20},
                {4629, false, 4, 1},
                {4648, false, 4, 20},
                {4649, false, 5, 1},
                {4667, false, 5, 19},
                {4668, true, 0, 0},
                {4669, false, 1, 1},
                {4687, false, 1, 19},
                {4688, false, 2, 1},
                {4707, false, 2, 20},
                {4708, false, 3, 1},
                {4717, false, 3, 10},
                {4718, true, 3, 0},
                {4719, false, 3, 11},
                {4728, false, 3, 20},
                {4729, false, 4, 1},
                {4748, false, 4, 20},
                {4749, false, 5, 1},
                {4767, false, 5, 19}
        });
    }

    public PlanetaryCalendarCasteTest(double days, boolean festivalDay, int caste, int casteDay) {
        this.days = days;
        this.festivalDay = festivalDay;
        this.caste = caste;
        this.casteDay = casteDay;
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
}

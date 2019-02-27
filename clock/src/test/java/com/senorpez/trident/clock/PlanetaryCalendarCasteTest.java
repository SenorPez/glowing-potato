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
    private PlanetaryCalendar planetaryCalendar = new PlanetaryCalendar(Clock.systemUTC());

    @Parameterized.Parameters(name = "days: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {4569, true, 0},
                {4570, false, 1},
                {4589, false, 2},
                {4609, false, 3},
                {4629, false, 4},
                {4649, false, 5},
                {4668, true, 0},
                {4669, false, 1},
                {4688, false, 2},
                {4708, false, 3},
                {4718, true, 3},
                {4729, false, 4},
                {4749, false, 5}
        });
    }

    public PlanetaryCalendarCasteTest(double days, boolean festivalDay, int caste) {
        this.days = days;
        this.festivalDay = festivalDay;
        this.caste = caste;
    }

    @Test
    public void testGetCaste() {
        assertThat(planetaryCalendar.getCaste(days), is(caste));
    }

    @Test
    public void testIsFestivalDay() {
        assertThat(planetaryCalendar.isFestivalDay(days), is(festivalDay));
    }
}

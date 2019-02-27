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
public class PlanetaryCalendarTimeTest {
    private double days;
    private int shift;
    private PlanetaryCalendar planetaryCalendar = new PlanetaryCalendar(Clock.systemUTC());

    @Parameterized.Parameters(name = "days: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {4569.00, 1},
                {4569.24, 1},
                {4569.25, 2},
                {4569.49, 2},
                {4569.50, 3},
                {4569.74, 3},
                {4569.75, 4},
                {4569.99, 4}
        });
    }

    public PlanetaryCalendarTimeTest(double days, int shift) {
        this.days = days;
        this.shift = shift;
    }

    @Test
    public void testGetShift() {
        assertThat(planetaryCalendar.getShift(days), is(shift));
    }
}

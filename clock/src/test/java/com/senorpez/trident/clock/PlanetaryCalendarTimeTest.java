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
    private double tithe;

    private PlanetaryCalendar planetaryCalendar = new PlanetaryCalendar(Clock.systemUTC());

    @Parameterized.Parameters(name = "days: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {4569.00, 1, 0.00},
                {4569.24, 1, 0.24},
                {4569.25, 2, 0.25},
                {4569.49, 2, 0.49},
                {4569.50, 3, 0.50},
                {4569.74, 3, 0.74},
                {4569.75, 4, 0.75},
                {4569.99, 4, 0.99}
        });
    }

    public PlanetaryCalendarTimeTest(double days, int shift, double tithe) {
        this.days = days;
        this.shift = shift;
        this.tithe = tithe;
    }

    @Test
    public void testGetShift() {
        assertThat(planetaryCalendar.getShift(days), is(shift));
    }

    @Test
    public void testGetTithe() {
        assertThat(planetaryCalendar.getTithe(days), is(tithe));
    }
}

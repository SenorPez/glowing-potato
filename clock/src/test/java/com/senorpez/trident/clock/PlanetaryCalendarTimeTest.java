package com.senorpez.trident.clock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.Clock;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

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
                {4569.24, 1, 0.96},
                {4569.25, 2, 0.00},
                {4569.49, 2, 0.96},
                {4569.50, 3, 0.00},
                {4569.74, 3, 0.96},
                {4569.75, 4, 0.00},
                {4569.99, 4, 0.96}
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
        assertThat(planetaryCalendar.getTithe(days), closeTo(tithe, 1e-9));
    }
}

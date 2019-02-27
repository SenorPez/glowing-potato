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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class PlanetaryCalendarTimeTest {
    private double days;
    private int shift;
    private double tithe;
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
                {4569.00, 1, 0.00, 601067070623.448},
                {4569.24, 1, 0.96, 601098487811.611},
                {4569.25, 2, 0.00, 601099796861.118},
                {4569.49, 2, 0.96, 601131214049.281},
                {4569.50, 3, 0.00, 601132523098.788},
                {4569.74, 3, 0.96, 601163940286.951},
                {4569.75, 4, 0.00, 601165249336.458},
                {4569.99, 4, 0.96, 601196666524.621}
        });
    }

    public PlanetaryCalendarTimeTest(double days, int shift, double tithe, double standardMilliseconds) {
        this.days = days;
        this.shift = shift;
        this.tithe = tithe;
        this.standardMilliseconds = standardMilliseconds;
    }

    @Test
    public void testGetShift() {
        assertThat(planetaryCalendar.getShift(days), is(shift));
    }

    @Test
    public void testGetTithe() {
        assertThat(planetaryCalendar.getTithe(days), closeTo(tithe, 1e-9));
    }

    @Test
    public void testGetShift_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getShift(), is(shift));
    }

    @Test
    public void testGetTithe_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getTithe(), closeTo(tithe, 1e-7));
    }
}

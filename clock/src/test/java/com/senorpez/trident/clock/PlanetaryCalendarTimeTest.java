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
                {4569.00, 1, 0.00, 591860140233.182},
                {4569.24, 1, 0.96, 591891557421.345},
                {4569.25, 2, 0.00, 591892866470.852},
                {4569.49, 2, 0.96, 591924283659.015},
                {4569.50, 3, 0.00, 591925592708.522},
                {4569.74, 3, 0.96, 591957009896.685},
                {4569.75, 4, 0.00, 591958318946.192},
                {4569.99, 4, 0.96, 591989736134.355}
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
    public void testGetShift_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getShift(), is(shift));
    }

    @Test
    public void testGetTithe() {
        assertThat(planetaryCalendar.getTithe(days), closeTo(tithe, 1e-9));
    }

    @Test
    public void testGetTithe_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getTithe(), closeTo(tithe, 1e-7));
    }
}

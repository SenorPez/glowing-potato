package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class PlanetaryCalendarTest {
    private int expectedYear;
    private int days;
    private PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        planetaryCalendar = new PlanetaryCalendar();
    }

    public PlanetaryCalendarTest(int expectedYear, int days) {
        this.expectedYear = expectedYear;
        this.days = days;
    }

    @Parameterized.Parameters(name = "days: {1}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {47, 4569},
                {48, 4668},
                {49, 4768},
                {50, 4867},
                {51, 4966},
                {52, 5065},
                {53, 5164},
                {54, 5263},
                {55, 5363}
        });
    }

    @Test
    public void testGetYear() {
        assertThat(planetaryCalendar.getYear(days), is(expectedYear));
    }
}

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
    private int localYear;
    private int days;
    private double milliseconds;
    private PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        planetaryCalendar = new PlanetaryCalendar();
    }

    public PlanetaryCalendarTest(int localYear, int days, double milliseconds) {
        this.localYear = localYear;
        this.days = days;
        this.milliseconds = milliseconds;
    }

    @Parameterized.Parameters(name = "days: {1}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {47, 4569, 598104719656.92},
                {48, 4668, 611064309774.24},
                {49, 4768, 624154804842.24},
                {50, 4867, 637114394959.56},
                {51, 4966, 650073985076.88},
                {52, 5065, 663033575194.20},
                {53, 5164, 675993165311.52},
                {54, 5263, 688952755428.84},
                {55, 5363, 702043250496.84}
        });
    }

    @Test
    public void testGetYear() {
        assertThat(planetaryCalendar.getYear(days), is(localYear));
    }

    @Test
    public void testGetLocalDays() {
        assertThat(planetaryCalendar.getLocalDays(milliseconds), is(days));
    }
}

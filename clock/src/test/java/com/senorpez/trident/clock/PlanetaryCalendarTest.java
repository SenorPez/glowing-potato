package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

@RunWith(Parameterized.class)
public class PlanetaryCalendarTest {
    private int localYear;
    private int days;
    private double planetaryMilliseconds;
    private double standardMilliseconds;
    private PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        planetaryCalendar = new PlanetaryCalendar();
    }

    public PlanetaryCalendarTest(int localYear, int days, double planetaryMilliseconds, double standardMilliseconds) {
        this.localYear = localYear;
        this.days = days;
        this.planetaryMilliseconds = planetaryMilliseconds;
        this.standardMilliseconds = standardMilliseconds;
    }

    @Parameterized.Parameters(name = "days: {1}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {47, 4569, 598104719656.92, 601067070623.45},
                {48, 4668, 611064309774.24, 614026660740.77},
                {49, 4768, 624154804842.24, 627117155808.77},
                {50, 4867, 637114394959.56, 640076745926.09},
                {51, 4966, 650073985076.88, 653036336043.41},
                {52, 5065, 663033575194.20, 665995926160.73},
                {53, 5164, 675993165311.52, 678955516278.05},
                {54, 5263, 688952755428.84, 691915106395.37},
                {55, 5363, 702043250496.84, 705005601463.37}
        });
    }

    @Test
    public void testGetLocalYear() {
        assertThat(planetaryCalendar.getLocalYear(days), is(localYear));
    }

    @Test
    public void testGetLocalDays() {
        assertThat(planetaryCalendar.getLocalDays(planetaryMilliseconds), closeTo(days, 1e-9));
    }

    @Test
    public void testGetLocalMilliseconds() {
        assertThat(planetaryCalendar.getLocalMilliseconds(standardMilliseconds), closeTo(planetaryMilliseconds, 1e-9));
    }
}

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
public class PlanetaryCalendarYearTest {
    private int localYear;
    private int days;
    private double planetaryMilliseconds;
    private double standardMilliseconds;

    @InjectMocks
    PlanetaryCalendar planetaryCalendar;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameterized.Parameters(name = "days: {1}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {47, 4569, 598104719656.92, 601067070623.448},
                {48, 4668, 611064309774.24, 614026660740.768},
                {49, 4768, 624154804842.24, 627117155808.768},
                {50, 4867, 637114394959.56, 640076745926.088},
                {51, 4966, 650073985076.88, 653036336043.408},
                {52, 5065, 663033575194.20, 665995926160.728},
                {53, 5164, 675993165311.52, 678955516278.048},
                {54, 5263, 688952755428.84, 691915106395.368},
                {55, 5363, 702043250496.84, 705005601463.368}
        });
    }

    public PlanetaryCalendarYearTest(int localYear, int days, double planetaryMilliseconds, double standardMilliseconds) {
        this.localYear = localYear;
        this.days = days;
        this.planetaryMilliseconds = planetaryMilliseconds;
        this.standardMilliseconds = standardMilliseconds;
    }

    @Test
    public void testGetLocalDays() {
        assertThat(planetaryCalendar.getLocalDays(planetaryMilliseconds), closeTo(days, 1e-9));
    }

    @Test
    public void testGetLocalDays_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getLocalDays(), closeTo(days, 1e-8));
    }

    @Test
    public void testGetLocalMilliseconds() {
        assertThat(planetaryCalendar.getLocalMilliseconds(standardMilliseconds), closeTo(planetaryMilliseconds, 1));
    }

    @Test
    public void testGetLocalMilliseconds_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getLocalMilliseconds(), closeTo(planetaryMilliseconds, 1));
    }

    @Test
    public void testGetLocalYear() {
        assertThat(planetaryCalendar.getLocalYear(days), is(localYear));
    }

    @Test
    public void testGetLocalYear_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(planetaryCalendar.getLocalYear(), is(localYear));
    }
}

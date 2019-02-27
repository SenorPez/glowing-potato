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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class PlanetaryCalendarTest {
    private int localYear;
    private int days;
    private double planetaryMilliseconds;
    private double standardMilliseconds;
    private String standardTimecode;

    @InjectMocks
    PlanetaryCalendar planetaryCalendar;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public PlanetaryCalendarTest(int localYear, int days, double planetaryMilliseconds, double standardMilliseconds, String standardTimecode) {
        this.localYear = localYear;
        this.days = days;
        this.planetaryMilliseconds = planetaryMilliseconds;
        this.standardMilliseconds = standardMilliseconds;
        this.standardTimecode = standardTimecode;
    }

    @Parameterized.Parameters(name = "days: {1}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {47, 4569, 598104719656.92, 601067070623.448, "2019-01-17T19:04:30.624Z"},
                {48, 4668, 611064309774.24, 614026660740.768, "2019-06-16T18:57:40.741Z"},
                {49, 4768, 624154804842.24, 627117155808.768, "2019-11-15T07:12:35.809Z"},
                {50, 4867, 637114394959.56, 640076745926.088, "2020-04-13T07:05:45.926Z"},
                {51, 4966, 650073985076.88, 653036336043.408, "2020-09-10T06:58:56.043Z"},
                {52, 5065, 663033575194.20, 665995926160.728, "2021-02-07T06:52:06.160Z"},
                {53, 5164, 675993165311.52, 678955516278.048, "2021-07-07T06:45:16.278Z"},
                {54, 5263, 688952755428.84, 691915106395.368, "2021-12-04T06:38:26.395Z"},
                {55, 5363, 702043250496.84, 705005601463.368, "2022-05-04T18:53:21.463Z"}
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
        assertThat(planetaryCalendar.getLocalMilliseconds(standardMilliseconds), closeTo(planetaryMilliseconds, 1));
    }

    @Test
    public void testGetLocalMilliseconds_Current() {
        when(mockClock.millis()).thenReturn((long) standardMilliseconds);
        assertThat(planetaryCalendar.getLocalMilliseconds(), closeTo(planetaryMilliseconds, 1));
    }
}

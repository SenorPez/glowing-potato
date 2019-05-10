package com.senorpez.trident.libraries;

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
public class WorkersCalendarYearTest {
    private final int localYear;
    private final int days;
    private final double planetaryMilliseconds;
    private final double standardMilliseconds;

    @InjectMocks
    WorkersCalendar workersCalendar;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameterized.Parameters(name = "days: {3}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {36.36248779296875, -72.27522277832031, 47, 4569, 598104744213.87, 591860164965.819},
                {36.36248779296875, -72.27522277832031, 48, 4668, 611064334863.28, 604819755615.234},
                {36.36248779296875, -72.27522277832031, 49, 4768, 624154830468.75, 617910251220.702},
                {36.36248779296875, -72.27522277832031, 50, 4867, 637114421118.16, 630869841870.116},
                {36.36248779296875, -72.27522277832031, 51, 4966, 650074011767.58, 643829432519.530},
                {36.36248779296875, -72.27522277832031, 52, 5065, 663033602416.99, 656789023168.944},
                {36.36248779296875, -72.27522277832031, 53, 5164, 675993193066.41, 669748613818.358},
                {36.36248779296875, -72.27522277832031, 54, 5263, 688952783715.82, 682708204467.772},
                {36.36248779296875, -72.27522277832031, 55, 5363, 702043279321.29, 695798700073.241}
        });
    }

    public WorkersCalendarYearTest(
            double standardHoursPerDay,
            double epochOffset,
            int localYear,
            int days,
            double planetaryMilliseconds,
            double standardMilliseconds) {
        this.workersCalendar = new WorkersCalendar(standardHoursPerDay, epochOffset);
        this.localYear = localYear;
        this.days = days;
        this.planetaryMilliseconds = planetaryMilliseconds;
        this.standardMilliseconds = standardMilliseconds;
    }

    @Test
    public void testGetLocalDays() {
        assertThat(workersCalendar.getLocalDays(planetaryMilliseconds), closeTo(days, 1e-9));
    }

    @Test
    public void testGetLocalDays_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(workersCalendar.getLocalDays(), closeTo(days, 1e-5));
    }

    @Test
    public void testGetLocalMilliseconds() {
        assertThat(workersCalendar.getLocalMilliseconds(standardMilliseconds), closeTo(planetaryMilliseconds, 1e-2));
    }

    @Test
    public void testGetLocalMilliseconds_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(workersCalendar.getLocalMilliseconds(), closeTo(planetaryMilliseconds, 1));
    }

    @Test
    public void testGetLocalYear() {
        assertThat(workersCalendar.getLocalYear(days), is(localYear));
    }

    @Test
    public void testGetLocalYear_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(workersCalendar.getLocalYear(), is(localYear));
    }
}

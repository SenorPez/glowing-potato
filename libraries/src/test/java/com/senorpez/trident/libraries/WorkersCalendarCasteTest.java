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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class WorkersCalendarCasteTest {
    private final double days;
    private final boolean festivalDay;
    private final int caste;
    private final int casteDay;
    private final double standardMilliseconds;

    @InjectMocks
    WorkersCalendar workersCalendar;

    @Mock
    Clock mockClock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameterized.Parameters(name = "days: {2}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {36.36248779296875, -72.27522277832031, 4569, true,  0, 0,  591860164965.819},
                {36.36248779296875, -72.27522277832031, 4570, false, 1, 1,  591991069921.874},
                {36.36248779296875, -72.27522277832031, 4588, false, 1, 19, 594347359130.859},
                {36.36248779296875, -72.27522277832031, 4589, false, 2, 1,  594478264086.913},
                {36.36248779296875, -72.27522277832031, 4608, false, 2, 20, 596965458251.952},
                {36.36248779296875, -72.27522277832031, 4609, false, 3, 1,  597096363208.007},
                {36.36248779296875, -72.27522277832031, 4628, false, 3, 20, 599583557373.046},
                {36.36248779296875, -72.27522277832031, 4629, false, 4, 1,  599714462329.101},
                {36.36248779296875, -72.27522277832031, 4648, false, 4, 20, 602201656494.140},
                {36.36248779296875, -72.27522277832031, 4649, false, 5, 1,  602332561450.194},
                {36.36248779296875, -72.27522277832031, 4667, false, 5, 19, 604688850659.179},
                {36.36248779296875, -72.27522277832031, 4668, true,  0, 0,  604819755615.234},
                {36.36248779296875, -72.27522277832031, 4669, false, 1, 1,  604950660571.288},
                {36.36248779296875, -72.27522277832031, 4687, false, 1, 19, 607306949780.273},
                {36.36248779296875, -72.27522277832031, 4688, false, 2, 1,  607437854736.327},
                {36.36248779296875, -72.27522277832031, 4707, false, 2, 20, 609925048901.366},
                {36.36248779296875, -72.27522277832031, 4708, false, 3, 1,  610055953857.421},
                {36.36248779296875, -72.27522277832031, 4717, false, 3, 10, 611234098461.913},
                {36.36248779296875, -72.27522277832031, 4718, true,  3, 0,  611365003417.968},
                {36.36248779296875, -72.27522277832031, 4719, false, 3, 11, 611495908374.023},
                {36.36248779296875, -72.27522277832031, 4728, false, 3, 20, 612674052978.515},
                {36.36248779296875, -72.27522277832031, 4729, false, 4, 1,  612804957934.569},
                {36.36248779296875, -72.27522277832031, 4748, false, 4, 20, 615292152099.609},
                {36.36248779296875, -72.27522277832031, 4749, false, 5, 1,  615423057055.663},
                {36.36248779296875, -72.27522277832031, 4767, false, 5, 19, 617779346264.648}
        });
    }

    public WorkersCalendarCasteTest(
            double standardHoursPerDay,
            double epochOffset,
            double days,
            boolean festivalDay,
            int caste,
            int casteDay,
            double standardMilliseconds) {
        this.workersCalendar = new WorkersCalendar(standardHoursPerDay, epochOffset);
        this.days = days;
        this.festivalDay = festivalDay;
        this.caste = caste;
        this.casteDay = casteDay;
        this.standardMilliseconds = standardMilliseconds;
    }

    @Test
    public void testGetCaste() {
        assertThat(workersCalendar.getCaste(days), is(caste));
    }

    @Test
    public void testGetCaste_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(workersCalendar.getCaste(), is(caste));
    }

    @Test
    public void testGetCasteDay() {
        assertThat(workersCalendar.getCasteDay(days), is(casteDay));
    }

    @Test
    public void testGetCasteDay_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(workersCalendar.getCasteDay(), is(casteDay));
    }

    @Test
    public void testIsFestivalDay() {
        assertThat(workersCalendar.isFestivalDay(days), is(festivalDay));
    }

    @Test
    public void testIsFestivalDay_Current() {
        when(mockClock.millis()).thenReturn((long) Math.ceil(standardMilliseconds));
        assertThat(workersCalendar.isFestivalDay(), is(festivalDay));
    }

}

package com.senorpez.trident.clock;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class PlanetaryClockActivityTest {
    @Mock
    PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLaunchPlanetaryCalendarActivity() {
    }
}

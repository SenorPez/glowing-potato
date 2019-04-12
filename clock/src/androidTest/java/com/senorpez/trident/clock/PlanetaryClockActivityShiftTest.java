package com.senorpez.trident.clock;

import android.content.Intent;
import androidx.lifecycle.MutableLiveData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.senorpez.trident.clock.PlanetaryClockMatchers.withProgress;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class PlanetaryClockActivityShiftTest extends PlanetaryClockActivityTestBase {
//    @Parameterized.Parameter
//    public int shiftValue = 1;
//
//    @Parameterized.Parameters(name = "shift: {0}")
//    public static Collection params() {
//        return Arrays.asList(new Object[][]{
//                {1},
//                {2},
//                {3},
//                {4}
//        });
//    }
//
//    @Test
//    public void testShiftProgress() {
//        MutableLiveData<Integer> shift = new MutableLiveData<>();
//        shift.postValue(shiftValue);
//        when(planetaryCalendarViewModel.getShift()).thenReturn(shift);
//
//        activityTestRule.launchActivity(new Intent());
//        onView(withId(R.id.prgShift)).check(matches(withProgress(this.shiftValue - 1)));
//    }
}

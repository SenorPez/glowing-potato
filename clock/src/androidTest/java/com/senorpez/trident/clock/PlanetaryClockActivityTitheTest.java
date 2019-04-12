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
public class PlanetaryClockActivityTitheTest extends PlanetaryClockActivityTestBase {
//    @Parameterized.Parameter
//    public int titheValue;
//
//    @Parameterized.Parameter(value = 1)
//    public int subtitheValue;
//
//    @Parameterized.Parameter(value = 2)
//    public int spinnerValue;
//
//    @Parameterized.Parameters(name = "tithe: {0}, subtithe: {1}, spinner: {2}")
//    public static Collection params() {
//        return Arrays.asList(new Object[][]{
//                {0, 0, 9},
//                {1, 1, 8},
//                {2, 2, 7},
//                {3, 3, 6},
//                {4, 4, 5},
//                {5, 5, 4},
//                {6, 6, 3},
//                {7, 7, 2},
//                {8, 8, 1},
//                {9, 9, 0}
//        });
//    }
//
//    @Test
//    public void testTitheProgress() {
//        MutableLiveData<Integer> tithe = new MutableLiveData<>();
//        tithe.postValue(titheValue);
//        when(planetaryCalendarViewModel.getTithe()).thenReturn(tithe);
//
//        activityTestRule.launchActivity(new Intent());
//        onView(withId(R.id.prgTithe)).check(matches(withProgress(titheValue)));
//    }
//    @Test
//    public void testSubtitheProgress() {
//        MutableLiveData<Integer> subtithe = new MutableLiveData<>();
//        subtithe.postValue(subtitheValue);
//        when(planetaryCalendarViewModel.getSubtithe()).thenReturn(subtithe);
//
//        activityTestRule.launchActivity(new Intent());
//        onView(withId(R.id.prgSubtithe)).check(matches(withProgress(subtitheValue)));
//    }
//    @Test
//    public void testSpinnerProgress() {
//        MutableLiveData<Integer> spinner = new MutableLiveData<>();
//        spinner.postValue(spinnerValue);
//        when(planetaryCalendarViewModel.getSpinner()).thenReturn(spinner);
//
//        activityTestRule.launchActivity(new Intent());
//        onView(withId(R.id.prgTicker)).check(matches(withProgress(spinnerValue)));
//    }
}

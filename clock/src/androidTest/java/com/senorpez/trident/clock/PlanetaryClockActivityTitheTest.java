package com.senorpez.trident.clock;

import android.app.Instrumentation;
import android.content.Intent;
import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.senorpez.trident.clock.PlanetaryClockMatchers.withProgress;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class PlanetaryClockActivityTitheTest {
    @Parameterized.Parameter
    public int titheValue;

    @Parameterized.Parameter(value = 1)
    public int subtitheValue;

    @Parameterized.Parameter(value = 2)
    public int spinnerValue;

    private final static int shiftValue = 1;
    private final static String localDateTimeValue = "Local Date Time";
    private final static String standardDateTimeValue = "Standard Date Time";

    @Rule
    public ActivityTestRule<PlanetaryClockActivity> activityTestRule = new ActivityTestRule<>(PlanetaryClockActivity.class, true, false);

    @Mock
    PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Mock
    PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(planetaryCalendarViewModelFactory.create(any())).thenReturn(planetaryCalendarViewModel);

        MutableLiveData<Integer> shift = new MutableLiveData<>();
        shift.postValue(shiftValue);

        MutableLiveData<Integer> tithe = new MutableLiveData<>();
        tithe.postValue(titheValue);

        MutableLiveData<Integer> subtithe = new MutableLiveData<>();
        subtithe.postValue(subtitheValue);

        MutableLiveData<Integer> spinner = new MutableLiveData<>();
        spinner.postValue(spinnerValue);

        MutableLiveData<String> localDateTime = new MutableLiveData<>();
        localDateTime.postValue(localDateTimeValue);

        MutableLiveData<String> standardDateTime = new MutableLiveData<>();
        standardDateTime.postValue(standardDateTimeValue);

        when(planetaryCalendarViewModel.getShift()).thenReturn(shift);
        when(planetaryCalendarViewModel.getTithe()).thenReturn(tithe);
        when(planetaryCalendarViewModel.getSubtithe()).thenReturn(subtithe);
        when(planetaryCalendarViewModel.getSpinner()).thenReturn(spinner);
        when(planetaryCalendarViewModel.getLocalDateTime()).thenReturn(localDateTime);
        when(planetaryCalendarViewModel.getStandardDateTime()).thenReturn(standardDateTime);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        PlanetaryClockTestApplication application = (PlanetaryClockTestApplication) instrumentation.getTargetContext().getApplicationContext();
        DaggerPlanetaryClockTestApplicationComponent
                .builder()
                .activityModule(new PlanetaryClockTestActivityModule(planetaryCalendarViewModelFactory))
                .create(application)
                .inject(application);
    }

    @Parameterized.Parameters(name = "tithe: {0}, subtithe: {1}, spinner: {2}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {0, 0, 9},
                {1, 1, 8},
                {2, 2, 7},
                {3, 3, 6},
                {4, 4, 5},
                {5, 5, 4},
                {6, 6, 3},
                {7, 7, 2},
                {8, 8, 1},
                {9, 9, 0}
        });
    }

    @Test
    public void testTitheProgress() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.prgTithe)).check(matches(withProgress(titheValue)));
    }
    @Test
    public void testSubtitheProgress() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.prgSubtithe)).check(matches(withProgress(subtitheValue)));
    }
    @Test
    public void testSpinnerProgress() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.prgTicker)).check(matches(withProgress(spinnerValue)));
    }
}

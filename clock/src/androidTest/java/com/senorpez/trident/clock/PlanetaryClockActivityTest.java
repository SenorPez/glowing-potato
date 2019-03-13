package com.senorpez.trident.clock;

import android.app.Instrumentation;
import android.content.Intent;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class PlanetaryClockActivityTest {
    private final static int shiftValue = 2;
    private final static int titheValue = 3;
    private final static int subtitheValue = 2;
    private final static int spinnerValue = 9;
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

    @Test
    public void testLocalDateTime() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.tavenTime)).check(matches(withText(localDateTimeValue)));
    }

    @Test
    public void testStandardDateTime() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.standardTime)).check(matches(withText(standardDateTimeValue)));
    }
}

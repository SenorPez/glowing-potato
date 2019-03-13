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
public class PlanetaryClockActivityShiftTest {
    @Parameterized.Parameter
    public int shiftValue;

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

    @Parameterized.Parameters(name = "shift: {0}")
    public static Collection params() {
        return Arrays.asList(new Object[][]{
                {1},
                {2},
                {3},
                {4}
        });
    }

    @Test
    public void testShiftProgress() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.prgShift)).check(matches(withProgress(shiftValue - 1)));
    }
}

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class PlanetaryClockActivityTest {
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
        MutableLiveData<Integer> outputData = new MutableLiveData<>();
        outputData.postValue(42);

        MutableLiveData<String> stringData = new MutableLiveData<>();
        stringData.postValue("Test");
        when(planetaryCalendarViewModel.getShift()).thenReturn(outputData);
        when(planetaryCalendarViewModel.getTithe()).thenReturn(outputData);
        when(planetaryCalendarViewModel.getSubtithe()).thenReturn(outputData);
        when(planetaryCalendarViewModel.getSpinner()).thenReturn(outputData);
        when(planetaryCalendarViewModel.getLocalDateTime()).thenReturn(stringData);
        when(planetaryCalendarViewModel.getStandardDateTime()).thenReturn(stringData);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        PlanetaryClockTestApplication application = (PlanetaryClockTestApplication) instrumentation.getTargetContext().getApplicationContext();
        DaggerPlanetaryClockTestApplicationComponent
                .builder()
                .activityModule(new PlanetaryClockTestActivityModule(planetaryCalendarViewModelFactory))
                .create(application)
                .inject(application);
    }

    @Test
    public void testTrivial() {
        activityTestRule.launchActivity(new Intent());
        assertThat(4, is(4));
    }
}

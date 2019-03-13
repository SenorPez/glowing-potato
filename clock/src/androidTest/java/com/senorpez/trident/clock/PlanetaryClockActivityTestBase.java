package com.senorpez.trident.clock;

import android.app.Instrumentation;

import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PlanetaryClockActivityTestBase {
    @Rule
    public ActivityTestRule<PlanetaryClockActivity> activityTestRule = new ActivityTestRule<>(PlanetaryClockActivity.class, true, false);

    final String localDateTimeValue = "Local Date Time";
    final String standardDateTimeValue = "Standard Date Time";

    @Mock
    PlanetaryCalendarViewModel planetaryCalendarViewModel;
    @Mock
    PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(planetaryCalendarViewModelFactory.create(any())).thenReturn(planetaryCalendarViewModel);

        MutableLiveData<Integer> shift = new MutableLiveData<>();
        shift.postValue(1);
        when(planetaryCalendarViewModel.getShift()).thenReturn(shift);

        MutableLiveData<Integer> tithe = new MutableLiveData<>();
        tithe.postValue(3);
        when(planetaryCalendarViewModel.getTithe()).thenReturn(tithe);

        MutableLiveData<Integer> subtithe = new MutableLiveData<>();
        subtithe.postValue(2);
        when(planetaryCalendarViewModel.getSubtithe()).thenReturn(subtithe);

        MutableLiveData<Integer> spinner = new MutableLiveData<>();
        spinner.postValue(9);
        when(planetaryCalendarViewModel.getSpinner()).thenReturn(spinner);

        MutableLiveData<String> localDateTime = new MutableLiveData<>();
        localDateTime.postValue(localDateTimeValue);
        when(planetaryCalendarViewModel.getLocalDateTime()).thenReturn(localDateTime);

        MutableLiveData<String> standardDateTime = new MutableLiveData<>();
        standardDateTime.postValue(standardDateTimeValue);
        when(planetaryCalendarViewModel.getStandardDateTime()).thenReturn(standardDateTime);

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        PlanetaryClockTestApplication application = (PlanetaryClockTestApplication) instrumentation.getTargetContext().getApplicationContext();
        DaggerPlanetaryClockTestApplicationComponent
                .builder()
                .activityModule(new PlanetaryClockTestActivityModule(planetaryCalendarViewModelFactory))
                .create(application)
                .inject(application);
    }
}

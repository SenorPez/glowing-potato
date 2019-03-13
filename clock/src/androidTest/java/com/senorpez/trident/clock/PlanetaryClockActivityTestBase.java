package com.senorpez.trident.clock;

import android.app.Instrumentation;
import androidx.lifecycle.MutableLiveData;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PlanetaryClockActivityTestBase {
    @Rule
    public ActivityTestRule<PlanetaryClockActivity> activityTestRule = new ActivityTestRule<>(PlanetaryClockActivity.class, true, false);

    public int shiftValue = 1;
    public int titheValue = 3;
    public int subtitheValue = 2;
    public int spinnerValue = 9;

    public String localDateTimeValue = "Local Date Time";
    public String standardDateTimeValue = "Standard Date Time";

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
        when(planetaryCalendarViewModel.getShift()).thenReturn(shift);

        MutableLiveData<Integer> tithe = new MutableLiveData<>();
        tithe.postValue(titheValue);
        when(planetaryCalendarViewModel.getTithe()).thenReturn(tithe);

        MutableLiveData<Integer> subtithe = new MutableLiveData<>();
        subtithe.postValue(subtitheValue);
        when(planetaryCalendarViewModel.getSubtithe()).thenReturn(subtithe);

        MutableLiveData<Integer> spinner = new MutableLiveData<>();
        spinner.postValue(spinnerValue);
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

package com.senorpez.trident.clock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.senorpez.trident.libraries.WorkersCalendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PlanetaryCalendarRepositoryTest {
    @Mock
    TridentAPI mockAPI;

    @Mock
    Call<WorkersCalendar> mockCall;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private PlanetaryCalendarRepository planetaryCalendarRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        planetaryCalendarRepository = new PlanetaryCalendarRepository(mockAPI);
    }

    @Test
    public void testGetPlanetaryCalendar() {
        when(mockAPI.getPlanetaryCalendarData()).thenReturn(mockCall);
        WorkersCalendar planetaryCalendarExpected = new WorkersCalendar();

        doAnswer(invocation -> {
            Callback<WorkersCalendar> callback = invocation.getArgument(0);
            callback.onResponse(mockCall, Response.success(planetaryCalendarExpected));
            return null;
        }).when(mockCall).enqueue(any());

        assertThat(planetaryCalendarRepository.getPlanetaryCalendar(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarRepository.getPlanetaryCalendar().getValue(), is(planetaryCalendarExpected));
        assertThat(planetaryCalendarRepository.getCalendarCache().getValue(), is(planetaryCalendarExpected));

        verify(mockAPI).getPlanetaryCalendarData();
        verifyNoMoreInteractions(mockAPI);
    }

//    @Test(expected = IOException.class)
//    public void testGetPlanetaryCalendar_Failure() {
//        when(mockAPI.getPlanetaryCalendarData()).thenReturn(mockCall);
//        doAnswer(invocation -> {
//            Callback<WorkersCalendar> callback = invocation.getArgument(0);
//            callback.onFailure(mockCall, new IOException());
//            return null;
//        }).when(mockCall).enqueue(any());
//
//        planetaryCalendarRepository.getPlanetaryCalendar();
//    }

    @Test
    public void testGetPlanetaryCalendar_Cached() {
        LiveData<WorkersCalendar> planetaryCalendarLiveData = new MutableLiveData<>();
        planetaryCalendarRepository.setCalendarCache(planetaryCalendarLiveData);

        assertThat(planetaryCalendarRepository.getPlanetaryCalendar(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarRepository.getPlanetaryCalendar(), is(planetaryCalendarLiveData));

        verifyZeroInteractions(mockAPI);
    }
}

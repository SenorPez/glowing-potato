package com.senorpez.trident.clock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PlanetaryCalendarViewModelTest {
    @Mock
    PlanetaryCalendarRepository planetaryCalendarRepository;

    @Mock
    PlanetaryCalendar planetaryCalendar;

    private PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        MutableLiveData<PlanetaryCalendar> planetaryCalendarLiveData = new MutableLiveData<>();
        planetaryCalendarLiveData.postValue(planetaryCalendar);
        when(planetaryCalendarRepository.getPlanetaryCalendar()).thenReturn(planetaryCalendarLiveData);
        PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory = new PlanetaryCalendarViewModelFactory(planetaryCalendarRepository);
        planetaryCalendarViewModel = planetaryCalendarViewModelFactory.create(PlanetaryCalendarViewModel.class);
    }

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Test
    public void testGetShift() {
        when(planetaryCalendar.getShift()).thenReturn(1);
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getShift()).update();

        assertThat(planetaryCalendarViewModel.getShift(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getShift().getValue(), is(0));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getShift();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetTithe() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getTithe()).update();

        assertThat(planetaryCalendarViewModel.getTithe(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getTithe().getValue(), is(3));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetSubTithe() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSubTithe()).update();

        assertThat(planetaryCalendarViewModel.getSubTithe(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getSubTithe().getValue(), is(2));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetSpinner() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSpinner()).update();

        assertThat(planetaryCalendarViewModel.getSpinner(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getSpinner().getValue(), is(9));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetLocalDateTime_Regular() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(2);
        when(planetaryCalendar.getCasteDay()).thenReturn(15);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);

        ((ClockLiveData<String>) planetaryCalendarViewModel.getLocalDateTime()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getShift()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getTithe()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSubTithe()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSpinner()).update();

        assertThat(planetaryCalendarViewModel.getLocalDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getLocalDateTime().getValue(), is("35 FY 2 Caste 15 Day 3.32 Shift"));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getLocalYear();
        verify(planetaryCalendar).getCaste();
        verify(planetaryCalendar, times(3)).getCasteDay();
        verify(planetaryCalendar, times(2)).getShift();
        verify(planetaryCalendar, times(4)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetLocalDateTime_FestivalDay() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(0);
        when(planetaryCalendar.getCasteDay()).thenReturn(0);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);

        ((ClockLiveData<String>) planetaryCalendarViewModel.getLocalDateTime()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getShift()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getTithe()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSubTithe()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSpinner()).update();

        assertThat(planetaryCalendarViewModel.getLocalDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getLocalDateTime().getValue(), is("35 FY Festival Day 3.32 Shift"));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getLocalYear();
        verify(planetaryCalendar).getCaste();
        verify(planetaryCalendar).getCasteDay();
        verify(planetaryCalendar, times(2)).getShift();
        verify(planetaryCalendar, times(4)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetLocalDateTime_MidfestivalDay() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(3);
        when(planetaryCalendar.getCasteDay()).thenReturn(0);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);

        ((ClockLiveData<String>) planetaryCalendarViewModel.getLocalDateTime()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getShift()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getTithe()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSubTithe()).update();
        ((ClockLiveData<Integer>) planetaryCalendarViewModel.getSpinner()).update();

        assertThat(planetaryCalendarViewModel.getLocalDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getLocalDateTime().getValue(), is("35 FY Midfestival Day 3.32 Shift"));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verify(planetaryCalendar).getLocalYear();
        verify(planetaryCalendar, times(2)).getCaste();
        verify(planetaryCalendar, times(2)).getCasteDay();
        verify(planetaryCalendar, times(2)).getShift();
        verify(planetaryCalendar, times(4)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetStandardDateTime() {
        ((ClockLiveData<String>) planetaryCalendarViewModel.getStandardDateTime()).update();

        assertThat(planetaryCalendarViewModel.getStandardDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getStandardDateTime().getValue(), is(notNullValue()));

        verify(planetaryCalendarRepository).getPlanetaryCalendar();
        verifyNoMoreInteractions(planetaryCalendarRepository);

        verifyZeroInteractions(planetaryCalendar);
    }
}

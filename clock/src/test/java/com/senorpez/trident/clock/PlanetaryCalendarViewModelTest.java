package com.senorpez.trident.clock;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class PlanetaryCalendarViewModelTest {
    private PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Mock
    PlanetaryCalendar planetaryCalendar;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory = new PlanetaryCalendarViewModelFactory(planetaryCalendar);
        planetaryCalendarViewModel = planetaryCalendarViewModelFactory.create(PlanetaryCalendarViewModel.class);
    }

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Test
    public void testGetShift() {
        when(planetaryCalendar.getShift()).thenReturn(1);
        assertThat(planetaryCalendarViewModel.getShift(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getShift().getValue(), is(1));
        verify(planetaryCalendar, times(2)).getShift();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetTithe() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getTithe(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getTithe().getValue(), is(3));
        verify(planetaryCalendar, times(2)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetSubtithe() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getSubTithe(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getSubTithe().getValue(), is(2));
        verify(planetaryCalendar, times(2)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetSpinner() {
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getSpinner(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getSpinner().getValue(), is(9));
        verify(planetaryCalendar, times(2)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetLocalDateTime_Regular() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(2);
        when(planetaryCalendar.getCasteDay()).thenReturn(15);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getLocalDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getLocalDateTime().getValue(), is("35 FY 2 Caste 15 Day 3.32 Shift"));
        verify(planetaryCalendar, times(2)).getLocalYear();
        verify(planetaryCalendar, times(2)).getCaste();
        verify(planetaryCalendar, times(4)).getCasteDay();
        verify(planetaryCalendar, times(2)).getShift();
        verify(planetaryCalendar, times(2)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetLocalDateTime_FestivalDay() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(0);
        when(planetaryCalendar.getCasteDay()).thenReturn(0);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getLocalDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getLocalDateTime().getValue(), is("35 FY Festival Day 3.32 Shift"));
        verify(planetaryCalendar, times(2)).getLocalYear();
        verify(planetaryCalendar, times(2)).getCaste();
        verify(planetaryCalendar, times(2)).getCasteDay();
        verify(planetaryCalendar, times(2)).getShift();
        verify(planetaryCalendar, times(2)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetLocalDateTime_MidfestivalDay() {
        when(planetaryCalendar.getLocalYear()).thenReturn(35);
        when(planetaryCalendar.getCaste()).thenReturn(3);
        when(planetaryCalendar.getCasteDay()).thenReturn(0);
        when(planetaryCalendar.getShift()).thenReturn(3);
        when(planetaryCalendar.getTithe()).thenReturn(0.329);
        assertThat(planetaryCalendarViewModel.getLocalDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getLocalDateTime().getValue(), is("35 FY Midfestival Day 3.32 Shift"));
        verify(planetaryCalendar, times(2)).getLocalYear();
        verify(planetaryCalendar, times(4)).getCaste();
        verify(planetaryCalendar, times(2)).getCasteDay();
        verify(planetaryCalendar, times(2)).getShift();
        verify(planetaryCalendar, times(2)).getTithe();
        verifyNoMoreInteractions(planetaryCalendar);
    }

    @Test
    public void testGetStandardDateTime() {
        assertThat(planetaryCalendarViewModel.getStandardDateTime(), instanceOf(LiveData.class));
        assertThat(planetaryCalendarViewModel.getStandardDateTime().getValue(), is(notNullValue()));
        verifyZeroInteractions(planetaryCalendar);
    }

}

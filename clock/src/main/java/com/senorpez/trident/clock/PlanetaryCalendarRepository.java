package com.senorpez.trident.clock;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.senorpez.trident.libraries.WorkersCalendar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class PlanetaryCalendarRepository {
    private final TridentAPI tridentAPI;
    private LiveData<WorkersCalendar> calendarCache;

    @Inject
    PlanetaryCalendarRepository(TridentAPI tridentAPI) {
        this.tridentAPI = tridentAPI;
    }

    LiveData<WorkersCalendar> getPlanetaryCalendar() {
        if (calendarCache != null) {
            return calendarCache;
        }

        final MutableLiveData<WorkersCalendar> calendar = new MutableLiveData<>();

        tridentAPI.getPlanetaryCalendarData().enqueue(new Callback<WorkersCalendar>() {
            @Override
            public void onResponse(Call<WorkersCalendar> call, Response<WorkersCalendar> response) {
                calendar.setValue(response.body());
                calendarCache = calendar;
            }

            @Override
            public void onFailure(Call<WorkersCalendar> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return calendar;
    }

    LiveData<WorkersCalendar> getCalendarCache() {
        return calendarCache;
    }

    void setCalendarCache(LiveData<WorkersCalendar> calendarCache) {
        this.calendarCache = calendarCache;
    }
}

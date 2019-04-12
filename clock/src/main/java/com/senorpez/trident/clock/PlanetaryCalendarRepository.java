package com.senorpez.trident.clock;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
class PlanetaryCalendarRepository {
    private final TridentAPI tridentAPI;
    private LiveData<PlanetaryCalendar> calendarCache;

    @Inject
    PlanetaryCalendarRepository(TridentAPI tridentAPI) {
        this.tridentAPI = tridentAPI;
    }

    LiveData<PlanetaryCalendar> getPlanetaryCalendar() {
        if (calendarCache != null) {
            return calendarCache;
        }

        final MutableLiveData<PlanetaryCalendar> calendar = new MutableLiveData<>();

        tridentAPI.getPlanetaryCalendarData().enqueue(new Callback<PlanetaryCalendar>() {
            @Override
            public void onResponse(Call<PlanetaryCalendar> call, Response<PlanetaryCalendar> response) {
                calendar.setValue(response.body());
                calendarCache = calendar;
            }

            @Override
            public void onFailure(Call<PlanetaryCalendar> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return calendar;
    }

    LiveData<PlanetaryCalendar> getCalendarCache() {
        return calendarCache;
    }

    void setCalendarCache(LiveData<PlanetaryCalendar> calendarCache) {
        this.calendarCache = calendarCache;
    }
}

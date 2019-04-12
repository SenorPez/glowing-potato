package com.senorpez.trident.clock;

import retrofit2.Call;
import retrofit2.http.GET;

interface TridentAPI {
    @GET("systems/1817514095/stars/1905216634/planets/-455609026/calendars/-1010689347")
    Call<PlanetaryCalendar> getPlanetaryCalendarData();
}

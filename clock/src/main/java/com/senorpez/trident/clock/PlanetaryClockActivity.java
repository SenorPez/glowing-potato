package com.senorpez.trident.clock;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.time.*;

public class PlanetaryClockActivity extends AppCompatActivity {
    private PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcalendar);

        planetaryCalendarViewModel = ViewModelProviders.of(this).get(PlanetaryCalendarViewModel.class);
        Clock clockJ2000 = Clock.offset(
                Clock.systemUTC(),
                Duration.ofMillis(
                        Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1));
        planetaryCalendarViewModel.init(new PlanetaryCalendar(clockJ2000));
    }
}

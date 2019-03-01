package com.senorpez.trident.clock;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class PlanetaryClockActivity extends AppCompatActivity {
    private PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcalendar);
        planetaryCalendarViewModel = ViewModelProviders.of(this).get(PlanetaryCalendarViewModel.class);
    }
}

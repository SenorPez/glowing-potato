package com.senorpez.trident.clock;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class PlanetaryClockActivity extends AppCompatActivity {
    private PlanetaryCalendarViewModel planetaryCalendarViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcalendar);
        planetaryCalendarViewModel = ViewModelProviders.of(this).get(PlanetaryCalendarViewModel.class);
    }
}

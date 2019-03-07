package com.senorpez.trident.clock;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;

import javax.inject.Inject;

public class PlanetaryClockActivity extends AppCompatActivity {
    @Inject
    PlanetaryCalendarViewModelFactory planetaryCalendarViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
//        DaggerPlanetaryClockComponent.builder()
//                .planetaryCalendarViewModelFactoryModule(new PlanetaryCalendarViewModelFactoryModule())
//                .build()
//                .inject(this);
//        super.onCreate(savedInstanceState);
//
        setContentView(R.layout.activity_pcalendar);
        PlanetaryCalendarViewModel planetaryCalendarViewModel =
                ViewModelProviders.of(this, planetaryCalendarViewModelFactory).get(PlanetaryCalendarViewModel.class);

        planetaryCalendarViewModel.getShift().observe(this, shift -> {
            ProgressBar progressShift = this.findViewById(R.id.prgShift);
            progressShift.setProgress(shift - 1);
        });
        planetaryCalendarViewModel.getTithe().observe(this, tithe -> {
            ProgressBar progressTithe = this.findViewById(R.id.prgTithe);
            progressTithe.setProgress(tithe);
        });
        planetaryCalendarViewModel.getSubtithe().observe(this, subTithe -> {
            ProgressBar progressSubTithe = this.findViewById(R.id.prgSubtithe);
            progressSubTithe.setProgress(subTithe);
        });
        planetaryCalendarViewModel.getSpinner().observe(this, spinner -> {
            ProgressBar progressSpinner = this.findViewById(R.id.prgTicker);
            progressSpinner.setProgress(spinner);
        });
        planetaryCalendarViewModel.getLocalDateTime().observe(this, localDateTime -> {
            TextView textLocalDateTime = this.findViewById(R.id.tavenTime);
            textLocalDateTime.setText(localDateTime);
        });
        planetaryCalendarViewModel.getStandardDateTime().observe(this, standardDateTime -> {
            TextView textStandardDateTime = this.findViewById(R.id.standardTime);
            textStandardDateTime.setText(standardDateTime);
        });
    }
}

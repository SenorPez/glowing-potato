package com.senorpez.trident.clock;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.time.*;

public class PlanetaryClockActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcalendar);

        PlanetaryCalendarViewModel planetaryCalendarViewModel = ViewModelProviders.of(this).get(PlanetaryCalendarViewModel.class);
        Clock clockJ2000 = Clock.offset(
                Clock.systemUTC(),
                Duration.ofMillis(
                        Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1));
        planetaryCalendarViewModel.init(new PlanetaryCalendar(clockJ2000));

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

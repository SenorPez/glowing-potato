package com.senorpez.trident.clock;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.time.*;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView standardTime = this.findViewById(R.id.standardTime);
        standardTime.setText(String.format("%s", Clock.systemUTC().instant()));

        Clock clock = Clock.offset(
                Clock.systemUTC(),
                Duration.ofMillis(
                        Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1));

        PlanetaryCalendar tavenCalendar = new PlanetaryCalendar(clock);
        TextView tavenTime = this.findViewById(R.id.tavenTime);

        tavenTime.setText(String.format(
                Locale.US,
                "%d FY %d Caste %d Day %d.%d Shift",
                tavenCalendar.getLocalYear(),
                tavenCalendar.getCaste(),
                tavenCalendar.getCasteDay(),
                tavenCalendar.getShift(),
                (int) Math.floor(tavenCalendar.getTithe() * 100)));

        ProgressBar progressShift = this.findViewById(R.id.prgShift);
        progressShift.setProgress(tavenCalendar.getShift() - 1);

        ProgressBar progressTithe = this.findViewById(R.id.prgTithe);
        progressTithe.setProgress((int) Math.floor(tavenCalendar.getTithe() * 10));

        ProgressBar progressSubtithe = this.findViewById(R.id.prgSubtithe);
        progressSubtithe.setProgress((int) (Math.floor(tavenCalendar.getTithe() * 100)) % 10);

        ProgressBar progressTicker = this.findViewById(R.id.prgTicker);
        progressTicker.setProgress((int) (Math.floor(tavenCalendar.getTithe() * 1000)) % 10);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                standardTime.setText(String.format("%s", Clock.systemUTC().instant()));
                tavenTime.setText(String.format(
                        Locale.US,
                        "%d FY %d Caste %d Day %d.%d Shift",
                        tavenCalendar.getLocalYear(),
                        tavenCalendar.getCaste(),
                        tavenCalendar.getCasteDay(),
                        tavenCalendar.getShift(),
                        (int) Math.floor(tavenCalendar.getTithe() * 100)));
                handler.postDelayed(this, 500);
                progressShift.setProgress(tavenCalendar.getShift() - 1);
                progressTithe.setProgress((int) Math.floor(tavenCalendar.getTithe() * 10));
                progressSubtithe.setProgress((int) (Math.floor(tavenCalendar.getTithe() * 100)) % 10);
                progressTicker.setProgress((int) (Math.floor(tavenCalendar.getTithe() * 1000)) % 10);
            }
        };
        handler.post(runnable);
    }
}

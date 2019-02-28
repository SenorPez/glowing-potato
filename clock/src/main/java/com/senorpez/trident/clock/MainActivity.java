package com.senorpez.trident.clock;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView standardTime;
    private TextView tavenTime;

    private ProgressBar progressShift;
    private ProgressBar progressTithe;
    private ProgressBar progressSubtithe;
    private ProgressBar progressTicker;

    private Clock clockJ2000 = Clock.offset(
            Clock.systemUTC(),
            Duration.ofMillis(
                    Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1));
    private PlanetaryCalendar tavenCalendar = new PlanetaryCalendar(clockJ2000);

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy G MMM d HH:mm:ss", Locale.US);

            try {
                Date date = inputFormat.parse(String.format("%s", Clock.systemUTC().instant()));
                standardTime.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tavenTime.setText(String.format(
                    Locale.US,
                    "%d FY %d Caste %d Day %d.%02d Shift",
                    tavenCalendar.getLocalYear(),
                    tavenCalendar.getCaste(),
                    tavenCalendar.getCasteDay(),
                    tavenCalendar.getShift(),
                    (int) Math.floor(tavenCalendar.getTithe() * 100)));
            progressShift.setProgress(tavenCalendar.getShift() - 1);
            progressTithe.setProgress((int) Math.floor(tavenCalendar.getTithe() * 10));
            progressSubtithe.setProgress((int) (Math.floor(tavenCalendar.getTithe() * 100)) % 10);
            progressTicker.setProgress((int) (Math.floor(tavenCalendar.getTithe() * 1000)) % 10);
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("onCreate");

        standardTime = this.findViewById(R.id.standardTime);
        tavenTime = this.findViewById(R.id.tavenTime);

        progressShift = this.findViewById(R.id.prgShift);
        progressTithe = this.findViewById(R.id.prgTithe);
        progressSubtithe = this.findViewById(R.id.prgSubtithe);
        progressTicker = this.findViewById(R.id.prgTicker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}

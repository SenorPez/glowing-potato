package com.senorpez.trident.clock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DateFormat;
import java.time.*;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = new Date();
        TextView standardTime = this.findViewById(R.id.standardTime);
        standardTime.setText(DateFormat.getDateTimeInstance().format(date));

        Clock clock = Clock.offset(
                Clock.systemUTC(),
                Duration.ofMillis(Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1)
        );
        PlanetaryCalendar tavenCalendar = new PlanetaryCalendar(clock);
        TextView tavenTime = this.findViewById(R.id.tavenTime);
        tavenTime.setText(String.format(Locale.US, "%d FY", tavenCalendar.getLocalYear()));
    }
}

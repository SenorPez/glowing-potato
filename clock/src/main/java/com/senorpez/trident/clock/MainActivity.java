package com.senorpez.trident.clock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date date = new Date();
        TextView currentTime = this.findViewById(R.id.standardTime);
        currentTime.setText(DateFormat.getDateTimeInstance().format(date));

        TextView tavenTime = this.findViewById(R.id.tavenTime);
        tavenTime.setText(String.format(Locale.US, "%d FY", new TavenTime().getYear()));

//        ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
//        TavenTime tavenTime = new TavenTime();
//        progressBar.setProgress(tavenTime.getYear());
//        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 400);
//        animator.setDuration(5000);
//        animator.setInterpolator(new DecelerateInterpolator());
    }


}

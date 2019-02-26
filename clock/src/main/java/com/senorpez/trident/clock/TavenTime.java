package com.senorpez.trident.clock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

class TavenTime {
    private static long timeEpochTemp;
    static {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss Z", Locale.US);
            timeEpochTemp = format.parse("01-01-2000 00:00:00 +0000").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // TODO: API calls with offline fallback.
    private final static float OFFSET = (float) -34.28646951536321;
    private final static float HOURS_PER_LOCAL_DAY = (float) 36.362486;
    private final static float TIME_EPOCH = timeEpochTemp;

    private int year = 1;

    TavenTime() {
        float timeNow = System.currentTimeMillis() - OFFSET * 86400000;
        float timeDelta = timeNow - TIME_EPOCH;
        float hours = timeDelta / 3600000;
        float localDays = hours / HOURS_PER_LOCAL_DAY;

        while (true) {
            if (year % 3 != 0) {
                if (localDays > 99) {
                    year += 1;
                    localDays -= 99;
                } else {
                    break;
                }
            } else {
                if (localDays > 100) {
                    year += 1;
                    localDays -= 100;
                } else {
                    break;
                }
            }
        }
    }

    int getYear() {
        return year;
    }
}

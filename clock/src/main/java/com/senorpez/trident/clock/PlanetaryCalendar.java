package com.senorpez.trident.clock;

class PlanetaryCalendar {
    // TODO: API integration with cache fallback.
    private static final double STD_HOURS_PER_DAY = 36.3624863;
    private static final double EPOCH_OFFSET = -34.28646952;

    int getLocalYear(double localDays) {
        int year = 1;
        while (localDays >= getDaysInYear(year)) {
            localDays -= getDaysInYear(year);
            year += 1;
        }
        return year;
    }

    double getLocalDays(final double milliseconds) {
        final double hours = milliseconds / 3600000;
        return hours / STD_HOURS_PER_DAY;
    }

    private int getDaysInYear(final int year) {
        return year % 3 == 0 && year % 51 != 0 ? 100 : 99;
    }

    double getLocalMilliseconds(final double standardMilliseconds) {
        return standardMilliseconds + EPOCH_OFFSET * 86400000;
    }
}

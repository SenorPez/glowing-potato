package com.senorpez.trident.clock;

class PlanetaryCalendar {
    // TODO: API integration with cache fallback.
    private static final double STD_HOURS_PER_DAY = 36.3624863;

    int getYear(int localDays) {
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
}

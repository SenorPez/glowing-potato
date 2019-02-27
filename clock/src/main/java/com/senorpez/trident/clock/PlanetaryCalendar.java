package com.senorpez.trident.clock;

class PlanetaryCalendar {
    int getYear(int localDays) {
        int year = 1;
        while (localDays >= getDaysInYear(year)) {
            localDays -= getDaysInYear(year);
            year += 1;
        }
        return year;
    }

    double getLocalDays(final double milliseconds) {
        return -1;
    }

    private int getDaysInYear(final int year) {
        return year % 3 == 0 && year % 51 != 0 ? 100 : 99;
    }
}

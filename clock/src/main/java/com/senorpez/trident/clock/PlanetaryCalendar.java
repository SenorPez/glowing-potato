package com.senorpez.trident.clock;

import java.time.Clock;

class PlanetaryCalendar {
    // TODO: API integration with cache fallback.
    private static final double STD_HOURS_PER_DAY = 36.3624863;
    private static final double EPOCH_OFFSET = -34.28646952;

    private final Clock clock;

    PlanetaryCalendar(Clock clock) {
        this.clock = clock;
    }

    double getLocalDays() {
        return getLocalDays(getLocalMilliseconds(clock.millis()));
    }

    double getLocalDays(final double milliseconds) {
        final double hours = milliseconds / 3600000;
        return hours / STD_HOURS_PER_DAY;
    }

    double getLocalMilliseconds() {
        return getLocalMilliseconds(clock.millis());
    }

    double getLocalMilliseconds(final double standardMilliseconds) {
        return standardMilliseconds + EPOCH_OFFSET * 86400000;
    }

    int getLocalYear() {
        double localMilliseconds = getLocalMilliseconds(clock.millis());
        double localDays = getLocalDays(localMilliseconds);
        return getLocalYear(localDays);
    }

    int getLocalYear(double localDays) {
        int year = 1;
        while (localDays >= getDaysInYear(year)) {
            localDays -= getDaysInYear(year);
            year += 1;
        }
        return year;
    }

    private int getDaysInYear(final int year) {
        return year % 3 == 0 && year % 51 != 0 ? 100 : 99;
    }

    int getCaste(double days) {
        int year = getLocalYear(days) - 1;
        days -= year * 99 + Math.floorDiv(year, 3) - Math.floorDiv(year, 51);
        if (days < 1) {
            return 0;
        } else if (days < 20) {
            return 1;
        } else if (days < 40) {
            return 2;
        } else if (!isFestivalYear(year)) {
            if (days < 60) {
                return 3;
            } else if (days < 80) {
                return 4;
            } else if (days < 99) {
                return 5;
            } else {
                // TODO: Throw error instead of a sentinel.
                return -1;
            }
        } else {
            if (days < 61) {
                return 3;
            } else if (days < 81) {
                return 4;
            } else if (days < 100) {
                return 5;
            } else {
                // TODO: Throw error instead of a sentinel.
                return -1;
            }
        }
    }

    private boolean isFestivalYear(final int year) {
        return year % 3 == 0 && year % 51 != 0;
    }

    boolean isFestivalDay(double days) {
        int year = getLocalYear(days);
        double currentDays = removeYearDays(year, days);
        if (currentDays < 1) {
            return true;
        } else {
            return isFestivalYear(year) && currentDays >= 50 && currentDays < 51;
        }
    }

    private double removeYearDays(int year, double days) {
        year -= 1;
        return days - (year * 99 + Math.floorDiv(year, 3) - Math.floorDiv(year, 51));
    }
}

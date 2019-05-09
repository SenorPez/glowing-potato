package com.senorpez.trident.libraries;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.*;

public class WorkersCalendar {
    private double standardHoursPerDay;
    private double epochOffset;
    private Clock clock;

    WorkersCalendar(double standardHoursPerDay, double epochOffset) {
        this.standardHoursPerDay = standardHoursPerDay;
        this.epochOffset = epochOffset;
    }

    @JsonCreator
    WorkersCalendar() {
        this.clock = Clock.offset(
                Clock.systemUTC(),
                Duration.ofMillis(
                        Clock.fixed(Instant.parse("2000-01-01T00:00:00Z"), ZoneId.ofOffset("GMT", ZoneOffset.UTC)).millis() * -1));
    }

    int getCaste() {
        double localMilliseconds = getLocalMilliseconds(clock.millis());
        double localDays = getLocalDays(localMilliseconds);
        return getCaste(localDays);
    }

    int getCaste(double days) {
        int year = getLocalYear(days);
        double currentDays = removeYearDays(year, days);
        if (currentDays < 1) {
            return 0;
        } else if (currentDays < 20) {
            return 1;
        } else if (currentDays < 40) {
            return 2;
        } else if (!isFestivalYear(year)) {
            if (currentDays < 60) {
                return 3;
            } else if (currentDays < 80) {
                return 4;
            } else if (currentDays < 99) {
                return 5;
            } else {
                // TODO: Throw error instead of a sentinel.
                return -1;
            }
        } else {
            if (currentDays < 61) {
                return 3;
            } else if (currentDays < 81) {
                return 4;
            } else if (currentDays < 100) {
                return 5;
            } else {
                // TODO: Throw error instead of a sentinel.
                return -1;
            }
        }
    }

    int getCasteDay() {
        double localMilliseconds = getLocalMilliseconds(clock.millis());
        double localDays = getLocalDays(localMilliseconds);
        return getCasteDay(localDays);
    }

    int getCasteDay(double days) {
        if (isFestivalDay(days)) {
            return 0;
        }

        int year = getLocalYear(days);
        double currentYearDays = removeYearDays(year, days);
        int caste = getCaste(days);

        if (caste == 1) {
            return (int) Math.floor(currentYearDays);
        } else if (caste == 2) {
            currentYearDays -= 19;
            return (int) Math.floor(currentYearDays);
        } else if (isFestivalYear(year)) {
            if (caste == 3 && currentYearDays < 51) {
                currentYearDays -= 39;
                return (int) Math.floor(currentYearDays);
            } else if (caste == 3) {
                currentYearDays -= 40;
                return (int) Math.floor(currentYearDays);
            } else if (caste == 4) {
                currentYearDays -= 60;
                return (int) Math.floor(currentYearDays);
            } else if (caste == 5) {
                currentYearDays -= 80;
                return (int) Math.floor(currentYearDays);
            } else {
                // TODO: Throw error instead of a sentinel.
                return -1;
            }
        } else {
            if (caste == 3) {
                currentYearDays -= 39;
                return (int) Math.floor(currentYearDays);
            } else if (caste == 4) {
                currentYearDays -= 59;
                return (int) Math.floor(currentYearDays);
            } else if (caste == 5) {
                currentYearDays -= 79;
                return (int) Math.floor(currentYearDays);
            } else {
                // TODO: Throw error instead of a sentinel.
                return -1;
            }
        }
    }

    double getLocalDays() {
        return getLocalDays(getLocalMilliseconds(clock.millis()));
    }

    double getLocalDays(final double localMilliseconds) {
        final double hours = localMilliseconds / 3600000;
        return hours / standardHoursPerDay;
    }

    double getLocalMilliseconds() {
        return getLocalMilliseconds(clock.millis());
    }

    double getLocalMilliseconds(final double standardMilliseconds) {
        return standardMilliseconds - epochOffset * 86400000;
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

    int getShift() {
        double localMilliseconds = getLocalMilliseconds(clock.millis());
        double localDays = getLocalDays(localMilliseconds);
        return getShift(localDays);
    }

    int getShift(double days) {
        return (int) Math.floor(days % 1 / 0.25) + 1;
    }

    double getTithe() {
        double localMilliseconds = getLocalMilliseconds(clock.millis());
        double localDays = getLocalDays(localMilliseconds);
        return getTithe(localDays);
    }

    double getTithe(double days) {
        return (days % 1 / 0.25) % 1;
    }

    boolean isFestivalDay() {
        double localMilliseconds = getLocalMilliseconds(clock.millis());
        double localDays = getLocalDays(localMilliseconds);
        return isFestivalDay(localDays);
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

    private int getDaysInYear(final int year) {
        return year % 3 == 0 && year % 51 != 0 ? 100 : 99;
    }

    private boolean isFestivalYear(final int year) {
        return year % 3 == 0 && year % 51 != 0;
    }

    private double removeYearDays(int year, double days) {
        year -= 1;
        return days - (year * 99 + Math.floorDiv(year, 3) - Math.floorDiv(year, 51));
    }

    public void setStandardHoursPerDay(double standardHoursPerDay) {
        this.standardHoursPerDay = standardHoursPerDay;
    }

    public void setEpochOffset(double epochOffset) {
        this.epochOffset = epochOffset;
    }
}

package com.senorpez.trident.api;

class FestivalYear {
    private final int localYear;
    private final boolean festivalYear;

    FestivalYear(final int localYear) {
        this.localYear = localYear;
        this.festivalYear = (localYear % 3) == 0 && (localYear % 51) != 0;
    }

    int getLocalYear() {
        return localYear;
    }

    boolean isFestivalYear() {
        return festivalYear;
    }
}

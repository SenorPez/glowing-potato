package com.senorpez.trident.api;

public enum Constant {
    G ("Newtonian constant of gravitation", "G", 6.67408e-11, "m^3*kg^-1*s^-2"),
    Msol ("Standard solar mass", "Msol", 1.9884e30, "kg"),
    Mpln ("Standard planetary mass", "Mpln", 5.9722e24, "kg"),
    Rpln ("Standard planetary equatorial radius", "Rpln", 6378136.6, "m");

    private final String name;
    private final String symbol;
    private final double value;
    private final String units;

    Constant(final String name, final String symbol, final double value, final String units) {
        this.name = name;
        this.symbol = symbol;
        this.value = value;
        this.units = units;
    }

    String getName() {
        return name;
    }

    String getSymbol() {
        return symbol;
    }

    double getValue() {
        return value;
    }

    String getUnits() {
        return units;
    }
}

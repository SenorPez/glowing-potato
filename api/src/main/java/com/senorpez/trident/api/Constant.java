package com.senorpez.trident.api;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Constant {
    G ("Newtonian constant of gravitation", "G", 6.67408e-11, "m^3*kg^-1*s^-2");

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

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getValue() {
        return value;
    }

    public String getUnits() {
        return units;
    }

    static public List<Constant> getAll() {
        return new ArrayList<>(EnumSet.allOf(Constant.class));
    }
}

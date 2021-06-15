package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

class ConstantEntity implements APIEntity<String> {
    private final String name;
    private final String symbol;
    private final double value;
    private final String units;

    ConstantEntity(final Constant constant) {
        this.name = constant.getName();
        this.symbol = constant.getSymbol();
        this.value = constant.getValue();
        this.units = constant.getUnits();
    }

    @Override
    @JsonProperty("symbol")
    public String getId() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public String getUnits() {
        return units;
    }
}

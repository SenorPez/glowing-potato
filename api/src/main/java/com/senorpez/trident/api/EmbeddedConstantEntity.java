package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;

class EmbeddedConstantEntity implements APIEntity<String> {
    private final String symbol;

    EmbeddedConstantEntity(final Constant constant) {
        this.symbol = constant.getSymbol();
    }

    @Override
    @JsonProperty("symbol")
    public String getId() {
        return symbol;
    }
}

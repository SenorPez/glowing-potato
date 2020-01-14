package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "constant", collectionRelation = "constant")
class ConstantModel implements Identifiable<String> {
    private final String name;
    private final String symbol;
    private final double value;
    private final String units;

    ConstantModel(final Constant constant) {
        this.name = constant.getName();
        this.symbol = constant.getSymbol();
        this.value = constant.getValue();
        this.units = constant.getUnits();
    }

    ConstantResource toResource() {
        final APIResourceAssembler<ConstantModel, ConstantResource> assembler =
                new APIResourceAssembler<>(
                        ConstantController.class,
                        ConstantResource.class,
                        () -> new ConstantResource(this));
        return assembler.toResource(this);
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

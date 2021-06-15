package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "constant", collectionRelation = "constant")
class ConstantModel extends RepresentationModel<ConstantModel> {
    @JsonProperty
    private String name;
    @JsonProperty
    private String symbol;
    @JsonProperty
    private double value;
    @JsonProperty
    private String units;

    public ConstantModel setName(String name) {
        this.name = name;
        return this;
    }

    public ConstantModel setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public ConstantModel setValue(double value) {
        this.value = value;
        return this;
    }

    public ConstantModel setUnits(String units) {
        this.units = units;
        return this;
    }

    static RepresentationModel<ConstantModel> toModel(final ConstantEntity content) {
        ConstantModelAssembler assembler = new ConstantModelAssembler();
        return assembler.toModel(content);
    }

    static class ConstantModelAssembler extends RepresentationModelAssemblerSupport<ConstantEntity, ConstantModel> {
        public ConstantModelAssembler() {
            super(ConstantController.class, ConstantModel.class);
        }

        @Override
        @NonNull
        public ConstantModel toModel(@NonNull ConstantEntity entity) {
            return createModelWithId(entity.getId(), entity)
                    .setName(entity.getName())
                    .setSymbol(entity.getId())
                    .setValue(entity.getValue())
                    .setUnits(entity.getUnits());
        }
    }
}

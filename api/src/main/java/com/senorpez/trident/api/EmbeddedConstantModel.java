package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

@Relation(value = "constant", collectionRelation = "constant")
class EmbeddedConstantModel extends RepresentationModel<EmbeddedConstantModel> {
    @JsonProperty
    private String symbol;

    public EmbeddedConstantModel setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    static RepresentationModel<EmbeddedConstantModel> toModel(final ConstantEntity content) {
        EmbeddedConstantModelAssembler assembler = new EmbeddedConstantModelAssembler();
        return assembler.toModel(content);
    }

    static class EmbeddedConstantModelAssembler extends RepresentationModelAssemblerSupport<ConstantEntity, EmbeddedConstantModel> {
        public EmbeddedConstantModelAssembler() {
            super(ConstantController.class, EmbeddedConstantModel.class);
        }

        @Override
        @NonNull
        public EmbeddedConstantModel toModel(@NonNull ConstantEntity entity) {
            return createModelWithId(entity.getId(), entity)
                    .setSymbol(entity.getId());
        }
    }

}

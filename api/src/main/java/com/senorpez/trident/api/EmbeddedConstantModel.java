package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.core.Relation;

@Relation(value = "constant", collectionRelation = "constant")
class EmbeddedConstantModel implements Identifiable<String> {
    private final String symbol;

    EmbeddedConstantModel(final Constant constant) {
        this.symbol = constant.getSymbol();
    }

    Resource<EmbeddedConstantModel> toResource() {
        final APIEmbeddedResourceAssembler<EmbeddedConstantModel, EmbeddedConstantResource> assembler =
                new APIEmbeddedResourceAssembler<>(
                        ConstantController.class,
                        EmbeddedConstantResource.class,
                        () -> new EmbeddedConstantResource(this));
        return assembler.toResource(this);
    }

    private class EmbeddedConstantResource extends Resource<EmbeddedConstantModel> {
        private EmbeddedConstantResource(final EmbeddedConstantModel content, final Link... links) {
            super(content, links);
        }
    }

    @Override
    @JsonProperty("symbol")
    public String getId() {
        return symbol;
    }
}

package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "constant", collectionRelation = "constant")
class ConstantModel extends RepresentationModel<ConstantModel> {
    ConstantModel(final ConstantEntity content) {
        final APIResourceAssembler<ConstantEntity, ConstantModel> assembler = new APIResourceAssembler<>(
                ConstantController.class,
                ConstantModel.class,
                () -> new ConstantModel(content)
        );
        assembler.toModel(content);
        this.add(linkTo(methodOn(ConstantController.class).constants()).withRel("constants"));
    }

    ConstantModel(final EmbeddedConstantEntity content) {
        final APIResourceAssembler<EmbeddedConstantEntity, ConstantModel> assembler = new APIResourceAssembler<>(
                ConstantController.class,
                ConstantModel.class,
                () -> new ConstantModel(content)
        );
        assembler.toModel(content);
    }
}

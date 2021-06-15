package com.senorpez.trident.api;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import java.util.function.Supplier;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class APIResourceAssembler<E extends APIEntity<?>, M extends RepresentationModel<M>> extends RepresentationModelAssemblerSupport<E, M> {
    public APIResourceAssembler(Class<?> controllerClass, Class<M> resourceType, Supplier<M> supplier) {
        super(controllerClass, resourceType);
        this.supplier = supplier;
    }

    private final Supplier<M> supplier;

    @Override
    @NonNull
    public M toModel(@NonNull E entity) {
        final M model = createModelWithId(entity.getId(), entity);
        return addIndexLink(model);
    }

    M toModel(E entity, Object... parameters) {
        final M model = createModelWithId(entity.getId(), entity, parameters);
        return addIndexLink(model);
    }

    @Override
    @NonNull
    protected M instantiateModel(@NonNull E entity) {
        return supplier.get();
    }

    private M addIndexLink(final M model) {
        model.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return model;
    }
}

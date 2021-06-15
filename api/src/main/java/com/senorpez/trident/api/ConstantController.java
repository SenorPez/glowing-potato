package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.IanaLinkRelations.INDEX;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(
        value = "/constants",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_VALUE}
)
@RestController
class ConstantController {
    private final APIService apiService;
    private final static EnumSet<Constant> CONSTANTS = EnumSet.allOf(Constant.class);

    @Autowired
    ConstantController(final APIService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping
    ResponseEntity<CollectionModel<RepresentationModel<EmbeddedConstantModel>>> constants() {
        final Collection<ConstantEntity> constantEntities = CONSTANTS
                .stream()
                .map(ConstantEntity::new)
                .collect(Collectors.toList());
        final Collection<RepresentationModel<EmbeddedConstantModel>> constantModels = constantEntities
                .stream()
                .map(EmbeddedConstantModel::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(
                constantModels,
                linkTo(methodOn(ConstantController.class).constants()).withSelfRel(),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)));
    }

    @RequestMapping("/{symbol}")
    ResponseEntity<RepresentationModel<ConstantModel>> constants(@PathVariable final String symbol) {
        final Constant constant = apiService.findOne(
                CONSTANTS,
                findConstant -> findConstant.getSymbol().equals(symbol),
                () -> new ConstantNotFoundException(symbol));
        final ConstantEntity constantEntity = new ConstantEntity(constant);
        final RepresentationModel<ConstantModel> constantModel = ConstantModel.toModel(constantEntity);
        constantModel.add(
                linkTo(methodOn(ConstantController.class).constants()).withRel("constants"),
                linkTo(methodOn(RootController.class).root()).withRel(INDEX)
        );
        return ResponseEntity.ok(constantModel);
    }
}

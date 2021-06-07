package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
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
    ResponseEntity<CollectionModel<ConstantModel>> constants() {
        final CollectionModel<ConstantModel> constantModels = CollectionModel.of(CONSTANTS
                .stream()
                .map(EmbeddedConstantEntity::new)
                .map(ConstantModel::new)
                .collect(Collectors.toList())
        );
        return ResponseEntity.ok(constantModels);
    }

    @RequestMapping("/{symbol}")
    ResponseEntity<ConstantModel> constants(@PathVariable final String symbol) {
        final Constant constant = apiService.findOne(
                CONSTANTS,
                findConstant -> findConstant.getSymbol().equals(symbol),
                () -> new ConstantNotFoundException(symbol));
        final ConstantEntity constantEntity = new ConstantEntity(constant);
        final ConstantModel constantModel = new ConstantModel(constantEntity);
        return ResponseEntity.ok(constantModel);
    }
}

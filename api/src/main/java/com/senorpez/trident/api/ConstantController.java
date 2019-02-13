package com.senorpez.trident.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RequestMapping(
        value = "/constants",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, APPLICATION_JSON_UTF8_VALUE}
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
    ResponseEntity<EmbeddedConstantResources> constants() {
        final Collection<EmbeddedConstantModel> constantModels = CONSTANTS.stream()
                .map(EmbeddedConstantModel::new)
                .collect(Collectors.toList());
        final Collection<Resource<EmbeddedConstantModel>> constantResources = constantModels.stream()
                .map(EmbeddedConstantModel::toResource)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new EmbeddedConstantResources(constantResources));
    }

    @RequestMapping("/{symbol}")
    ResponseEntity<ConstantResource> constants(@PathVariable final String symbol) {
        final Constant constant = apiService.findOne(
                CONSTANTS,
                findConstant -> findConstant.getSymbol().equals(symbol),
                () -> new StarNotFoundException(1));
        final ConstantModel constantModel = new ConstantModel(constant);
        final ConstantResource constantResource = constantModel.toResource();
        return ResponseEntity.ok(constantResource);
    }
}

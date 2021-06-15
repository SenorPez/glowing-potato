package com.senorpez.trident.api;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.senorpez.trident.api.SupportedMediaTypes.FALLBACK_VALUE;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(
        value = "/",
        method = RequestMethod.GET,
        produces = {TRIDENT_API_VALUE, FALLBACK_VALUE}
)
@RestController
class RootController {
    @RequestMapping
    ResponseEntity<RootObject> root() {
        final RootObject root = new RootObject();
        root.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        root.add(linkTo(methodOn(RootController.class).root()).withRel(IanaLinkRelations.INDEX));
        root.add(linkTo(methodOn(ConstantController.class).constants()).withRel("constants"));
        root.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel("systems"));
        return ResponseEntity.ok(root);
    }

    private static class RootObject extends RepresentationModel<RootObject> {
    }
}

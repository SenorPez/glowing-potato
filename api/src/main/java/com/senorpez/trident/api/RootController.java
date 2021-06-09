package com.senorpez.trident.api;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
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
        method = RequestMethod.GET
)
@RestController
class RootController {
//    @RequestMapping(produces = {TRIDENT_API_VALUE, FALLBACK_VALUE})
//    ResponseEntity<EntityModel<Object>> root() {
//        HalModelBuilder.emptyHalModel().build();
//    }


    ResponseEntity<RootObject> root() {
        return ResponseEntity.ok(new RootObject());
    }
//    ResponseEntity<EntityModel<Object>> root() {
//        final EntityModel<Object> root = EntityModel.of(new Object());
//        root.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
//        root.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
//        root.add(linkTo(methodOn(ConstantController.class).constants()).withRel("constants"));
//        root.add(linkTo(methodOn(SolarSystemController.class).solarSystems()).withRel("systems"));
//        return ResponseEntity.ok(root);
//    }

    private static class RootObject {
        public int number;
        public String name;

        public RootObject() {
            this.number = 1;
            this.name = "Hi";
        }
    }
}

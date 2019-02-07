package com.senorpez.trident.api;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.senorpez.trident.api.SupportedMediaTypes.FALLBACK_VALUE;
import static com.senorpez.trident.api.SupportedMediaTypes.TRIDENT_API_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RequestMapping(
        value = "/",
        method = RequestMethod.GET
)
@RestController
public class RootController {
    @RequestMapping(produces = {TRIDENT_API_VALUE, FALLBACK_VALUE})
    ResponseEntity<ResourceSupport> root() {
        final ResourceSupport root = new ResourceSupport();
        root.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        root.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return ResponseEntity.ok(root);
    }
}

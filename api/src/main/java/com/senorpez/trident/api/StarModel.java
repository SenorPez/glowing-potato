package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Relation(value = "star", collectionRelation = "star")
class StarModel extends RepresentationModel<StarModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private float mass;

    @JsonProperty
    private Float semimajorAxis;
    @JsonProperty
    private Float eccentricity;
    @JsonProperty
    private Float inclination;
    @JsonProperty
    private Float longitudeOfAscendingNode;
    @JsonProperty
    private Float argumentOfPeriapsis;
    @JsonProperty
    private Float trueAnomalyAtEpoch;

    public StarModel setId(int id) {
        this.id = id;
        return this;
    }

    public StarModel setName(String name) {
        this.name = name;
        return this;
    }

    public StarModel setMass(float mass) {
        this.mass = mass;
        return this;
    }

    public StarModel setSemimajorAxis(Float semimajorAxis) {
        this.semimajorAxis = semimajorAxis;
        return this;
    }

    public StarModel setEccentricity(Float eccentricity) {
        this.eccentricity = eccentricity;
        return this;
    }

    public StarModel setInclination(Float inclination) {
        this.inclination = inclination;
        return this;
    }

    public StarModel setLongitudeOfAscendingNode(Float longitudeOfAscendingNode) {
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        return this;
    }

    public StarModel setArgumentOfPeriapsis(Float argumentOfPeriapsis) {
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        return this;
    }

    public StarModel setTrueAnomalyAtEpoch(Float trueAnomalyAtEpoch) {
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;
        return this;
    }

    static RepresentationModel<StarModel> toModel(final StarEntity content, final int solarSystemId) {
        StarModelAssembler assembler = new StarModelAssembler();
        return assembler.toModel(content, solarSystemId);
    }

    static class StarModelAssembler extends RepresentationModelAssemblerSupport<StarEntity, StarModel> {
        public StarModelAssembler() {
            super(StarController.class, StarModel.class);
        }

        @Override
        @NonNull
        public StarModel toModel(@NonNull StarEntity entity) {
            throw new NotImplementedException();
        }

        public StarModel toModel(StarEntity entity, final int solarSystemId) {
            return createModelWithId(entity.getId(), entity, solarSystemId)
                    .setId(entity.getId())
                    .setName(entity.getName())
                    .setMass(entity.getMass())
                    .setSemimajorAxis(entity.getSemimajorAxis())
                    .setEccentricity(entity.getEccentricity())
                    .setInclination(entity.getInclination())
                    .setLongitudeOfAscendingNode(entity.getLongitudeOfAscendingNode())
                    .setArgumentOfPeriapsis(entity.getArgumentOfPeriapsis())
                    .setTrueAnomalyAtEpoch(entity.getTrueAnomalyAtEpoch());
        }
    }
}

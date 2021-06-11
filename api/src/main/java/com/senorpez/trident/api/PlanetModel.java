package com.senorpez.trident.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Relation(value = "planet", collectionRelation = "planet")
class PlanetModel extends RepresentationModel<PlanetModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private float mass;
    @JsonProperty
    private float radius;

    @JsonProperty
    private float semimajorAxis;
    @JsonProperty
    private float eccentricity;
    @JsonProperty
    private float inclination;
    @JsonProperty
    private float longitudeOfAscendingNode;
    @JsonProperty
    private float argumentOfPeriapsis;
    @JsonProperty
    private float trueAnomalyAtEpoch;

    public PlanetModel setId(int id) {
        this.id = id;
        return this;
    }

    public PlanetModel setName(String name) {
        this.name = name;
        return this;
    }

    public PlanetModel setMass(float mass) {
        this.mass = mass;
        return this;
    }

    public PlanetModel setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public PlanetModel setSemimajorAxis(float semimajorAxis) {
        this.semimajorAxis = semimajorAxis;
        return this;
    }

    public PlanetModel setEccentricity(float eccentricity) {
        this.eccentricity = eccentricity;
        return this;
    }

    public PlanetModel setInclination(float inclination) {
        this.inclination = inclination;
        return this;
    }

    public PlanetModel setLongitudeOfAscendingNode(float longitudeOfAscendingNode) {
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        return this;
    }

    public PlanetModel setArgumentOfPeriapsis(float argumentOfPeriapsis) {
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        return this;
    }

    public PlanetModel setTrueAnomalyAtEpoch(float trueAnomalyAtEpoch) {
        this.trueAnomalyAtEpoch = trueAnomalyAtEpoch;
        return this;
    }

    static RepresentationModel<PlanetModel> toModel(final PlanetEntity content, final int solarSystemId, final int starId) {
        PlanetModelAssembler assembler = new PlanetModelAssembler();
        return assembler.toModel(content, solarSystemId, starId);
    }

    static class PlanetModelAssembler extends RepresentationModelAssemblerSupport<PlanetEntity, PlanetModel> {
        public PlanetModelAssembler() {
            super(PlanetController.class, PlanetModel.class);
        }

        @Override
        @NonNull
        public PlanetModel toModel(@NonNull PlanetEntity entity) {
            throw new NotImplementedException();
        }

        public PlanetModel toModel(final PlanetEntity entity, final int solarSystemId, final int starId) {
            return createModelWithId(entity.getId(), entity, solarSystemId, starId)
                    .setId(entity.getId())
                    .setName(entity.getName())
                    .setMass(entity.getMass())
                    .setRadius(entity.getRadius())
                    .setSemimajorAxis(entity.getSemimajorAxis())
                    .setEccentricity(entity.getEccentricity())
                    .setInclination(entity.getInclination())
                    .setLongitudeOfAscendingNode(entity.getLongitudeOfAscendingNode())
                    .setArgumentOfPeriapsis(entity.getArgumentOfPeriapsis())
                    .setTrueAnomalyAtEpoch(entity.getTrueAnomalyAtEpoch());
        }
    }
}

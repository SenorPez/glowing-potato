package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "star", collectionRelation = "star")
public class StarModel implements Identifiable<Integer> {
    private final int id;
    private final String name;
    private final float mass;

    private final Float semimajorAxis;
    private final Float eccentricity;
    private final Float inclination;
    private final Float longitudeOfAscendingNode;
    private final Float argumentOfPeriapsis;
    private final Float trueAnomalyAtEpoch;

    StarModel(final Star star) {
        this.id = star.getId();
        this.name = star.getName();
        this.mass = star.getMass();

        this.semimajorAxis = star.getSemimajorAxis();
        this.eccentricity = star.getEccentricity();
        this.inclination = star.getInclination();
        this.longitudeOfAscendingNode = star.getLongitudeOfAscendingNode();
        this.argumentOfPeriapsis = star.getArgumentOfPeriapsis();
        this.trueAnomalyAtEpoch = star.getTrueAnomalyAtEpoch();
    }

    StarResource toResource(final int solarSystemId) {
        final APIResourceAssembler<StarModel, StarResource> assembler = new APIResourceAssembler<>(
                StarController.class,
                StarResource.class,
                () -> new StarResource(this, solarSystemId));
        return assembler.toResource(this, solarSystemId);
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getMass() {
        return mass;
    }

    public Float getSemimajorAxis() {
        return semimajorAxis;
    }

    public Float getEccentricity() {
        return eccentricity;
    }

    public Float getInclination() {
        return inclination;
    }

    public Float getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    public Float getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    public Float getTrueAnomalyAtEpoch() {
        return trueAnomalyAtEpoch;
    }
}

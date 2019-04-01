package com.senorpez.trident.api;

import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "planet", collectionRelation = "planet")
class PlanetModel implements Identifiable<Integer> {
    private final int id;
    private final String name;

    private final float mass;
    private final float radius;

    private final float semimajorAxis;
    private final float eccentricity;
    private final float inclination;
    private final float longitudeOfAscendingNode;
    private final float argumentOfPeriapsis;
    private final float trueAnomalyAtEpoch;

    PlanetModel(final Planet planet) {
        this.id = planet.getId();
        this.name = planet.getName();
        this.mass = planet.getMass();
        this.radius = planet.getRadius();
        this.semimajorAxis = planet.getSemimajorAxis();
        this.eccentricity = planet.getEccentricity();
        this.inclination = planet.getInclination();
        this.longitudeOfAscendingNode = planet.getLongitudeOfAscendingNode();
        this.argumentOfPeriapsis = planet.getArgumentOfPeriapsis();
        this.trueAnomalyAtEpoch = planet.getTrueAnomalyAtEpoch();
    }

    PlanetResource toResource(final int solarSystemId, final int starId) {
        final APIResourceAssembler<PlanetModel, PlanetResource> assembler = new APIResourceAssembler<>(
                PlanetController.class,
                PlanetResource.class,
                () -> new PlanetResource(this, solarSystemId, starId));
        return assembler.toResource(this, solarSystemId, starId);
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

    public float getRadius() {
        return radius;
    }

    public float getSemimajorAxis() {
        return semimajorAxis;
    }

    public float getEccentricity() {
        return eccentricity;
    }

    public float getInclination() {
        return inclination;
    }

    public float getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    public float getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    public float getTrueAnomalyAtEpoch() {
        return trueAnomalyAtEpoch;
    }
}

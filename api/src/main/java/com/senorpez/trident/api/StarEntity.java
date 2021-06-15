package com.senorpez.trident.api;

class StarEntity implements APIEntity<Integer> {
    private final int id;
    private final String name;
    private final float mass;

    private final Float semimajorAxis;
    private final Float eccentricity;
    private final Float inclination;
    private final Float longitudeOfAscendingNode;
    private final Float argumentOfPeriapsis;
    private final Float trueAnomalyAtEpoch;

    StarEntity(final Star star) {
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
